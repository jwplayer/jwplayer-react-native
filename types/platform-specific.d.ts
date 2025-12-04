/**
 * Platform-Specific Configuration Types
 * 
 * Types that are only supported on iOS or Android
 * 
 * @see iOS: ios-json-parser/jwplayer-config.d.ts
 * @see Android: android-json-parser/jwplayer-config-types.d.ts
 */

// ============================================================================
// IOS-SPECIFIC TYPES
// ============================================================================

/**
 * Audio session category for iOS
 * Controls how the app's audio interacts with other audio on the device
 * 
 * @platform ios
 * @see https://developer.apple.com/documentation/avfoundation/avaudiosession/category
 */
export type AudioSessionCategory =
  | 'Ambient'          // Mix with other audio, silence when locked
  | 'SoloAmbient'      // Default, silences other audio
  | 'Playback'         // For media playback
  | 'Record'           // For recording
  | 'PlayAndRecord'    // For VoIP, recording & playback
  | 'MultiRoute';      // Multiple audio routes

/**
 * Audio session category options for iOS
 * Modifies the behavior of the audio session category
 * 
 * @platform ios
 * @see https://developer.apple.com/documentation/avfoundation/avaudiosession/categoryoptions
 */
export type AudioSessionCategoryOptions =
  | 'MixWithOthers'                  // Mix with other audio
  | 'DuckOthers'                     // Lower volume of other audio
  | 'AllowBluetooth'                 // Allow Bluetooth A2DP
  | 'DefaultToSpeaker'               // Route to speaker by default
  | 'InterruptSpokenAudioAndMix'     // Interrupt spoken audio
  | 'AllowBluetoothA2DP'             // Allow Bluetooth A2DP
  | 'AllowAirPlay'                   // Allow AirPlay
  | 'OverrideMutedMicrophone';       // Override muted microphone

/**
 * Audio session mode for iOS
 * Specialized modes for specific use cases
 * 
 * @platform ios
 * @see https://developer.apple.com/documentation/avfoundation/avaudiosession/mode
 */
export type AudioSessionMode =
  | 'Default'          // Default mode
  | 'VoiceChat'        // For VoIP
  | 'VideoChat'        // For video calls
  | 'GameChat'         // For game chat
  | 'VideoRecording'   // For video recording
  | 'Measurement'      // For audio analysis
  | 'MoviePlayback'    // Optimized for movie playback
  | 'SpokenAudio'      // For podcasts/audiobooks
  | 'VoicePrompt';     // For voice prompts

/**
 * Edge styles for caption text emphasis
 * 
 * @platform ios
 */
export type EdgeStyles = 'none' | 'dropshadow' | 'raised' | 'depressed' | 'uniform';

/**
 * Preload values
 */
export type Preload = 'auto' | 'none';

/**
 * Interface behavior modes
 * 
 * @platform ios
 */
export type InterfaceBehavior = 'normal' | 'hidden' | 'onscreen';

/**
 * UI groups that can be hidden on iOS
 * 
 * @platform ios
 */
export type UIGroup =
  | 'overlay'
  | 'control_bar'
  | 'center_controls'
  | 'next_up'
  | 'error'
  | 'playlist'
  | 'controls_container'
  | 'settings_menu'
  | 'quality_submenu'
  | 'captions_submenu'
  | 'playback_submenu'
  | 'audiotracks_submenu'
  | 'casting_menu';

/**
 * Control types for visibility control
 * 
 * @platform ios
 */
export type JWControlType =
  | 'forward'
  | 'rewind'
  | 'pip'
  | 'airplay'
  | 'chromecast'
  | 'next'
  | 'previous'
  | 'settings'
  | 'languages'
  | 'fullscreen';

/**
 * Font configuration for iOS styling
 * 
 * @platform ios
 */
export interface Font {
  /**
   * Font name (e.g., "Helvetica", "Arial")
   */
  name?: string;
  
  /**
   * Font size in points
   */
  size?: number;
}

/**
 * Caption styling configuration for iOS
 * 
 * Only applies to SRT and WebVTT captions when accessibility settings allow.
 * EIA-608 captions always use system accessibility settings.
 * 
 * @platform ios
 */
export interface CaptionStyle {
  /**
   * Font configuration
   */
  font?: Font;
  
  /**
   * Font family name
   */
  fontFamily?: string;
  
