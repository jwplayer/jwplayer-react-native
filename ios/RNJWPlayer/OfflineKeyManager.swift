//
//  OfflineKeyManager.swift
//  RNJWPlayer
//
//  Manages persistent storage of DRM keys for offline playback.
//  Tracks which media IDs are actively downloading so contentKeyTypeFor
//  returns .persistable during download (to trigger key persistence)
//  and during playback (when keys exist on disk).
//

import Foundation
import JWPlayerKit

/// Callback to notify when persistable keys have been written to disk,
/// signaling that stream download can begin.
protocol OfflineKeyManagerDelegate: AnyObject {
    func offlineKeyManager(_ manager: OfflineKeyManager, didPersistKeyFor contentKeyIdentifier: String)
}

class OfflineKeyManager: NSObject, JWDRMContentKeyManager {
    
    let keyDirectory: URL
    weak var delegate: OfflineKeyManagerDelegate?
    
    private var downloadingKeyIdentifiers: Set<String> = []
    
    override init() {
        let documentDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
        
        guard let contentKeyDirectory = documentDirectory?.appendingPathComponent(".jwplayer-keys/", isDirectory: true) else {
            fatalError("This device does not have a valid document directory")
        }
        
        if !FileManager.default.fileExists(atPath: contentKeyDirectory.path, isDirectory: nil) {
            do {
                try FileManager.default.createDirectory(
                    at: contentKeyDirectory,
                    withIntermediateDirectories: true,
                    attributes: nil
                )
            } catch {
                fatalError("Unable to create directory for content keys at path: \(contentKeyDirectory.path)")
            }
        }
        
        keyDirectory = contentKeyDirectory
        super.init()
    }
    
    // MARK: - Download State Tracking
    
    func markAsDownloading(_ contentKeyIdentifier: String) {
        downloadingKeyIdentifiers.insert(contentKeyIdentifier)
    }
    
    /// Marks all incoming key requests as persistable (used when the
    /// content key identifier isn't known ahead of time).
    func markDownloadActive() {
        downloadingKeyIdentifiers.insert("__download_active__")
    }
    
    func clearDownloadState() {
        downloadingKeyIdentifiers.removeAll()
    }
    
    private func isDownloading(_ contentKeyIdentifier: String) -> Bool {
        return downloadingKeyIdentifiers.contains(contentKeyIdentifier) ||
               downloadingKeyIdentifiers.contains("__download_active__")
    }
    
    private func keyURL(for contentKeyIdentifier: String) -> URL {
        return keyDirectory.appendingPathExtension(contentKeyIdentifier)
    }
    
    private func keyExistsOnDisk(_ contentKeyIdentifier: String) -> Bool {
        return FileManager.default.fileExists(atPath: keyURL(for: contentKeyIdentifier).relativePath)
    }
    
    // MARK: - JWDRMContentKeyManager
    
    func contentLoader(_ contentLoader: JWDRMContentLoader, writePersistableContentKey contentKey: Data, contentKeyIdentifier: String) {
        do {
            try contentKey.write(to: keyURL(for: contentKeyIdentifier))
        } catch {
            print("Error writing DRM key: \(error.localizedDescription)")
        }
    }
    
    func contentLoader(_ contentLoader: JWDRMContentLoader, didWritePersistableContentKey contentKeyIdentifier: String) {
        downloadingKeyIdentifiers.remove(contentKeyIdentifier)
        delegate?.offlineKeyManager(self, didPersistKeyFor: contentKeyIdentifier)
    }
    
    func contentLoader(_ contentLoader: JWDRMContentLoader, deletePersistableContentKey contentKeyIdentifier: String) {
        do {
            try FileManager.default.removeItem(at: keyURL(for: contentKeyIdentifier))
        } catch {
            print("Error deleting DRM key: \(error.localizedDescription)")
        }
    }
    
    func contentLoader(_ contentLoader: JWDRMContentLoader, contentKeyTypeFor contentKeyIdentifier: String) -> JWContentKeyType {
        if isDownloading(contentKeyIdentifier) || keyExistsOnDisk(contentKeyIdentifier) {
            return .persistable
        }
        return .nonpersistable
    }
    
    func contentLoader(_ contentLoader: JWDRMContentLoader, contentKeyExistsOnDisk contentKeyIdentifier: String) -> Bool {
        return keyExistsOnDisk(contentKeyIdentifier)
    }
    
    func contentLoader(_ contentLoader: JWDRMContentLoader, urlForPersistableContentKey contentKeyIdentifier: String) -> URL {
        return keyURL(for: contentKeyIdentifier)
    }
    
    func contentLoader(_ contentLoader: JWDRMContentLoader, failedWithError error: JWError) {
        print("JWDRMContentLoader error: \(error.localizedDescription)")
    }
}
