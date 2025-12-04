/**
 * Legacy Type Definitions
 * 
 * These types are maintained for backward compatibility with existing code.
 * New code should use the unified types from unified-config.d.ts
 * 
 * @deprecated Use unified types instead
 */

import { JWPlayerConfig } from './unified-config';
import { JWAdvertisingConfig, JWImaDaiSettings, JWImaSdkSettings } from './advertising';
import { JWPlaylistItem, JWSource, JWTrack } from './playlist';
import {
  JWStyling,
  JWUiConfig,
  JWLogoView,
  JWRelatedConfig,
  AudioSessionCategory,
  AudioSessionCategoryOptions,
  AudioSessionMode,
} from './platform-specific';

/**
 * @deprecated Use JWPlayerConfig instead
 */
export type JwConfig = JWPlayerConfig;

/**
 * @deprecated Use JWPlayerConfig instead
 */
export type Config = JWPlayerConfig;

/**
 * @deprecated Use JWPlaylistItem instead
 */
export type PlaylistItem = JWPlaylistItem;

/**
 * @deprecated Use JWSource instead
 */
export type Source = JWSource;

/**
 * @deprecated Use JWTrack instead
 */
export type Track = JWTrack;

/**
 * @deprecated Use JWAdvertisingConfig instead
 */
export type Advertising = JWAdvertisingConfig;

/**
 * @deprecated Use JWImaDaiSettings instead
 */
export type ImaDaiSettings = JWImaDaiSettings;

/**
 * @deprecated Use JWImaSdkSettings instead
 */
export type ImaSdkSettings = JWImaSdkSettings;

/**
 * @deprecated Use JWStyling instead
 */
export type Styling = JWStyling;

/**
 * @deprecated Use JWUiConfig instead
 */
export type UiConfig = JWUiConfig;

/**
 * @deprecated Use JWLogoView instead
 */
export type LogoView = JWLogoView;

/**
 * @deprecated Use JWRelatedConfig instead
 */
export type Related = JWRelatedConfig;