  /**
   * Font size in points
   */
  fontSize?: number;
  
  /**
   * Font color in hex format (e.g., "#FFFFFF")
   * @default "#FFFFFF"
   */
  fontColor?: string;
  
  /**
   * Alternative naming for fontColor
   */
  color?: string;
  
  /**
   * Font opacity (0-100)
   * @default 100
   */
  fontOpacity?: number;
  
  /**
   * Background/highlight color in hex format
   * The background behind the text
   * @default "#000000"
   */
  backgroundColor?: string;
  
  /**
   * Background/highlight opacity (0-100)
   * @default 100
   */
  backgroundOpacity?: number;
  
  /**
   * Highlight color (alternative naming)
   */
  highlightColor?: string;
  
  /**
   * Window color in hex format
   * The color of the caption window/box
   * @default "#000000"
   */
  windowColor?: string;
  
  /**
   * Window opacity (0-100)
   * @default 0
   */
  windowOpacity?: number;
  
  /**
   * Edge style for text emphasis
   * @default "none"
   */
  edgeStyle?: EdgeStyles;
  
  /**
   * Whether to allow dynamic text scaling
   * @default true
   */
  allowScaling?: boolean;
  
  /**
   * Override strategy for applying styles
   */
  overrideStrategy?: string;
}

/**
 * Menu styling configuration for iOS
 * 
 * @platform ios
 */
export interface MenuStyle {
  /**
   * Font configuration
   */
  font?: Font;
  
  /**
   * Font color in hex format
   */
  fontColor?: string;
  
  /**
   * Background color in hex format
   */
  backgroundColor?: string;
}

/**
 * Timeslider styling configuration for iOS
 * 
 * @platform ios
 */
export interface TimesliderStyle {
  /**
   * Thumb color in hex format
   */
  thumb?: string;
  
  /**
   * Rail color in hex format
   */
  rail?: string;
  
  /**
   * Slider/progress color in hex format
   */
  slider?: string;
}

/**
 * Colors configuration for iOS styling
 * 
 * @platform ios
 */
export interface Colors {
  /**
   * Button colors in hex format
   */
  buttons?: string;
  
  /**
   * Background color in hex format
   */
  backgroundColor?: string;
  
  /**
   * Font color in hex format
   */
  fontColor?: string;
  
  /**
   * Timeslider styling
   */
  timeslider?: TimesliderStyle;
}

/**
 * Styling configuration for iOS
 * 
 * Note: Android requires overloading of JWP IDs using XML styling
 * @see https://docs.jwplayer.com/players/docs/android-styling-guide
 * 
 * @platform ios
 */
export interface JWStyling {
  /**
   * Color configuration
   */
  colors?: Colors;
  
  /**
   * Font configuration
   */
  font?: Font;
  
  /**
   * Show title in player UI
   */
  showTitle?: boolean;
  
  /**
   * Show description in player UI
   */
  showDesc?: boolean;
  
  /**
   * Caption styling
   */
  captionsStyle?: CaptionStyle;
  
  /**
   * Menu styling
   */
  menuStyle?: MenuStyle;
}

/**
 * Related content click behavior
 * 
 * @platform ios
 */
export type RelatedOnClick = 'play' | 'link';

/**
 * Related content complete behavior
 * 
 * @platform ios
 */
export type RelatedOnComplete = 'show' | 'hide' | 'autoplay';

/**
 * Related content configuration for iOS
 * 
 * @platform ios
 */
export interface JWRelatedIOS {
  /**
   * Behavior when user clicks a related item
   * @default "play"
   */
  onClick?: RelatedOnClick;
  
  /**
   * When to show the related content overlay
   * @default "show"
   */
  onComplete?: RelatedOnComplete;
  
  /**
   * Heading text for related content overlay
   */
  heading?: string;
  
  /**
   * URL to the related content feed
   */
  url?: string;
  
  /**
   * Alternative naming for url
   */
  file?: string;
  
  /**
   * Autoplay countdown message
   * Use `__title__` for the next video title and `xx` for countdown seconds
   */
  autoplayMessage?: string;
  
  /**
   * Autoplay countdown timer in seconds
   * @default 10
   */
  autoplayTimer?: number;
}

/**
 * Next up style configuration
 * 
 * @platform ios
 */
export interface NextUpStyle {
  /**
   * Offset in seconds before end of video to show next up
   */
  offsetSeconds?: number;
  
