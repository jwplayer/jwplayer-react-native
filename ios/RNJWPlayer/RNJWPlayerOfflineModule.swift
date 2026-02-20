//
//  RNJWPlayerOfflineModule.swift
//  RNJWPlayer
//
//  Native module for offline video downloads with FairPlay DRM support.
//  Follows the NativeDemo OfflineDRM pattern:
//    1. Acquire and persist DRM keys via contentLoader.load(playlist:)
//    2. Start AVAssetDownloadTask after keys are persisted
//    3. Offline playback uses persisted keys from disk
//

import Foundation
import JWPlayerKit
import AVFoundation
import React

@objc(RNJWPlayerOfflineModule)
class RNJWPlayerOfflineModule: RCTEventEmitter, OfflineKeyManagerDelegate {
    
    // MARK: - Properties
    
    private let savedDataKeyBase = "jwplayer_offline:"
    private func key(for mediaId: String) -> String { savedDataKeyBase + mediaId }
    
    private var contentLoader: JWDRMContentLoader?
    private let keyManager: OfflineKeyManager = OfflineKeyManager()
    private let keyDataSource: OfflineKeyDataSource = OfflineKeyDataSource()
    
    private var assetDownloadURLSession: AVAssetDownloadURLSession!
    private let delegateQueue: OperationQueue = {
        let q = OperationQueue()
        q.maxConcurrentOperationCount = 1
        return q
    }()
    
    private let assetDelegate = OfflineAssetDownloadDelegate()
    
    /// Pending downloads waiting for DRM key persistence before starting
    /// the AVAssetDownloadTask. Keyed by mediaId.
    private var pendingDownloads: [String: PendingDownload] = [:]
    
    private struct PendingDownload {
        let mediaId: String
        let fileURL: URL
        let playlistURL: URL
    }
    
    // MARK: - Initialization
    
    override init() {
        super.init()
        keyManager.delegate = self
        setupDownloadSession()
    }
    
    private func setupDownloadSession() {
        let backgroundConfig = URLSessionConfiguration.background(withIdentifier: "com.jwplayer.offline.download")
        assetDownloadURLSession = AVAssetDownloadURLSession(
            configuration: backgroundConfig,
            assetDownloadDelegate: assetDelegate,
            delegateQueue: delegateQueue
        )
        
        assetDelegate.onProgress = { [weak self] mediaId, progress in
            self?.sendEvent(withName: "onDownloadProgress", body: [
                "mediaId": mediaId,
                "progress": progress
            ])
        }
        
        assetDelegate.onComplete = { [weak self] mediaId, url in
            self?.persist(url: url, for: mediaId)
            self?.sendEvent(withName: "onDownloadComplete", body: [
                "mediaId": mediaId,
                "url": url.absoluteString
            ])
        }
        
        assetDelegate.onError = { [weak self] mediaId, error in
            self?.sendEvent(withName: "onDownloadError", body: [
                "mediaId": mediaId,
                "error": error.localizedDescription
            ])
        }
    }
    
    // MARK: - Persistence
    
    private func persist(url: URL, for mediaId: String) {
        do {
            let bookmark = try url.bookmarkData(options: .minimalBookmark, includingResourceValuesForKeys: nil, relativeTo: nil)
            UserDefaults.standard.set(bookmark, forKey: key(for: mediaId))
        } catch {
            print("Failed to create bookmark for mediaId=\(mediaId): \(error)")
        }
    }
    
    private func restoredURL(for mediaId: String) -> URL? {
        guard let data = UserDefaults.standard.data(forKey: key(for: mediaId)) else { return nil }
        var stale = false
        do {
            let url = try URL(resolvingBookmarkData: data, bookmarkDataIsStale: &stale)
            return stale ? nil : url
        } catch {
            return nil
        }
    }
    
    private func removePersistedURL(for mediaId: String) {
        UserDefaults.standard.removeObject(forKey: key(for: mediaId))
    }
    
    // MARK: - Stream Download
    
    /// Starts the AVAssetDownloadTask for the given pending download.
    /// Called after DRM keys have been persisted, or immediately for non-DRM content.
    private func startStreamDownload(for pending: PendingDownload) {
        let asset = AVURLAsset(url: pending.fileURL)
        let preferredMediaSelection = asset.preferredMediaSelection
        
        guard let task = assetDownloadURLSession.aggregateAssetDownloadTask(
            with: asset,
            mediaSelections: [preferredMediaSelection],
            assetTitle: pending.mediaId,
            assetArtworkData: nil,
            options: [AVAssetDownloadTaskMinimumRequiredMediaBitrateKey: 265_000]
        ) else {
            sendEvent(withName: "onDownloadError", body: [
                "mediaId": pending.mediaId,
                "error": "Failed to create download task"
            ])
            return
        }
        
        task.taskDescription = pending.mediaId
        assetDelegate.register(task: task, mediaId: pending.mediaId)
        task.resume()
    }
    
