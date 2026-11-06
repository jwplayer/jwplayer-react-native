//
//  RNJWPlayerRemoteCommands.swift
//  RNJWPlayer
//
//  iOS side of `notificationButtons`: maps buttons that carry an `iosCommand` onto the
//  MPRemoteCommandCenter feedback commands (like / dislike / bookmark).
//
//  JWPlayerKit registers handlers for play, pause, next/previous track, skip, seek and
//  change-playback-position, but never touches the feedback commands, so the bridge can own
//  them without conflicting with the SDK. Targets are added with the block API and removed by
//  token, so the bridge never removes a target it did not add.
//

import Foundation
import MediaPlayer

final class RNJWPlayerRemoteCommands {

    /// MPRemoteCommandCenter is process-wide. Only one player owns the feedback commands at a
    /// time: the one that applied buttons last. Taking ownership clears the previous owner's
    /// targets, so a tap is only ever reported to one player.
    private static weak var owner: RNJWPlayerRemoteCommands?

    private var targets: [(MPFeedbackCommand, Any)] = []
    private let onClick: (String) -> Void
    /// Set on teardown; later apply() calls (e.g. a late setNotificationButtons) are ignored.
    private var invalidated = false

    init(onClick: @escaping (String) -> Void) {
        self.onClick = onClick
    }

    deinit {
        // Safety net only: the view calls invalidate() on teardown. Target removal is
        // dispatched to the main thread; commands are never disabled here because another
        // player may own them by now.
        let tokens = targets
        DispatchQueue.main.async {
            for (command, token) in tokens {
                command.removeTarget(token)
            }
        }
    }

    private static func command(for name: String) -> MPFeedbackCommand? {
        let center = MPRemoteCommandCenter.shared()
        switch name {
        case "like": return center.likeCommand
        case "dislike": return center.dislikeCommand
        case "bookmark": return center.bookmarkCommand
        default: return nil
        }
    }

    /// Replaces the bridge's feedback-command buttons and takes ownership of the commands.
    /// Buttons without a known `iosCommand` are ignored (Android-only). `[]` removes them.
    /// Must be called on the main thread.
    func apply(_ buttons: [[String: Any]]) {
        assert(Thread.isMainThread, "RNJWPlayerRemoteCommands must be used on the main thread")
        guard !invalidated else { return }
        clear()

        // Validate first, so ownership only moves when this player will show something.
        var specs: [(id: String, command: MPFeedbackCommand, button: [String: Any])] = []
        var used = Set<String>()
        for (index, button) in buttons.enumerated() {
            guard let name = button["iosCommand"] as? String else { continue } // Android-only button
            guard let id = button["id"] as? String, !id.isEmpty,
                  let command = RNJWPlayerRemoteCommands.command(for: name) else {
                print("RNJWPlayer: notification button at index \(index) skipped on iOS: needs a string id and iosCommand 'like', 'dislike' or 'bookmark'")
                continue
            }
            guard !used.contains(name) else {
                print("RNJWPlayer: notification button '\(id)' skipped on iOS: iosCommand '\(name)' is already used")
                continue
            }
            used.insert(name)
            specs.append((id, command, button))
        }
        guard !specs.isEmpty else { return }

        // Take ownership: the previous owner's targets go (and its commands are disabled), so a
        // tap is reported to this player only.
        if let previous = RNJWPlayerRemoteCommands.owner, previous !== self {
            previous.clear()
        }
        RNJWPlayerRemoteCommands.owner = self

        for spec in specs {
            let id = spec.id
            spec.command.localizedTitle = spec.button["title"] as? String ?? id
            spec.command.isActive = spec.button["iosActive"] as? Bool ?? false
            spec.command.isEnabled = spec.button["enabled"] as? Bool ?? true
            let token = spec.command.addTarget { [weak self] _ in
                guard let self = self else { return .commandFailed }
                self.onClick(id)
                return .success
            }
            targets.append((spec.command, token))
        }
    }

    /// Removes this player's targets and, if it owns the commands, disables them.
    func clear() {
        assert(Thread.isMainThread, "RNJWPlayerRemoteCommands must be used on the main thread")
        let isOwner = RNJWPlayerRemoteCommands.owner === self
        for (command, token) in targets {
            command.removeTarget(token)
            if isOwner { command.isEnabled = false }
        }
        targets.removeAll()
        if isOwner { RNJWPlayerRemoteCommands.owner = nil }
    }

    /// Teardown: clears and ignores any later apply().
    func invalidate() {
        clear()
        invalidated = true
    }
}