  /**
   * Offset as percentage of video duration
   */
  offsetPercentage?: number;
}

/**
 * External playback settings for AirPlay
 * 
 * @platform ios
 */
export interface JWExternalPlaybackSettings {
  /**
   * Whether external playback is enabled
   */
  playbackEnabled?: boolean;
  
  /**
   * Whether to use external playback while an external screen is active
   */
  usesExternalPlaybackWhileExternalScreenIsActive?: boolean;
  
  /**
   * Video gravity for external playback
   */
  videoGravity?: 'resize' | 'resizeAspect' | 'resizeAspectFill';
}

// ============================================================================
// ANDROID-SPECIFIC TYPES
// ============================================================================

/**
 * Stretching mode for video on Android
 * 
 * @platform android
 */
export type Stretching = 'uniform' | 'exactfit' | 'fill' | 'none';

/**
 * Thumbnail preview quality levels
 * 
 * @platform android
 */
export type ThumbnailPreview = 101 | 102 | 103;

/**
 * UI Configuration for Android
 * 
 * Controls which UI elements are visible in the player.
 * When using this, specify all elements - unspecified elements default to false.
 * 
 * @platform android
 */
export interface JWUiConfig {
  /**
   * Show overlay UI
   * @default false
   */
  hasOverlay?: boolean;
  
  /**
   * Show control bar
   * @default false
   */
  hasControlbar?: boolean;
  
  /**
   * Show center play/pause controls
   * @default false
   */
  hasCenterControls?: boolean;
  
  /**
   * Show next up overlay
   * @default false
   */
  hasNextUp?: boolean;
  
  /**
   * Show side seek controls
   * @default false
   */
  hasSideSeek?: boolean;
  
  /**
   * Show logo view
   * @default false
   */
  hasLogoView?: boolean;
  
  /**
   * Show error messages
   * @default false
   */
  hasError?: boolean;
  
  /**
   * Show playlist UI
   * @default false
   */
  hasPlaylist?: boolean;
  
  /**
   * Show quality submenu
   * @default false
   */
  hasQualitySubMenu?: boolean;
  
  /**
   * Show captions submenu
   * @default false
   */
  hasCaptionsSubMenu?: boolean;
  
  /**
   * Show playback rates submenu
   * @default false
   */
  hasPlaybackRatesSubMenu?: boolean;
  
  /**
   * Show audio tracks submenu
   * @default false
   */
  hasAudiotracksSubMenu?: boolean;
  
  /**
   * Show settings menu
   * @default false
   */
  hasMenu?: boolean;
  
  /**
   * Show player controls container
   * @default false
   */
  hasPlayerControlsContainer?: boolean;
  
  /**
   * Show casting menu
   * @default false
   */
  hasCastingMenu?: boolean;
  
  /**
   * Show chapters UI
   * @default false
   */
  hasChapters?: boolean;
  
  /**
   * Show ads UI
   * @default false
   */
  hasAds?: boolean;
}

/**
 * Logo position on Android
 * 
 * @platform android
 */
export type LogoPosition = 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight';

/**
 * Logo view configuration for Android
 * 
 * @platform android
 */
export interface JWLogoView {
  /**
   * Path to logo image file
   */
  imageFile: string;
  
  /**
   * Whether logo fades in/out with controls
   * Note: Margin required for fade on Android
   */
  fades: boolean;
  
  /**
   * Margin around logo in pixels
   */
  margin?: number;
  
  /**
   * Logo position
   */
  position?: LogoPosition;
  
  /**
   * Web link to open when logo is clicked
   */
  webLink: string;
}

/**
 * Related content configuration for Android
 * 
 * @platform android
 */
export interface JWRelatedAndroid {
  /**
   * URL to the related content feed
   */
  file?: string;
  
  /**
   * When to show the related content overlay
   */
  oncomplete?: 'hide' | 'show' | 'none' | 'autoplay';
  
  /**
   * Behavior when user clicks a related item
   */
  onclick?: 'play' | 'link';
  
  /**
   * Autoplay countdown timer in seconds
   */
  autoplaytimer: number;
}

/**
 * Unified related content configuration
 * Supports both iOS and Android structures
 * 
 * @platforms iOS, Android
 */
export type JWRelatedConfig = JWRelatedIOS | JWRelatedAndroid;