    // MARK: - OfflineKeyManagerDelegate
    
    /// Called when DRM keys have been persisted to disk.
    /// Now safe to start the actual stream download.
    func offlineKeyManager(_ manager: OfflineKeyManager, didPersistKeyFor contentKeyIdentifier: String) {
        for (mediaId, pending) in pendingDownloads {
            if contentKeyIdentifier.contains(mediaId) || true {
                pendingDownloads.removeValue(forKey: mediaId)
                startStreamDownload(for: pending)
                return
            }
        }
        
        if let (mediaId, pending) = pendingDownloads.first {
            pendingDownloads.removeValue(forKey: mediaId)
            startStreamDownload(for: pending)
        }
    }
    
    // MARK: - Download Management
    
    @objc
    func downloadVideo(_ config: NSDictionary,
                      resolver: @escaping RCTPromiseResolveBlock,
                      rejecter: @escaping RCTPromiseRejectBlock) {
        guard let mediaId = config["mediaId"] as? String,
              let fileURLString = config["file"] as? String,
              let fileURL = URL(string: fileURLString) else {
            rejecter("INVALID_CONFIG", "Missing mediaId or file URL", nil)
            return
        }
        
        let hasDRM = config["processSpcUrl"] is String && config["certificateUrl"] is String
        
        if hasDRM,
           let processSpcUrl = config["processSpcUrl"] as? String,
           let certificateUrl = config["certificateUrl"] as? String {
            
            keyDataSource.processSPCURLStr = processSpcUrl
            keyDataSource.certificateURLStr = certificateUrl
            contentLoader = JWDRMContentLoader(dataSource: keyDataSource, keyManager: keyManager)
            
            // Check if keys already exist on disk (re-download case)
            let existingKeys = keyManager.contentLoader(contentLoader!, contentKeyExistsOnDisk: mediaId)
            
            if existingKeys {
                let pending = PendingDownload(mediaId: mediaId, fileURL: fileURL, playlistURL: fileURL)
                startStreamDownload(for: pending)
            } else {
                // Acquire persistable keys before starting the download.
                // Uses load(items:) which works with HLS URLs directly,
                // unlike load(playlist:) which expects a JW Platform JSON endpoint.
                keyManager.markDownloadActive()
                pendingDownloads[mediaId] = PendingDownload(mediaId: mediaId, fileURL: fileURL, playlistURL: fileURL)
                
                do {
                    let playerItem = try JWPlayerItemBuilder()
                        .file(fileURL)
                        .mediaId(mediaId)
                        .build()
                    contentLoader?.load(items: [playerItem])
                } catch {
                    keyManager.clearDownloadState()
                    pendingDownloads.removeValue(forKey: mediaId)
                }
            }
        } else {
            let pending = PendingDownload(mediaId: mediaId, fileURL: fileURL, playlistURL: fileURL)
            startStreamDownload(for: pending)
        }
        
        resolver(true)
    }
    
    @objc
    func isDownloaded(_ mediaId: String,
                     resolver: RCTPromiseResolveBlock,
                     rejecter: RCTPromiseRejectBlock) {
        resolver(restoredURL(for: mediaId) != nil)
    }
    
    @objc
    func getDownloads(_ resolve: RCTPromiseResolveBlock,
                     rejecter: RCTPromiseRejectBlock) {
        let defaults = UserDefaults.standard
        let all = defaults.dictionaryRepresentation().keys
        let prefix = savedDataKeyBase
        
        let results = all
            .filter { $0.hasPrefix(prefix) }
            .map { ["mediaId": String($0.dropFirst(prefix.count))] }
        
        resolve(results)
    }
    
