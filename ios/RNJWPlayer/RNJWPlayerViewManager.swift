import AVFoundation
import React
import JWPlayerKit

@objc(RNJWPlayerViewManager)
class RNJWPlayerViewManager: RCTViewManager {
    
    override func view() -> UIView {
        return RNJWPlayerView()
    }
    
    func methodQueue() -> DispatchQueue {
        return bridge.uiManager.methodQueue
    }
    
    override class func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    /// Looks up a React-managed native view of the given type: the UIManager covers Fabric,
    /// bridgeless and Paper, and the Paper view registry is the fallback for views it misses.
    private func reactView<T: UIView>(forTag reactTag: NSNumber, as type: T.Type = T.self) -> T? {
        guard let uiManager = self.bridge?.uiManager else { return nil }
        if let view = uiManager.view(forReactTag: reactTag) as? T {
            return view
        }
        if let viewRegistry = uiManager.value(forKey: "viewRegistry") as? [NSNumber: UIView] {
            return viewRegistry[reactTag] as? T
        }
        return nil
    }

    private func getPlayerView(reactTag: NSNumber, logFailure: Bool = true) -> RNJWPlayerView? {
        guard self.bridge != nil else {
            print("❌ RNJWPlayerViewManager: Bridge is nil")
            return nil
        }

        if let view = reactView(forTag: reactTag, as: RNJWPlayerView.self) {
            return view
        }

        if logFailure {
            print("❌ Invalid view returned for tag \(reactTag)")
        }
        return nil
    }

    private static let playerLookupAttempts = 20

    /// Resolves React tags to their native views. A view mounted just before the call that
    /// registers it (for example from its own mount effect) may not be in the view registry yet
    /// on the New Architecture, so missing tags are retried every 50ms (about 1s in total).
    /// Tags still missing after that are left out, typically a flattened layout-only view.
    private func resolveReactViews(_ tags: [NSNumber], attempt: Int = 0, found: [NSNumber: UIView] = [:], _ completion: @escaping ([NSNumber: UIView]) -> Void) {
        var found = found
        for tag in tags where found[tag] == nil {
            found[tag] = reactView(forTag: tag, as: UIView.self)
        }
        if found.count == Set(tags).count || attempt >= Self.playerLookupAttempts - 1 {
            completion(found)
            return
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            self.resolveReactViews(tags, attempt: attempt + 1, found: found, completion)
        }
    }

    /// Runs `body` on the main queue with the RNJWPlayerView for `reactTag`, or with nil if it
    /// never shows up. A call made from an effect that runs when the player mounts can arrive
    /// before the view exists: on the New Architecture the interop layer creates it a moment
    /// after mount. So the lookup is retried every 50ms (about 1s in total) before giving up.
    private func withPlayerView(_ reactTag: NSNumber, _ body: @escaping (RNJWPlayerView?) -> Void) {
        DispatchQueue.main.async {
            self.lookUpPlayerView(reactTag, attempt: 0, body)
        }
    }

    private func lookUpPlayerView(_ reactTag: NSNumber, attempt: Int, _ body: @escaping (RNJWPlayerView?) -> Void) {
        let isLastAttempt = attempt >= Self.playerLookupAttempts - 1
        if let view = getPlayerView(reactTag: reactTag, logFailure: isLastAttempt) {
            body(view)
            return
        }
        guard !isLastAttempt else {
            body(nil)
            return
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            self.lookUpPlayerView(reactTag, attempt: attempt + 1, body)
        }
    }
    
