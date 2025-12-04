/**
 * Unified JWPlayer React Native Configuration Types
 * 
 * This file provides a unified configuration interface that works across
 * both iOS (JWJSONParser) and Android (JsonHelper) platforms.
 * 
 * Platform-specific features are clearly marked with @platform tags.
 * 
 * @see iOS Parser: ios-json-parser/jwplayer-config.d.ts
 * @see Android Parser: android-json-parser/jwplayer-config-types.d.ts
 */

import { JWAdvertisingConfig } from './advertising';
import { JWPlaylistItem, JWSource, JWTrack, Playlist } from './playlist';
import {
  AudioSessionCategory,
  AudioSessionCategoryOptions,
  AudioSessionMode,
  JWStyling,
  JWUiConfig,
  JWLogoView,
  JWRelatedConfig,
  Stretching,
  ThumbnailPreview,
  Preload,
  InterfaceBehavior,
  UIGroup,
  NextUpStyle,
  JWExternalPlaybackSettings,
} from './platform-specific';

/**
 * Unified JWPlayer Configuration
 * 
 * Main configuration interface supporting both iOS and Android platforms.
 * Uses native JSON parsers: JWJSONParser (iOS) and JsonHelper (Android)
 * 
 * @platforms iOS, Android
 * 
 * @example Basic Video
 * ```typescript
 * const config: JWPlayerConfig = {
 *   license: 'YOUR_LICENSE_KEY',
 *   file: 'https://example.com/video.m3u8',
 *   autostart: true
 * };
 * ```
 * 
 * @example With IMA DAI (Cross-Platform)
 * ```typescript
 * const config: JWPlayerConfig = {
 *   license: 'YOUR_LICENSE_KEY',
 *   file: 'https://example.com/video.m3u8',
 *   advertising: {
 *     client: 'dai',
 *     imaDaiSettings: {
 *       videoId: 'tears-of-steel',
 *       cmsId: '2528370'
 *     }
 *   }
 * };
 * ```
 */
export interface JWPlayerConfig {
  // ========== REQUIRED ==========
  
  /**
   * Platform-specific license key
   * 
   * This is required for the wrapper but not parsed by native JSON parsers.
   * You must provide the correct license for each platform.
   * 
   * @see https://docs.jwplayer.com/players/docs/android-overview#requirements
   * @see https://docs.jwplayer.com/players/docs/ios-overview#requirements
   */
  license: string;
  
  // ========== MEDIA CONTENT (Both platforms) ==========
  
  /**
   * URL of a single media file
   * 
   * Use this for a single video configuration.
   * Mutually exclusive with `sources` and `playlist`.
   * 
   * @platforms iOS, Android
   */
  file?: string;
  
  /**
   * Array of video sources for multiple quality levels
   * 
   * Use this for a single video with multiple renditions.
   * Mutually exclusive with `file` and `playlist`.
   * 
   * @platforms iOS, Android
   */
  sources?: JWSource[];
  
  /**
   * Array of playlist items or URL string pointing to a playlist feed
   * 
   * Use this for multiple videos or a remote playlist.
   * Mutually exclusive with `file` and `sources`.
   * 
   * @platforms iOS, Android
   */
  playlist?: Playlist;
  
  /**
   * Index of playlist item to start with (0-based)
   * 
   * @default 0
   * @platforms iOS, Android
   */
  playlistIndex?: number;
  
  // ========== PLAYBACK SETTINGS (Both platforms) ==========
  
  /**
   * Whether the video should start playing automatically
   * 
   * @default false
   * @platforms iOS, Android
   */
  autostart?: boolean;
  
  /**
   * Whether to mute audio initially
   * 
   * @default false
   * @platforms iOS, Android
   */
  mute?: boolean;
  
  /**
   * Whether content should repeat after it's done playing
   * 
   * @default false
   * @platforms iOS, Android
   */
  repeat?: boolean;
  
  /**
   * Preload behavior for content
   * - "auto": Loads the manifest before playback (default)
   * - "none": Doesn't preload content
   * 
   * @default "auto"
   * @platforms iOS, Android
   */
  preload?: Preload | boolean;
  
  /**
   * Video stretching mode
   * 
   * @platforms iOS, Android
   */
  stretching?: Stretching;
  
  /**
   * Array of playback speed rates available in the player
   * Must be between 0.25 and 4.0
   * 
   * @example [0.5, 0.75, 1, 1.25, 1.5, 2]
   * @platforms iOS, Android
   */
  playbackRates?: number[];
  
