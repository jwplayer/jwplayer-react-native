//
//  RNJWPlayerMetadata.swift
//  RNJWPlayer
//
//  Serializes the JWPlayerKit metadata models delivered through
//  `JWPlayer.metadataDelegates` into JSON-safe dictionaries for the
//  `onMeta` / `onMetadataCueParsed` React Native events.
//
//  The payload shape intentionally mirrors the JW Player web player's
//  `meta` / `metadataCueParsed` events:
//
//      { metadataType, metadataTime?, metadata?, programDateTime?, ... }
//
//  Everything that crosses the bridge must be JSON serializable, so `Date`
//  becomes an ISO 8601 string, `Data` becomes base64 (or `0x…` hex for HLS
//  date-range attributes), and `AVMetadataItem` collapses to its value.
//

import Foundation
import AVFoundation
import JWPlayerKit

enum RNJWPlayerMetadata {

    // MARK: - Event builders

    /// `JWID3Metadata` → `{ metadataType: "id3", metadataTime, metadata: { <frame id>: <value> } }`
    static func id3(_ metadata: JWID3Metadata) -> [String: Any] {
        var frames: [String: Any] = [:]
        let items: [String: AnyObject] = metadata.metadata ?? [:]

        for (key, value) in items {
            guard let item = value as? AVMetadataItem else {
                frames[key] = jsonSafe(value)
                continue
            }

            let frameValue = self.value(of: item)

            // Frames that carry a description (TXXX, WXXX, ...) nest their value
            // under that description, matching the web player's ID3 parser.
            if let info = item.extraAttributes?[.info] as? String, !info.isEmpty {
                var nested = frames[key] as? [String: Any] ?? [:]
                nested[info] = frameValue
                frames[key] = nested
            } else {
                frames[key] = frameValue
            }

            // Friendly aliases the web player also exposes (title, artist, album, url).
            if let alias = id3Aliases[key], frames[alias] == nil {
                frames[alias] = frameValue
            }
        }

        return event(type: "id3", time: metadata.start, metadata: frames)
    }

    /// `JWDateRangeMetadata` (`#EXT-X-DATERANGE`) → `{ metadataType: "date-range", ... }`
    static func dateRange(_ metadata: JWDateRangeMetadata) -> [String: Any] {
        var attributes: [[String: Any]] = []
        var id: String?

        let rawAttributes: [[String: AnyObject]] = metadata.attributes ?? []
        for attribute in rawAttributes {
            let name = (attribute["name"] as? String) ?? ""
            let value = attributeValue(attribute["value"])
            attributes.append(["name": name, "value": value])
            if name == "ID", let idValue = value as? String {
                id = idValue
            }
        }

        var body: [String: Any] = [
            "tag": "EXT-X-DATERANGE",
            "start": number(metadata.start),
            "end": number(metadata.end),
            "duration": number(metadata.duration),
            "attributes": attributes,
        ]
        if let startDate = metadata.startDate {
            body["startDate"] = iso8601(startDate)
        }
        if let endDate = metadata.endDate {
            body["endDate"] = iso8601(endDate)
        }
        if let id = id {
            body["id"] = id
        }

        return event(type: "date-range", time: metadata.start, metadata: body)
    }

    /// `JWProgramDateTimeMetadata` (`#EXT-X-PROGRAM-DATE-TIME`) → `{ metadataType: "program-date-time", programDateTime, ... }`
    static func programDateTime(_ metadata: JWProgramDateTimeMetadata) -> [String: Any] {
        let programDateTime = iso8601(metadata.programDateTime)
        var payload = event(type: "program-date-time",
                            time: metadata.start,
                            metadata: [
                                "programDateTime": programDateTime,
                                "start": number(metadata.start),
                                "end": number(metadata.end),
                            ])
        // Hoisted to the top level as well, matching the web player.
        payload["programDateTime"] = programDateTime
        return payload
    }

    /// `JWExternalMetadata` (configured via `externalMetadata`) → `{ metadataType: "external", ... }`
    static func external(_ metadata: JWExternalMetadata) -> [String: Any] {
        var body: [String: Any] = [
            "identifier": metadata.identifier,
            "start": number(metadata.startTime),
            "end": number(metadata.endTime),
        ]
        if let id = Int(metadata.identifier) {
            body["id"] = id
        }
        return event(type: "external", time: metadata.startTime, metadata: body)
    }

