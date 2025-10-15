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
    
    private func getPlayerView(reactTag: NSNumber) -> RNJWPlayerView? {
        guard let bridge = self.bridge else {
            print("❌ RNJWPlayerViewManager: Bridge is nil")
            return nil
        }
        
        let uiManager = bridge.uiManager
        
        // ✅ Check Fabric first (New Architecture)
        if let view = uiManager?.view(forReactTag: reactTag) as? RNJWPlayerView {
            return view
        }
        
        // ✅ Check Legacy (Old Architecture) and add explicit type annotation
        if let viewRegistry = uiManager?.value(forKey: "viewRegistry") as? [NSNumber: UIView],
           let view = viewRegistry[reactTag] as? RNJWPlayerView {
            return view
        }
        
        print("❌ Invalid view returned for tag \(reactTag)")
        return nil
    }
    
    @objc func state(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag), let pipController = view.playerView?.pictureInPictureController else {
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
                do {
                    let item = try view.getPlayerItem(item: playlistItem as! [String: Any])
                    completion(item)
                } catch {
                    print("Error creating JWPlayerItem: \(error)")
                }
                view.onBeforeNextPlaylistItemCompletion = nil
            } else {
                print("Warning: resolveNextPlaylistItem called but no completion handler was set OR completion handler was already called")
            }
        }
    }

#if USE_GOOGLE_CAST
    @objc func setUpCastController(_ reactTag: NSNumber) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            view.setUpCastController()
        }
    }
    
    @objc func presentCastDialog(_ reactTag: NSNumber) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            view.presentCastDialog()
        }
    }
    
    @objc func connectedDevice(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
                let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "There is no player"])
                reject("no_player", "Invalid view returned from registry, expecting RNJWPlayerView", error)
                return
            }
            
            resolve(view.castState)
        }
    }
#endif
    
    @objc func getAudioTracks(_ reactTag: NSNumber, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerViewController = view.playerViewController {
                view.toggleUIGroup(view: playerViewController.view, name: "JWPlayerKit.InterfaceView", ofSubview: nil, show: show)
            }
        }
    }
    
    @objc func setVisibility(_ reactTag: NSNumber, _ visibility: Bool, _ controls: [String]) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if view.playerViewController != nil {
                view.setVisibility(isVisible: visibility, forControls: controls)
            }
        }
    }
    
    @objc func setLockScreenControls(_ reactTag: NSNumber, _ show: Bool) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
                print("Invalid view returned from registry, expecting RNJWPlayerView")
                return
            }
            
            if let playerViewController = view.playerViewController {
                playerViewController.enableLockScreenControls = show
            }
        }
    }
    
    @objc func setCurrentCaptions(_ reactTag: NSNumber, _ index: NSNumber) {
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
        DispatchQueue.main.async {
            guard let view = self.getPlayerView(reactTag: reactTag) else {
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
    
}