  /**
   * Whether to show playback rate controls
   * 
   * @default false
   * @platforms iOS, Android
   */
  playbackRateControls?: boolean;
  
  /**
   * Maximum bitrate (in bits per second) for automatic quality switching
   * 
   * Useful for limiting bandwidth consumption.
   * 
   * @platforms iOS, Android
   */
  bitrateUpperBound?: number;
  
  // ========== ADVERTISING (Both platforms) ==========
  
  /**
   * Advertising configuration
   * 
   * Supports multiple ad clients: VAST, VMAP, Google IMA, Google DAI
   * 
   * @platforms iOS, Android
   */
  advertising?: JWAdvertisingConfig;
  
  // ========== RELATED CONTENT (Both platforms) ==========
  
  /**
   * Related content configuration
   * 
   * Controls the display and behavior of recommended content.
   * 
   * @platforms iOS, Android
   */
  related?: JWRelatedConfig;
  
  // ========== PLAYER IDENTIFICATION (Both platforms) ==========
  
  /**
   * Player ID for JWPlayer Dashboard analytics
   * 
   * Must be an 8-character alphanumeric string.
   * Also accepted as "playerId".
   * 
   * @platforms iOS, Android
   */
  pid?: string;
  
  /**
   * Alternative naming for pid
   * 
   * @platforms iOS, Android
   */
  playerId?: string;
  
  // ========== DISPLAY SETTINGS ==========
  
  /**
   * Whether to display the title in the player UI
   * 
   * @platform android
   */
  displaytitle?: boolean;
  
  /**
   * Alternative camelCase naming for displaytitle
   * 
   * @platform android
   */
  displayTitle?: boolean;
  
  /**
   * Whether to display the description in the player UI
   * 
   * @platforms iOS, Android
   */
  displaydescription?: boolean;
  
  /**
   * Alternative camelCase naming for displaydescription
   * 
   * @platforms iOS, Android
   */
  displayDescription?: boolean;
  
  /**
   * Next up offset - when to show the next up overlay
   * Can be a number (seconds) or string with % (percentage)
   * 
   * @platforms iOS, Android
   */
  nextupoffset?: string | number;
  
  /**
   * Next up style configuration
   * 
   * @platform ios
   */
  nextUpStyle?: NextUpStyle;
  
  /**
   * Thumbnail preview quality level
   * 
   * @platform android
   */
  thumbnailPreview?: ThumbnailPreview;
  
  /**
   * Logo view configuration
   * 
   * @platforms iOS, Android
   */
  logoView?: JWLogoView;
  
  // ========== STYLING (iOS only) ==========
  
  /**
   * Styling configuration for iOS
   * 
   * Controls colors, fonts, captions styling, etc.
   * 
   * Note: Android requires overloading of JWP IDs using XML styling.
   * @see https://docs.jwplayer.com/players/docs/android-styling-guide
   * 
   * @platform ios
   */
  styling?: JWStyling;
  
  // ========== UI CONFIGURATION (Android only) ==========
  
  /**
   * UI configuration for Android
   * 
   * Controls which UI elements are visible.
   * When using this, specify all elements - unspecified default to false.
   * 
   * @platform android
   */
  uiConfig?: JWUiConfig;
  
  /**
   * Whether to show player controls
   * 
   * @default true
   * @platforms iOS, Android
   */
  controls?: boolean;
  
  // ========== AUDIO SESSION (iOS only) ==========
  
  /**
   * Enable background audio playback
   * 
   * @platform ios
   */
  backgroundAudioEnabled?: boolean;
  
  /**
   * Audio session category
   * 
   * Controls how the app's audio interacts with other audio.
   * 
   * @platform ios
   */
  category?: AudioSessionCategory;
  
  /**
   * Audio session category options
   * 
   * Modifies the behavior of the audio session category.
   * 
   * @platform ios
   */
  categoryOptions?: AudioSessionCategoryOptions[];
  
  /**
   * Audio session mode
   * 
   * Specialized modes for specific use cases.
   * 
   * @platform ios
   */
  mode?: AudioSessionMode;
  
  /**
   * Interface behavior mode
   * 
   * @platform ios
   */
  interfaceBehavior?: InterfaceBehavior;
  
  /**
   * Delay before interface fades out (in seconds)
   * 
   * @platform ios
   */
  interfaceFadeDelay?: number;
  
  /**
   * UI groups to hide
   * 
   * @platform ios
   */
  hideUIGroups?: UIGroup[];
  
  /**
   * FairPlay certificate URL for DRM
   * 
   * @platform ios
   */
  fairplayCertUrl?: string;
  
