/**
 * JWPlayer Advertising Configuration Types
 * 
 * Unified advertising types supporting both iOS (JWJSONParser) and Android (JsonHelper)
 * 
 * @see iOS: ios-json-parser/jwplayer-config.d.ts
 * @see Android: android-json-parser/jwplayer-config-types.d.ts
 */

/**
 * Ad client types supported across platforms
 */
export type AdClient = 
  | 'vast'        // VAST ads (both platforms)
  | 'VAST'        // VAST ads alternative (both platforms)
  | 'googima'     // Google IMA (both platforms)
  | 'IMA'         // Google IMA alternative (Android)
  | 'GOOGIMA'     // Google IMA alternative (Android)
  | 'dai'         // Google DAI (iOS)
  | 'GoogleIMADAI' // Google DAI (iOS)
  | 'IMA_DAI';    // Google DAI (Android)

/**
 * Ad break type
 */
export type AdType = 'linear' | 'nonlinear' | 'LINEAR' | 'NONLINEAR';

/**
 * Stream type for IMA DAI
 */
export type StreamType = 'hls' | 'HLS' | 'dash' | 'DASH';

/**
 * OMID (Open Measurement Interface Definition) support levels
 */
export type OmidSupport = 'auto' | 'enabled' | 'disabled';

/**
 * Ad break offset - when to show the ad
 * - "pre": Preroll (before content)
 * - "post": Postroll (after content)
 * - Number: Midroll at N seconds
 * - "N%": Midroll at N% of content duration
 */
export type AdOffset = 'pre' | 'post' | string | number;

/**
 * Start on seek behavior for ad rules
 */
export type StartOnSeek = 'none' | 'pre';

/**
 * IMA SDK Settings
 * Controls the behavior of the Google IMA SDK
 * 
 * @platforms iOS, Android
 */
export interface JWImaSdkSettings {
  /**
   * Session ID for tracking
   */
  sessionId?: string;
  
  /**
   * Publisher Provided ID for tracking
   */
  ppid?: string;
  
  /**
   * Whether to auto-play ad breaks
   * @default false
   */
  autoPlayAdBreaks?: boolean;
  
  /**
   * Language code (e.g., 'en', 'es', 'el')
   */
  language?: string;
  
  /**
   * Maximum number of redirects to follow for ad tags
   * @default 0
   */
  maxRedirects?: number;
  
  /**
   * Player type identifier
   */
  playerType?: string;
  
  /**
   * Player version identifier
   */
  playerVersion?: string;
  
  /**
   * Enable debug mode for IMA SDK
   * @default false
   */
  isDebugMode?: boolean;
  
  /**
   * Restrict to custom player
   * @default false
   * @platform android
   */
  doesRestrictToCustomPlayer?: boolean;
  
  /**
   * Locale for IMA SDK (alternative to language)
   * @platform ios
   */
  locale?: string;
  
  /**
   * Enable debug mode (alternative naming)
   * @platform ios
   */
  debug?: boolean;
}

/**
 * IMA DAI (Dynamic Ad Insertion) Settings
 * 
 * Must specify either:
 * - VOD stream: `videoId` + `cmsId`
 * - Live stream: `assetKey`
 * 
 * @platforms iOS, Android
 */
export interface JWImaDaiSettings {
  /**
   * Video ID for VOD DAI stream
   * Must be used with `cmsId`
   * Mutually exclusive with `assetKey`
   * 
   * Note: iOS parser accepts `videoID` (uppercase), but `videoId` is preferred
   */
  videoId?: string;
  
  /**
   * Content source ID for VOD DAI stream
   * Must be used with `videoId`
   * Mutually exclusive with `assetKey`
   * 
   * Note: iOS parser accepts `cmsID` (uppercase), but `cmsId` is preferred
   */
  cmsId?: string;
  
  /**
   * Asset key for live DAI stream
   * Mutually exclusive with `videoId` and `cmsId`
   */
  assetKey?: string;
  