    @objc func state(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: nil)
                reject("no_player", "There is no playerViewController or playerView", error)
                return
            }
            
            if let playerViewController = view.playerViewController {
                resolve(NSNumber(value: playerViewController.player.getState().rawValue))
            } else if let playerView = view.playerView {
                resolve(NSNumber(value: playerView.player.getState().rawValue))
            } else {
                let error = NSError(domain: "", code: 0, userInfo: nil)
                reject("no_player", "There is no playerView", error)
            }
        }
    }
    
    @objc func pause(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("❌ Failed to pause: RNJWPlayerView not found for tag \(reactTag)")
                return
            }
            
            view.userPaused = true
            if let playerView = view.playerView {
                playerView.player.pause()
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.pause()
            }
        }
    }
    
    @objc func play(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("❌ Failed to play: RNJWPlayerView not found for tag \(reactTag)")
                return
            }
            
            view.userPaused = false
            if let playerView = view.playerView {
                playerView.player.play()
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.play()
            }
        }
    }
    
    @objc func stop(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("❌ Failed to stop: RNJWPlayerView not found for tag \(reactTag)")
                return
            }
            
            view.userPaused = true
            if let playerView = view.playerView {
                playerView.player.stop()
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.stop()
            }
        }
    }
    
    @objc func position(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no playerView"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            if let playerView = view.playerView {
                resolve(playerView.player.time.position as NSNumber)
            } else if let playerViewController = view.playerViewController {
                resolve(playerViewController.player.time.position as NSNumber)
            } else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no playerView"])
                reject("no_player", "There is no playerView", error)
            }
        }
    }
    
    @objc func toggleSpeed(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView.")
                return
            }
            
            if let playerView = view.playerView {
                if playerView.player.playbackRate < 2.0 {
                    playerView.player.playbackRate += 0.5
                } else {
                    playerView.player.playbackRate = 0.5
                }
            } else if let playerViewController = view.playerViewController {
                if playerViewController.player.playbackRate < 2.0 {
                    playerViewController.player.playbackRate += 0.5
                } else {
                    playerViewController.player.playbackRate = 0.5
                }
            }
        }
    }
    
    @objc func setSpeed(_ reactTag: NSNumber, _ speed: Double) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerView = view.playerView {
                playerView.player.playbackRate = speed
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.playbackRate = speed
            }
        }
    }
    
    @objc func setPlaylistIndex(_ reactTag: NSNumber, _ index: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerView = view.playerView {
                playerView.player.loadPlayerItemAt(index: index.intValue)
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.loadPlayerItemAt(index: index.intValue)
            }
        }
    }
    
    @objc func seekTo(_ reactTag: NSNumber, _ time: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerView = view.playerView {
                playerView.player.seek(to: TimeInterval(time.intValue))
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.seek(to: TimeInterval(time.intValue))
            }
        }
    }
    
    @objc func setVolume(_ reactTag: NSNumber, _ volume: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerView = view.playerView {
                playerView.player.volume = volume.doubleValue
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.volume = volume.doubleValue
            }
        }
    }
    
    @objc func togglePIP(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view, let pipController = view.playerView?.pictureInPictureController else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if pipController.isPictureInPicturePossible {
                if pipController.isPictureInPictureActive {
                    pipController.stopPictureInPicture()
                } else {
                    pipController.startPictureInPicture()
                }
            }
        }
    }

    @objc func resolveNextPlaylistItem(_ reactTag: NSNumber, _ playlistItem: NSDictionary) {
        self.bridge.uiManager.addUIBlock { uiManager, viewRegistry in
            guard let view = self.getPlayerView(reactTag: reactTag) else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let completion = view.onBeforeNextPlaylistItemCompletion {
                guard let itemDict = playlistItem as? [String: Any] else {
                    print("Error: resolveNextPlaylistItem received invalid playlist item data")
                    completion(nil)
                    view.onBeforeNextPlaylistItemCompletion = nil
                    return
                }
                do {
                    let item = try view.getPlayerItem(item: itemDict)
                    // Ensure completion runs on the main thread — the SDK performs
                    // UI updates (view hierarchy changes) when loading the next item.
                    DispatchQueue.main.async {
                        completion(item)
                        view.onBeforeNextPlaylistItemCompletion = nil

                        // Apply any config change that was deferred while the callback was pending
                        if let pendingConfig = view.pendingConfigAfterPlaylistItemCallback {
                            view.pendingConfigAfterPlaylistItemCallback = nil
                            view.setConfig(pendingConfig)
                        }
                    }
                } catch {
                    print("Error creating JWPlayerItem: \(error)")
                    view.onBeforeNextPlaylistItemCompletion = nil
                    if let pendingConfig = view.pendingConfigAfterPlaylistItemCallback {
                        view.pendingConfigAfterPlaylistItemCallback = nil
                        view.setConfig(pendingConfig)
                    }
                }
            } else {
                print("Warning: resolveNextPlaylistItem called but no completion handler was set OR completion handler was already called")
            }
        }
    }