  /**
   * Alternative naming for fairplayCertUrl
   * 
   * @platform ios
   */
  certificateUrl?: string;
  
  /**
   * Process SPC URL for FairPlay DRM
   * 
   * @platform ios
   */
  processSpcUrl?: string;
  
  /**
   * Content UUID for DRM
   * 
   * @platform ios
   */
  contentUUID?: string;
  
  /**
   * View-only mode (no interaction)
   * 
   * @platform ios
   */
  viewOnly?: boolean;
  
  /**
   * Offline message to display when offline
   * 
   * @platform ios
   */
  offlineMessage?: string;
  
  /**
   * Offline image to display when offline
   * 
   * @platform ios
   */
  offlineImage?: string;
  
  /**
   * Force fullscreen when device is in landscape
   * 
   * @platform ios
   */
  forceFullScreenOnLandscape?: boolean;
  
  /**
   * Force landscape when entering fullscreen
   * 
   * @platform ios
   */
  forceLandscapeOnFullScreen?: boolean;
  
  /**
   * External playback settings (AirPlay)
   * 
   * @platform ios
   */
  externalPlaybackSettings?: JWExternalPlaybackSettings;
  
  // ========== ANDROID-SPECIFIC ==========
  
  /**
   * Use TextureView instead of SurfaceView
   * 
   * @platform android
   */
  useTextureView?: boolean;
  
  /**
   * Allow cross-protocol redirects (HTTP to HTTPS)
   * 
   * @platform android
   */
  allowCrossProtocolRedirectsSupport?: boolean;
  
  // ========== WRAPPER-SPECIFIC (Not in JSON parsers) ==========
  
  /**
   * Force use of legacy configuration builder instead of JSON parser
   * 
   * When true, bypasses native JSON parsers and uses custom builder logic.
   * 
   * @default false
   */
  forceLegacyConfig?: boolean;
  
  /**
   * Enable playlist item callback functionality
   * 
   * If true, `onBeforeNextPlaylistItem` MUST be implemented with
   * `player.resolveNextPlaylistItem()` called in the callback or content will hang.
   * 
   * @default false
   */
  playlistItemCallbackEnabled?: boolean;
  
  /**
   * Whether player is displayed in a modal
   * 
   * Affects fullscreen behavior.
   */
  playerInModal?: boolean;
  
  /**
   * Enter fullscreen when device rotates to landscape
   */
  fullScreenOnLandscape?: boolean;
  
  /**
   * Rotate device to landscape when entering fullscreen
   */
  landscapeOnFullScreen?: boolean;
  
  /**
   * Rotate device to portrait when exiting fullscreen
   */
  portraitOnExitFullScreen?: boolean;
  
  /**
   * Exit fullscreen when device rotates to portrait
   */
  exitFullScreenOnPortrait?: boolean;
  
  /**
   * Enable lock screen controls (iOS)
   * 
   * @platform ios
   */
  enableLockScreenControls?: boolean;
  
  /**
   * Enable Picture-in-Picture support
   */
  pipEnabled?: boolean;
  
  // ========== LEGACY PROPERTIES (for single-item configs) ==========
  // These are applied when using `file` or `sources` instead of `playlist`
  
  /**
   * Title of the media item
   */
  title?: string;
  
  /**
   * Description of the media item
   */
  description?: string;
  
  /**
   * URL of the poster image
   */
  image?: string;
  
  /**
   * Array of caption, thumbnail, or chapter tracks
   */
  tracks?: JWTrack[];
  
  /**
   * Start time in seconds
   */
  starttime?: number;
  
  /**
   * Media identifier
   */
  mediaid?: string;
  
  /**
   * Alternative camelCase naming for mediaid
   */
  mediaId?: string;
}

/**
 * Type guard to check if advertising config is IMA DAI
 */
export function isImaDaiAdvertising(
  advertising: JWAdvertisingConfig
): advertising is import('./advertising').ImaDaiAdvertisingConfig {
  return (
    advertising.client === 'dai' ||
    advertising.client === 'GoogleIMADAI' ||
    advertising.client === 'IMA_DAI'
  );
}

/**
 * Type guard to check if playlist is an array of items
 */
export function isPlaylistArray(
  playlist: Playlist
): playlist is JWPlaylistItem[] {
  return Array.isArray(playlist);
}

/**
 * Type guard to check if playlist is a URL string
 */
export function isPlaylistUrl(playlist: Playlist): playlist is string {
  return typeof playlist === 'string';
}