    /// `JWMediaMetadata` → `{ metadataType: "media", duration, height, width, frameRate, seekRange, drm }`
    static func media(_ metadata: JWMediaMetadata) -> [String: Any] {
        return [
            "metadataType": "media",
            "duration": number(metadata.duration),
            "height": number(metadata.height),
            "width": number(metadata.width),
            "frameRate": number(metadata.frameRate),
            "seekRange": [
                "start": number(metadata.seekRange.start),
                "end": number(metadata.seekRange.end),
            ],
            "drm": metadata.drmEncryption == .fairplay ? "fairplay" : NSNull(),
        ]
    }

    /// `JWAccessLogMetadata` → `{ metadataType: "access-log", metadata: { observedBitrate, indicatedBitrate, droppedFrames } }`
    /// Unknown values (reported as -1 by the SDK) are omitted.
    static func accessLog(_ metadata: JWAccessLogMetadata) -> [String: Any] {
        var body: [String: Any] = [:]
        if metadata.observedBitrate >= 0 {
            body["observedBitrate"] = number(metadata.observedBitrate)
        }
        if metadata.indicatedBitrate >= 0 {
            body["indicatedBitrate"] = number(metadata.indicatedBitrate)
        }
        if metadata.droppedFrames >= 0 {
            body["droppedFrames"] = metadata.droppedFrames
        }
        return ["metadataType": "access-log", "metadata": body]
    }

    // MARK: - Helpers

    private static let id3Aliases: [String: String] = [
        "TIT2": "title", "TT2": "title",
        "TPE1": "artist", "TP1": "artist",
        "TALB": "album", "TAL": "album",
        "WXXX": "url",
    ]

    private static let iso8601Formatter: ISO8601DateFormatter = {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return formatter
    }()

    static func iso8601(_ date: Date) -> String {
        return iso8601Formatter.string(from: date)
    }

    private static func event(type: String, time: Double, metadata: [String: Any]) -> [String: Any] {
        var payload: [String: Any] = ["metadataType": type, "metadata": metadata]
        if time.isFinite {
            payload["metadataTime"] = time
        }
        return payload
    }

    /// `NSJSONSerialization` rejects NaN / infinity, which live streams can report for durations.
    private static func number(_ value: Double) -> Any {
        return value.isFinite ? value : NSNull()
    }

    /// Collapses an `AVMetadataItem` to its most useful JSON-safe value.
    private static func value(of item: AVMetadataItem) -> Any {
        if let string = item.stringValue { return string }
        if let number = item.numberValue { return jsonSafe(number) }
        if let date = item.dateValue { return iso8601(date) }
        if let data = item.dataValue { return data.base64EncodedString() }
        if let raw = item.value { return jsonSafe(raw) }
        return NSNull()
    }

    /// HLS date-range attribute values. Binary attributes (e.g. `SCTE35-OUT`) are
    /// hex sequences in the manifest, so they are re-encoded as `0x…` hex strings.
    private static func attributeValue(_ value: Any?) -> Any {
        if let data = value as? Data {
            return "0x" + data.map { String(format: "%02X", $0) }.joined()
        }
        return jsonSafe(value)
    }

    /// Recursively converts an arbitrary value into something `NSJSONSerialization` accepts.
    static func jsonSafe(_ value: Any?) -> Any {
        guard let value = value else { return NSNull() }

        switch value {
        case is NSNull:
            return NSNull()
        case let string as String:
            return string
        case let number as NSNumber:
            // Bools are NSNumbers too; only guard against non-finite doubles.
            return number.doubleValue.isFinite ? number : NSNull()
        case let date as Date:
            return iso8601(date)
        case let data as Data:
            return data.base64EncodedString()
        case let url as URL:
            return url.absoluteString
        case let item as AVMetadataItem:
            return self.value(of: item)
        case let array as [Any]:
            return array.map { jsonSafe($0) }
        case let dictionary as [String: Any]:
            return dictionary.mapValues { jsonSafe($0) }
        case let dictionary as [AnyHashable: Any]:
            var result: [String: Any] = [:]
            for (key, entry) in dictionary {
                result[String(describing: key)] = jsonSafe(entry)
            }
            return result
        default:
            return String(describing: value)
        }
    }
}