#if USE_GOOGLE_CAST
    @objc func setUpCastController(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            view.setUpCastController()
        }
    }
    
    @objc func presentCastDialog(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            view.presentCastDialog()
        }
    }
    
    @objc func connectedDevice(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            if let device = view.connectedDevice() {
                var dict = [String: Any]()
                dict["name"] = device.name
                dict["identifier"] = device.identifier
                
                do {
                    let data = try JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted)
                    resolve(String(data: data, encoding: .utf8))
                } catch {
                    reject("json_error", "Failed to serialize JSON", error)
                }
            } else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no connected device"])
                reject("no_connected_device", "There is no connected device", error)
            }
        }
    }
    
    @objc func availableDevices(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            if let availableDevices = view.getAvailableDevices() {
                var devicesInfo: [[String: Any]] = []
                
                for device in availableDevices {
                    var dict = [String: Any]()
                    dict["name"] = device.name
                    dict["identifier"] = device.identifier
                    devicesInfo.append(dict)
                }
                
                do {
                    let data = try JSONSerialization.data(withJSONObject: devicesInfo, options: .prettyPrinted)
                    resolve(String(data: data, encoding: .utf8))
                } catch {
                    reject("json_error", "Failed to serialize JSON", error)
                }
            } else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There are no available devices"])
                reject("no_available_device", "There are no available devices", error)
            }
        }
    }
    
    @objc func castState(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            resolve(view.castState)
        }
    }
