/**
 * JWPlayer Playlist Configuration Types
 * 
 * Unified playlist, source, and track types supporting both iOS and Android
 * 
 * @see iOS: ios-json-parser/jwplayer-config.d.ts
 * @see Android: android-json-parser/jwplayer-config-types.d.ts
 */

import { JWAdBreak, JWImaDaiSettings } from './advertising';

/**
 * Media type/format
 */
export type MediaType = 'mp4' | 'webm' | 'aac' | 'mp3' | 'hls' | 'dash' | 'm3u8' | 'mpd' | 'oga';

/**
 * Track kind for captions, thumbnails, and chapters
 */
export type TrackKind = 'captions' | 'thumbnails' | 'chapters' | 'subtitles';

/**
 * DRM Configuration
 * 
 * Platform-specific support:
 * - iOS: FairPlay DRM
 * - Android: Widevine DRM
 */
export interface JWDrm {
  /**
   * FairPlay DRM configuration
   * @platform ios
   */
  fairplay?: {
    /**
     * URL for processing SPC (Server Playback Context)
     */
    processSpcUrl?: string;
    
    /**
     * URL for FairPlay certificate
     */
    certificateUrl?: string;
  };
  
  /**
   * Widevine DRM configuration
   * @platform android
   */
  widevine?: {
    /**
     * License server URL
     */
    url: string;
    
    /**
     * Optional key set ID for offline playback
     */
    keySetId?: string;
  };
  
  /**
   * PlayReady DRM configuration (Android)
   * @platform android
   */
  playready?: Record<string, string>;
}

/**
 * Video source representing a specific quality level
 * 
 * @platforms iOS, Android
 */
export interface JWSource {
  /**
   * URL of the video source file
   * Required.
   */
  file: string;
  
  /**
   * Label for the quality level (e.g., "1080p", "720p", "Auto")
   * Auto-generated if not provided
   */
  label?: string;
  
  /**
   * Media type/format
   */
  type?: MediaType;
  
  /**
   * Whether this is the default quality level
   * @default false
   */
  default?: boolean;
  
  /**
   * Alternative naming for default quality
   */
  defaultQuality?: boolean;
  
  /**
   * DRM configuration for this source
   */
  drm?: JWDrm;
  
  /**
   * Custom HTTP headers for this source
   * @platform android
   */
  httpheaders?: Record<string, string>;
}

/**
 * Media track for captions, thumbnails, or chapters
 * 
 * @platforms iOS, Android
 */
export interface JWTrack {
  /**
   * URL to the track file
   * Required.
   */
  file: string;
  
  /**
   * Type of track
   * - "captions": Subtitle/caption track
   * - "thumbnails": Thumbnail preview track (must be WebVTT)
   * - "chapters": Chapter markers track
   * - "subtitles": Subtitle track (Android)
   */
  kind: TrackKind;
  
  /**
   * Label shown in the player UI
   * For captions: shown in captions menu
   * For thumbnails/chapters: not displayed
   */
  label?: string;
  
  /**
   * Whether this track should be shown by default
   * Only applies to captions
   * @default false
   */
  default?: boolean;
  
  /**
   * Locale/language code for the track (e.g., "en", "es", "fr")
   * Primarily used for caption tracks
   */
  locale?: string;
  
  /**
   * Alternative naming for locale
   * @platform android
   */
  language?: string;
  
  /**
   * Whether this track is included in the HLS manifest
   * If true, the side-loaded track will be ignored
   * @default false
   * @platform ios
   */
  includedInManifest?: boolean;
  
  /**
   * Unique identifier for this track
   * @platform android
   */
  id?: string;
}

/**
 * External metadata for timed events
 * 
 * Supplements the encoded metadata of the media asset.
 * Triggers timed events during playback.
 * 
 * Maximum 5 items; excess will be ignored.
 * 
 * @platform ios
 */
export interface JWExternalMetadata {
  /**
   * Unique identifier for this metadata item
   * Required.
   */
  identifier: string;
  
  /**
   * Alternative naming for identifier
   */
  id?: number;
  
  /**
   * Start time in seconds
   * Required.
   */
  startTime: number;
  
  /**
   * End time in seconds
   * Required.
   */
  endTime: number;
}