  /**
   * API key for verifying the application
   */
  apiKey?: string;
  
  /**
   * Stream type: 'hls' or 'dash'
   * @default 'hls'
   */
  streamType?: StreamType;
  
  /**
   * Additional ad tag parameters
   * Used to override ad tag parameters in the stream request
   */
  adTagParameters?: Record<string, string>;
}

/**
 * Ad break configuration
 * Defines a single ad break in the schedule
 * 
 * @platforms iOS, Android
 */
export interface JWAdBreak {
  /**
   * Ad tag URL or array of URLs
   */
  tag?: string | string[];
  
  /**
   * Ad configuration (alternative structure)
   * Used in some Android configurations
   */
  ad?: {
    /**
     * Ad source identifier
     */
    source?: string;
    
    /**
     * Ad tag URL
     */
    tag?: string;
  };
  
  /**
   * When to play the ad break
   * - "pre": Preroll (before content)
   * - "post": Postroll (after content)  
   * - "123": Midroll at 123 seconds
   * - "50%": Midroll at 50% of content duration
   * 
   * @default "pre"
   */
  offset?: AdOffset;
  
  /**
   * Skip offset in seconds
   * Time before the skip button appears for this break
   */
  skipoffset?: number;
  
  /**
   * Ad break type
   * @default "linear"
   */
  type?: AdType;
  
  /**
   * Custom parameters for the ad request
   */
  custParams?: Record<string, string>;
}

/**
 * Ad schedule - can be array or object with named breaks
 */
export type AdSchedule = JWAdBreak[] | Record<string, JWAdBreak> | string;

/**
 * Ad playback rules
 * Controls how ads are played across a playlist
 * 
 * @platforms iOS (limited), Android (full support)
 */
export interface JWAdRules {
  /**
   * Which playlist item to start showing ads on (1-based)
   * @default 1
   */
  startOn?: number;
  
  /**
   * Show ads every N playlist items
   * @default 1
   */
  frequency?: number;
  
  /**
   * Minimum seconds between midroll ad breaks
   * @default 0
   * @platform android
   */
  timeBetweenAds?: number;
  
  /**
   * When to show ads after seeking
   * - "none": Don't show ads after seeking
   * - "pre": Show preroll after seeking
   * @platform android
   */
  startOnSeek?: StartOnSeek;
}

/**
 * Base advertising configuration
 * Common properties shared across all ad types
 */
export interface BaseAdvertisingConfig {
  /**
   * Skip offset in seconds
   * Time before the skip button appears
   * Can be a number or a percentage string (e.g., "50%")
   */
  skipoffset?: number | string;
  
  /**
   * Ad countdown text (e.g., "Ad - xx")
   * Shown during ad playback
   */
  admessage?: string;
  
  /**
   * Skip delay text (e.g., "Skip in xx")
   * Shown before skip is available
   */
  skipmessage?: string;
  
  /**
   * Skip button text (e.g., "Skip Ad")
   * Shown when skip is available
   */
  skiptext?: string;
  
  /**
   * Cue text for ad markers
   * @platform android
   */
  cuetext?: string;
  
  /**
   * Ad pod message (e.g., "Ad 1 of 3")
   * @platform android
   */
  adpodmessage?: string;
  
  /**
   * Enable VPAID controls
   * @platform android
   */
  vpaidcontrols?: boolean;
  
  /**
   * Request timeout in milliseconds
   * @platform android
   */
  requestTimeout?: number;
  
  /**
   * Creative timeout in milliseconds
   * @platform android
   */
  creativeTimeout?: number;
  
  /**
   * Conditional ad opt-out
   * @platform android
   */
  conditionaladoptout?: boolean;
  
  /**
   * OMID support level
   * @default 'disabled'
   */
  omidSupport?: OmidSupport;
  
  /**
   * List of allowed OMID vendor keys
   */
  allowedOmidVendors?: string[];
  
  /**
   * Ad playback rules for playlist-level control
   * Primary support on Android, limited on iOS
   */
  rules?: JWAdRules;
}