    @objc
    func deleteDownload(_ mediaId: String,
                       resolver: @escaping RCTPromiseResolveBlock,
                       rejecter: @escaping RCTPromiseRejectBlock) {
        // Delete downloaded .movpkg
        if let url = restoredURL(for: mediaId) {
            do {
                try FileManager.default.removeItem(at: url)
            } catch {
                rejecter("DELETE_FAILED", "Failed to delete media: \(error.localizedDescription)", error)
                return
            }
        }
        
        removePersistedURL(for: mediaId)
        
        // Clean up DRM key files stored via appendingPathExtension
        let documentsDir = keyManager.keyDirectory.deletingLastPathComponent()
        let keyPrefix = keyManager.keyDirectory.lastPathComponent
        let fm = FileManager.default
        do {
            let contents = try fm.contentsOfDirectory(at: documentsDir, includingPropertiesForKeys: nil)
            for fileURL in contents {
                let name = fileURL.lastPathComponent
                if name.hasPrefix(keyPrefix) && name != keyPrefix {
                    try fm.removeItem(at: fileURL)
                }
            }
        } catch {
            // Key files may not exist for non-DRM content
        }
        
        resolver(true)
    }
    
    @objc
    func getOfflinePlaylistItem(_ mediaId: String,
                               resolver: @escaping RCTPromiseResolveBlock,
                               rejecter: @escaping RCTPromiseRejectBlock) {
        guard let url = restoredURL(for: mediaId) else {
            rejecter("NOT_FOUND", "No offline file found for mediaId: \(mediaId)", nil)
            return
        }
        
        let dataDir = url.appendingPathComponent("Data")
        if let m3u8Path = firstM3U8File(inDirectory: dataDir) {
            resolver([
                "mediaId": mediaId,
                "file": url.absoluteString,
                "m3u8File": m3u8Path,
                "localURL": url.absoluteString,
                "isOffline": true
            ])
        } else {
            rejecter("NO_PLAYLIST", "No .m3u8 file found in downloaded content", nil)
        }
    }
    
    private func firstM3U8File(inDirectory directoryURL: URL) -> String? {
        let fm = FileManager.default
        guard fm.fileExists(atPath: directoryURL.path) else { return nil }
        
        do {
            let files = try fm.contentsOfDirectory(at: directoryURL, includingPropertiesForKeys: [.isRegularFileKey], options: [.skipsHiddenFiles])
            for fileURL in files {
                let resourceValues = try? fileURL.resourceValues(forKeys: [.isRegularFileKey])
                if resourceValues?.isRegularFile == true && fileURL.pathExtension.lowercased() == "m3u8" {
                    return fileURL.path
                }
            }
        } catch {
            // Directory not accessible
        }
        
        return nil
    }
    
    // MARK: - RCTEventEmitter
    
    override func supportedEvents() -> [String]! {
        return ["onDownloadProgress", "onDownloadComplete", "onDownloadError"]
    }
    
    override static func requiresMainQueueSetup() -> Bool {
        return false
    }
}

// MARK: - Asset Download Delegate

class OfflineAssetDownloadDelegate: NSObject, AVAssetDownloadDelegate {
    var onProgress: ((String, Double) -> Void)?
    var onComplete: ((String, URL) -> Void)?
    var onError: ((String, Error) -> Void)?
    
    private var finalLocations: [Int: URL] = [:]
    private var taskMediaIds: [Int: String] = [:]
    
    func register(task: URLSessionTask, mediaId: String) {
        taskMediaIds[task.taskIdentifier] = mediaId
    }
    
    func urlSession(_ session: URLSession,
                    aggregateAssetDownloadTask: AVAggregateAssetDownloadTask,
                    willDownloadTo location: URL) {
        finalLocations[aggregateAssetDownloadTask.taskIdentifier] = location
    }
    
    func urlSession(_ session: URLSession,
                    aggregateAssetDownloadTask: AVAggregateAssetDownloadTask,
                    didLoad timeRange: CMTimeRange,
                    totalTimeRangesLoaded loadedTimeRanges: [NSValue],
                    timeRangeExpectedToLoad: CMTimeRange,
                    for mediaSelection: AVMediaSelection) {
        let loadedDuration = loadedTimeRanges
            .map { $0.timeRangeValue }
            .reduce(0.0) { partial, range in
                let seconds = CMTimeGetSeconds(range.duration)
                return partial + (seconds.isFinite ? seconds : 0.0)
            }
        
        let expected = CMTimeGetSeconds(timeRangeExpectedToLoad.duration)
        let progress = expected > 0 ? min(max(loadedDuration / expected, 0), 1) : 0
        
        let mediaId = taskMediaIds[aggregateAssetDownloadTask.taskIdentifier] ?? ""
        onProgress?(mediaId, progress * 100)
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        let taskId = task.taskIdentifier
        let mediaId = taskMediaIds[taskId] ?? task.taskDescription ?? ""
        
        if let error = error {
            onError?(mediaId, error)
            return
        }
        
        guard let finalURL = finalLocations[taskId] else { return }
        onComplete?(mediaId, finalURL)
    }
}