/**
 * Playlist item configuration
 * 
 * Individual media item in a playlist
 * 
 * @platforms iOS, Android
 */
export interface JWPlaylistItem {
  // ========== MEDIA SOURCE (Required: file OR sources) ==========
  
  /**
   * URL of the media file
   * Mutually exclusive with `sources`
   */
  file?: string;
  
  /**
   * Array of video sources for multiple quality levels
   * Mutually exclusive with `file`
   */
  sources?: JWSource[];
  
  /**
   * Alternative naming for sources (iOS)
   * @platform ios
   */
  allSources?: JWSource[];
  
  // ========== METADATA ==========
  
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
   * Media identifier
   * 
   * Note: Native parsers use lowercase `mediaid`, but `mediaId` is preferred
   */
  mediaId?: string;
  
  /**
   * Legacy naming (lowercase) - still supported
   */
  mediaid?: string;
  
  /**
   * Feed ID for related content
   */
  feedid?: string;
  
  /**
   * Alternative naming for feedId
   */
  feedId?: string;
  
  // ========== PLAYBACK SETTINGS ==========
  
  /**
   * Start time in seconds
   * Where to begin playback
   * @default 0
   */
  starttime?: number;
  
  /**
   * Alternative naming for startTime
   */
  startTime?: number;
  
  /**
   * Duration of the media item in seconds
   * Typically used for related items
   */
  duration?: number;
  
  /**
   * Whether to autostart this item
   * @platform android
   */
  autostart?: boolean;
  
  // ========== TRACKS (CAPTIONS, THUMBNAILS, CHAPTERS) ==========
  
  /**
   * Array of caption, thumbnail, or chapter tracks
   */
  tracks?: JWTrack[];
  
  /**
   * Array of caption tracks only
   * Alternative to specifying captions in `tracks` array
   */
  captions?: JWTrack[];
  
  // ========== ITEM-LEVEL ADVERTISING ==========
  
  /**
   * Ad schedule for this item
   * Can be:
   * - VMAP URL or XML string
   * - Object with ad breaks keyed by offset
   * - Array of ad breaks
   * 
   * Note: Prefer camelCase `adSchedule` but `adschedule` is supported
   */
  adSchedule?: string | Record<string, JWAdBreak> | JWAdBreak[];
  
  /**
   * Alternative naming (lowercase) - still supported
   */
  adschedule?: string | Record<string, JWAdBreak> | JWAdBreak[];
  
  /**
   * Alternative naming - still supported
   */
  schedule?: string | Record<string, JWAdBreak> | JWAdBreak[];
  
  /**
   * Google DAI stream settings for this item
   * 
   * Platform-specific naming:
   * - iOS: `daiSetting` (singular)
   * - Android: `imaDaiSettings` (plural)
   * Both are supported
   */
  imaDaiSettings?: JWImaDaiSettings;
  
  /**
   * iOS naming for DAI settings
   * @platform ios
   */
  daiSetting?: JWImaDaiSettings;
  
  // ========== RELATED CONTENT ==========
  
  /**
   * URL to a feed containing related items for this specific item
   */
  recommendations?: string;
  
  /**
   * Link URL for the item
   */
  link?: string;
  
  // ========== CHROMECAST ==========
  
  /**
   * Custom user data for Chromecast
   * Data to be passed to Chromecast receiver (optional and typically used for DRM implementations)
   */
  userInfo?: Record<string, any>;
  
  // ========== PLATFORM-SPECIFIC ==========
  
  /**
   * Custom HTTP headers for this item
   * @platform android
   */
  httpheaders?: Record<string, string>;
  
  /**
   * AVAsset initialization options
   * See Apple's AVURLAsset Initialization Options documentation
   * @platform ios
   */
  assetOptions?: Record<string, any>;
  
  /**
   * Array of external metadata for this item
   * Overrides player-level external metadata
   * Maximum 5 items
   * @platform ios
   */
  externalMetadata?: JWExternalMetadata[];
  
  /**
   * DRM configuration for this item
   */
  drm?: JWDrm;
}

/**
 * Playlist type - array of items or URL string
 */
export type Playlist = JWPlaylistItem[] | string;