/**
 * VAST Advertising Configuration with Schedule
 * For VAST ads with an array or object schedule
 * 
 * @platforms iOS, Android
 */
export interface VastAdvertisingConfig extends BaseAdvertisingConfig {
  client: 'vast' | 'VAST';
  
  /**
   * Ad schedule - array or object of ad breaks
   */
  schedule?: AdSchedule;
}

/**
 * VMAP Advertising Configuration
 * For VAST ads with a VMAP tag URL
 * 
 * @platforms iOS, Android
 */
export interface VmapAdvertisingConfig extends BaseAdvertisingConfig {
  client: 'vast' | 'VAST';
  
  /**
   * VMAP tag URL or XML string
   */
  tag?: string;
  
  /**
   * Alternative way to specify VMAP URL (as string)
   */
  schedule?: string;
}

/**
 * Google IMA Advertising Configuration
 * For Google IMA ads with a schedule
 * 
 * @platforms iOS, Android
 */
export interface ImaAdvertisingConfig extends BaseAdvertisingConfig {
  client: 'googima' | 'IMA' | 'GOOGIMA';
  
  /**
   * Single ad tag URL
   */
  tag?: string;
  
  /**
   * Ad schedule - array or object of ad breaks
   */
  schedule?: AdSchedule;
  
  /**
   * Google IMA SDK settings
   */
  imaSdkSettings?: JWImaSdkSettings;
  
  /**
   * Alternative naming for IMA settings (iOS)
   * @platform ios
   */
  imaSettings?: JWImaSdkSettings;
  
  /**
   * Enable debug mode
   */
  debug?: boolean;
}

/**
 * Google IMA DAI Advertising Configuration
 * For Google IMA Dynamic Ad Insertion
 * 
 * @platforms iOS, Android
 * 
 * @example VOD Stream
 * ```typescript
 * {
 *   client: 'dai',
 *   imaDaiSettings: {
 *     videoId: 'tears-of-steel',
 *     cmsId: '2528370',
 *     streamType: 'hls'
 *   }
 * }
 * ```
 * 
 * @example Live Stream
 * ```typescript
 * {
 *   client: 'dai',
 *   imaDaiSettings: {
 *     assetKey: 'sN_IYUG8STe1ZzhIIE_ksA',
 *     streamType: 'hls'
 *   }
 * }
 * ```
 */
export interface ImaDaiAdvertisingConfig {
  client: 'dai' | 'GoogleIMADAI' | 'IMA_DAI';
  
  /**
   * IMA DAI settings
   * 
   * Platform-specific naming:
   * - iOS: Prefers `googimadai` but accepts `imaDaiSettings`
   * - Android: Uses `imaDaiSettings`
   * 
   * **Recommendation:** Use `imaDaiSettings` for cross-platform compatibility
   */
  imaDaiSettings?: JWImaDaiSettings;
  
  /**
   * iOS-style naming for DAI settings (alias)
   * @platform ios
   */
  googimadai?: JWImaDaiSettings;
  
  /**
   * Alternative naming used in iOS parser
   * @platform ios
   */
  googleimadaisettings?: JWImaDaiSettings;
  
  /**
   * Google IMA SDK settings
   */
  imaSdkSettings?: JWImaSdkSettings;
  
  /**
   * Alternative naming for IMA settings (iOS)
   * @platform ios
   */
  imaSettings?: JWImaSdkSettings;
  
  /**
   * Skip offset in seconds
   */
  skipoffset?: number;
}

/**
 * Unified Advertising Configuration
 * 
 * Supports all ad types across iOS and Android:
 * - VAST with schedule
 * - VMAP with tag URL
 * - Google IMA with schedule
 * - Google IMA DAI (VOD and Live)
 * 
 * @platforms iOS, Android
 */
export type JWAdvertisingConfig =
  | VastAdvertisingConfig
  | VmapAdvertisingConfig
  | ImaAdvertisingConfig
  | ImaDaiAdvertisingConfig;