#endif
    
    @objc func getAudioTracks(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            let audioTracks: [JWMediaSelectionOption]? = view.playerView?.player.audioTracks ?? view.playerViewController?.player.audioTracks
        
            // V4 tracks object instead of the V3 JSON object of old
            if let audioTracks = audioTracks {
                var results: [[String: Any]] = []
                for track in audioTracks {
                    let track = track as JWMediaSelectionOption
                        let trackDict: [String: Any] = [
                            "language": track.extendedLanguageTag ?? "UNKNOWN", // Intentionally defaulting to a non-spec language if none found
//                            "autoSelect": autoSelect, // not available in V4
                            "defaultTrack": track.defaultOption,
                            "name": track.name
//                            "groupId": groupId // not available in V4
                        ]
                        results.append(trackDict)
                    }
                resolve(results)
            } else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There are no audio tracks"])
                reject("no_audio_tracks", "There are no audio tracks", error)
            }
        }
    }
    
    @objc func getCurrentAudioTrack(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            if let playerView = view.playerView {
                resolve(NSNumber(value: playerView.player.currentAudioTrack))
            } else if let playerViewController = view.playerViewController {
                resolve(NSNumber(value: playerViewController.player.currentAudioTrack))
            } else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "There is no player", error)
            }
        }
    }
    
    @objc func setCurrentAudioTrack(_ reactTag: NSNumber, _ index: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerView = view.playerView {
                playerView.player.currentAudioTrack = index.intValue
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.currentAudioTrack = index.intValue
            }
        }
    }
    
    @objc func setControls(_ reactTag: NSNumber, _ show: Bool) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerViewController = view.playerViewController {
                view.toggleUIGroup(view: playerViewController.view, name: "JWPlayerKit.InterfaceView", ofSubview: nil, show: show)
            }
        }
    }
    
    @objc func setVisibility(_ reactTag: NSNumber, _ visibility: Bool, _ controls: [String]) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if view.playerViewController != nil {
                view.setVisibility(isVisible: visibility, forControls: controls)
            }
        }
    }
    
    @objc func setLockScreenControls(_ reactTag: NSNumber, _ show: Bool) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerViewController = view.playerViewController {
                playerViewController.enableLockScreenControls = show
            }
        }
    }
    
    @objc func setCurrentCaptions(_ reactTag: NSNumber, _ index: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            do {
                if let playerView = view.playerView {
                    try playerView.player.setCaptionTrack(index: index.intValue)
                } else if let playerViewController = view.playerViewController {
                    try playerViewController.player.setCaptionTrack(index: index.intValue)
                }
            } catch {
                print("Error setting caption track: \(error)")
            }
        }
    }
    
    @objc func getCurrentCaptions(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            if let playerView = view.playerView {
                resolve(NSNumber(value: playerView.player.currentCaptionsTrack))
            } else if let playerViewController = view.playerViewController {
                resolve(NSNumber(value: playerViewController.player.currentCaptionsTrack))
            } else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "There is no player", error)
            }
        }
    }
    
    @objc func setLicenseKey(_ reactTag: NSNumber, _ license: String) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            view.setLicense(license: license)
        }
    }
    
    private func performActionOnAllPlayers(_ action: @escaping (RNJWPlayerView) -> Void) {
        DispatchQueue.main.async {
            guard let bridge = self.bridge else { return }
            
            let uiManager = bridge.uiManager
            if let viewRegistry = uiManager?.value(forKey: "viewRegistry") as? [NSNumber: UIView] {
                for (reactTag, view) in viewRegistry {
                    if let playerView = self.getPlayerView(reactTag: reactTag) {
                        action(playerView)
                    }
                }
            }
        }
    }

    @objc func quite() {
        performActionOnAllPlayers { view in
            if let playerView = view.playerView {
                playerView.player.pause()
                playerView.player.stop()
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.pause()
                playerViewController.player.stop()
            }
        }
    }

    @objc func reset() {
        performActionOnAllPlayers { view in
            view.startDeinitProcess()
        }
    }

    @objc func loadPlaylist(_ reactTag: NSNumber, _ playlist: Any) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            var playlistArray = [JWPlayerItem]()
            
            if let playlistArrayInput = playlist as? [[String: Any]] {
                for item in playlistArrayInput {
                    if let playerItem = try? view.getPlayerItem(item: item) {
                        playlistArray.append(playerItem)
                    }
                }
                
                if let playerView = view.playerView {
                    playerView.player.loadPlaylist(items: playlistArray)
                } else if let playerViewController = view.playerViewController {
                    playerViewController.player.loadPlaylist(items: playlistArray)
                }
            }
        }
    }
    
    @objc func recreatePlayerWithConfig(_ reactTag: NSNumber, _ config: NSDictionary) {
        DispatchQueue.main.async {
            guard let view = self.bridge?.uiManager.view(
                forReactTag: reactTag
            ) as? RNJWPlayerView else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            view.recreatePlayerWithConfig(config as? [String: Any] ?? [:])
        }
    }

    @objc func loadPlaylistWithUrl(_ reactTag: NSNumber, _ playlistString: String) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
                        
            if let url = URL(string: playlistString) {
                if let playerView = view.playerView {
                    playerView.player.loadPlaylist(url: url)
                } else if let playerViewController = view.playerViewController {
                    playerViewController.player.loadPlaylist(url: url)
                }
            }
        }
    }
    
    @objc func setFullscreen(_ reactTag: NSNumber, _ fullscreen: Bool) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerViewController = view.playerViewController {
                if fullscreen {
                    playerViewController.transitionToFullScreen(animated: true, completion: nil)
                } else {
                    playerViewController.dismissFullScreen(animated: true, completion: nil)
                }
            } else {
                print("Invalid view returned from registry, expecting RNJWPlayerViewController")
            }
        }
    }

    // The `refreshNotification` parameter is part of the JS API for parity with an
    // Android-side workaround. On iOS JWPlayerKit already refreshes MPNowPlayingInfoCenter /
    // the Control Center via LockScreenControlsHandler when updateItemMetadata is called,
    // so this flag is accepted and ignored here.
    @objc func setPlaylistItemMetadata(_ reactTag: NSNumber, _ title: String?, _ description: String?, _ image: String?, _ refreshNotification: Bool) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }

            let posterURL = image.flatMap { URL(string: $0) }
            var updatedItem: JWPlayerItem?

            if let playerView = view.playerView {
                playerView.player.updateItemMetadata(title: title, description: description, posterImage: posterURL)
                updatedItem = playerView.player.currentItem
            } else if let playerViewController = view.playerViewController {
                playerViewController.player.updateItemMetadata(title: title, description: description, posterImage: posterURL)
                updatedItem = playerViewController.player.currentItem
            } else {
                return
            }

            guard let item = updatedItem else { return }
            do {
                let data = try JSONSerialization.data(withJSONObject: item.toJSONObject(), options: .prettyPrinted)
                view.onPlaylistItemMetadataChanged?([
                    "playlistItem": String(data: data, encoding: .utf8) as Any,
                    "index": view.currentPlayingIndex
                ])
            } catch {
                print("Error serializing updated playlist item: \(error)")
            }
        }
    }

    // MARK: - Friendly obstructions

    /// Resolves each React tag to its native view and registers it as a friendly obstruction.
    /// Resolves with `{ failed: [{ tag, reason }] }` so JS can say which refs could not be used.
    @objc func registerFriendlyObstructions(_ reactTag: NSNumber, _ obstructions: [[String: Any]], _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                reject("no_player", "RNJWPlayerView not found for tag \(reactTag)", nil)
                return
            }

            let tags = obstructions.compactMap { $0["tag"] as? NSNumber }
            self.resolveReactViews(tags) { targets in
                var resolved: [NSNumber: RNJWPlayerView.AppFriendlyObstruction] = [:]
                var failed: [[String: Any]] = []
                for entry in obstructions {
                    guard let tag = entry["tag"] as? NSNumber else { continue }
                    // A layout-only Fabric <View> is flattened away and has no native view: use collapsable={false}.
                    guard let target = targets[tag] else {
                        failed.append(["tag": tag, "reason": "notFound"])
                        continue
                    }
                    // Declaring the player (or anything containing it) friendly would hide real
                    // obstructions from the viewability vendor. Overlays rendered as children of
                    // the player are fine: they sit beside the ad surface like the SDK's own controls.
                    if view.isDescendant(of: target) {
                        failed.append(["tag": tag, "reason": "containsPlayer"])
                        continue
                    }
                    let purpose = RCTConvert.JWFriendlyObstructionPurpose(entry["purpose"] as? String)
                    // The SDK silently drops a `.notVisible` obstruction whose view is on screen, since
                    // OMID would then treat the whole view as covering the ad. Report it instead.
                    if purpose == .notVisible, !(target.isHidden || target.alpha <= 0) {
                        failed.append(["tag": tag, "reason": "visible"])
                        continue
                    }
                    let reason = RNJWPlayerView.sanitizedObstructionReason(entry["reason"] as? String)
                    let obstruction = JWFriendlyObstruction(view: target, purpose: purpose, reason: reason)
                    resolved[tag] = RNJWPlayerView.AppFriendlyObstruction(obstruction: obstruction, view: target)
                }

                view.registerFriendlyObstructions(resolved)
                resolve(["failed": failed])
            }
        }
    }

    @objc func deregisterFriendlyObstructions(_ reactTag: NSNumber, _ tags: [NSNumber]) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("❌ Failed to deregister friendly obstructions: RNJWPlayerView not found for tag \(reactTag)")
                return
            }
            view.deregisterFriendlyObstructions(tags: tags)
        }
    }

    @objc func deregisterAllFriendlyObstructions(_ reactTag: NSNumber) {
        self.withPlayerView(reactTag) { view in
            guard let view = view else {
                print("❌ Failed to deregister friendly obstructions: RNJWPlayerView not found for tag \(reactTag)")
                return
            }
            view.deregisterAllFriendlyObstructions()
        }
    }

}
