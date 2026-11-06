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

    /// MPRemoteCommandCenter is process-wide: with several players, the one that applied
    /// buttons last owns the feedback commands. Only the owner disables them on teardown.
    private static weak var owner: RNJWPlayerRemoteCommands?

    private var targets: [(MPFeedbackCommand, Any)] = []
    private let onClick: (String) -> Void

    init(onClick: @escaping (String) -> Void) {
        self.onClick = onClick
    }

    deinit {
        // Safety net only: the view calls clear() on teardown. Removing a target is safe on
        // any thread-hop; never disable here, as another player may own the commands now.
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

    /// Replaces the bridge's feedback-command buttons. Buttons without a known `iosCommand`
    /// are ignored (Android-only). `[]` removes them. Must be called on the main thread.
    func apply(_ buttons: [[String: Any]]) {
        clear()

        var used = Set<String>()
        for button in buttons {
            guard let id = button["id"] as? String,
                  let name = button["iosCommand"] as? String,
                  !used.contains(name),
                  let command = RNJWPlayerRemoteCommands.command(for: name) else {
                continue
            }
            used.insert(name)

            let title = button["title"] as? String ?? id
            command.localizedTitle = title
            command.localizedShortTitle = title
            command.isActive = button["iosActive"] as? Bool ?? false
            command.isEnabled = button["enabled"] as? Bool ?? true

            let token = command.addTarget { [weak self] _ in
                guard let self = self else { return .commandFailed }
                self.onClick(id)
                return .success
            }
            targets.append((command, token))
        }

        if !targets.isEmpty {
            RNJWPlayerRemoteCommands.owner = self
        }
    }

    /// Removes this player's targets and, if it owns the commands, disables them.
    func clear() {
        let isOwner = RNJWPlayerRemoteCommands.owner === self
        for (command, token) in targets {
            command.removeTarget(token)
            if isOwner { command.isEnabled = false }
        }
        targets.removeAll()
        if isOwner { RNJWPlayerRemoteCommands.owner = nil }
    }
}
