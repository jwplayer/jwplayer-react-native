//
//  OfflineKeyDataSource.swift
//  RNJWPlayer
//
//  Key data source for offline DRM content playback.
//

import Foundation
import JWPlayerKit

/// Manages DRM certificate and license requests for offline content.
class OfflineKeyDataSource: NSObject, JWDRMContentKeyDataSource {
    var certificateURLStr: String?
    var processSPCURLStr: String?
    
    func contentIdentifierForURL(_ url: URL, completionHandler handler: @escaping (Data?) -> Void) {
        handler(url.host?.data(using: .utf8))
    }
    
    func appIdentifierForURL(_ url: URL, completionHandler handler: @escaping (Data?) -> Void) {
        guard let certificateURLStr = certificateURLStr,
              let certificateURL = URL(string: certificateURLStr) else {
            handler(nil)
            return
        }
        
        URLSession.shared.dataTask(with: URLRequest(url: certificateURL)) { (data, response, error) in
            handler(data)
        }.resume()
    }
    
    func contentKeyWithSPCData(_ spcData: Data, completionHandler handler: @escaping (Data?, Date?, String?) -> Void) {
        guard let processSPCURLStr = processSPCURLStr,
              let processSPCURL = URL(string: processSPCURLStr) else {
            handler(nil, nil, nil)
            return
        }
        
        var request = URLRequest(url: processSPCURL)
        request.httpMethod = "POST"
        request.addValue("application/octet-stream", forHTTPHeaderField: "Content-type")
        request.httpBody = spcData
        
        URLSession.shared.dataTask(with: request) { (data, response, error) in
            handler(data, nil, "application/octet-stream")
        }.resume()
    }
}
