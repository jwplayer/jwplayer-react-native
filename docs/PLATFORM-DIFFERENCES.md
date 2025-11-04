# Platform Differences Guide

This guide documents the differences between iOS and Android platforms when using JWPlayer React Native.

## Table of Contents

- [Overview](#overview)
- [Configuration Naming Differences](#configuration-naming-differences)
- [iOS-Specific Features](#ios-specific-features)
- [Android-Specific Features](#android-specific-features)
- [Cross-Platform Best Practices](#cross-platform-best-practices)
- [Common Gotchas](#common-gotchas)

---

## Overview

The JWPlayer React Native wrapper provides a **unified configuration interface** that works across both platforms. However, some features are platform-specific due to native SDK differences.

### Platform Detection

```typescript
import { Platform } from 'react-native';

const config: JWPlayerConfig = {
  license: Platform.OS === 'ios' ? 'IOS_LICENSE' : 'ANDROID_LICENSE',
  file: 'https://example.com/video.m3u8',
  
  // Platform-specific feature
  ...(Platform.OS === 'ios' && {
    styling: {
      colors: { buttons: '#FF0000' }
    }
  }),
  
  ...(Platform.OS === 'android' && {
    uiConfig: {
      hasControlbar: true,
      hasOverlay: true
    }
  })
};
```

---

## Configuration Naming Differences

### IMA DAI Settings

**Field Name Differences:**

| Feature | iOS Parser | Android Parser | **Recommended** |
|---------|-----------|----------------|-----------------|
| DAI Settings Object | `googimadai` | `imaDaiSettings` | `imaDaiSettings` ✅ |
| DAI Settings (alternate) | `googleimadaisettings` | `imaDaiSettings` | `imaDaiSettings` ✅ |
| IMA Settings Object | `imaSettings` | `imaSdkSettings` | `imaSdkSettings` ✅ |
| Video ID | `videoID` | `videoId` | `videoId` ✅ |
| CMS ID | `cmsID` | `cmsId` | `cmsId` ✅ |

**✅ Recommended Cross-Platform Config:**

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  advertising: {
    client: 'dai',  // Works on both platforms
    imaDaiSettings: {  // Preferred naming
      videoId: 'tears-of-steel',  // camelCase
      cmsId: '2528370',           // camelCase
      streamType: 'hls'
    },
    imaSdkSettings: {  // Preferred naming
      language: 'en'
    }
  }
};
```

**iOS-Specific Naming (also supported):**

```typescript
// iOS can also use these alternate names
const iosConfig = {
  advertising: {
    client: 'GoogleIMADAI',  // iOS naming
    googimadai: {            // iOS naming
      videoID: '...',        // Uppercase ID
      cmsID: '...'           // Uppercase ID
    },
    imaSettings: {           // iOS uses "imaSettings"
      locale: 'en'
    }
  }
};
```

### Ad Client Values

| Ad Type | iOS Values | Android Values | **Recommended** |
|---------|-----------|----------------|-----------------|
| VAST | `'vast'`, `'VAST'` | `'vast'`, `'VAST'` | `'vast'` ✅ |
| Google IMA | `'googima'` | `'googima'`, `'IMA'`, `'GOOGIMA'` | `'googima'` ✅ |
| Google DAI | `'dai'`, `'GoogleIMADAI'` | `'IMA_DAI'` | `'dai'` ✅ |

**Example:**

```typescript
// ✅ Cross-platform
advertising: { client: 'dai' }

// ❌ Platform-specific
advertising: { client: 'GoogleIMADAI' }  // iOS only
advertising: { client: 'IMA_DAI' }       // Android only
```

### Playlist Item Fields

| Field | iOS | Android | **Recommended** |
|-------|-----|---------|-----------------|
| Media ID | `mediaid` | `mediaid` | `mediaId` ✅ |
| Ad Schedule | `adschedule` | `adschedule` | `adSchedule` ✅ |
| Feed ID | `feedid` | `feedid` | `feedId` ✅ |
| DAI Settings | `daiSetting` | `imaDaiSettings` | `imaDaiSettings` ✅ |
| Start Time | `starttime` | `starttime` | `startTime` ✅ |

---

## iOS-Specific Features

### 1. Styling Configuration

**iOS Only** - Android uses XML styling

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  styling: {  // ⚠️ iOS ONLY
    colors: {
      buttons: '#FF0000',
      backgroundColor: '#000000',
      fontColor: '#FFFFFF',
      timeslider: {
        thumb: '#FF0000',
        rail: '#808080',
        slider: '#FF0000'
      }
    },
    font: {
      name: 'Helvetica',
      size: 14
    },
    showTitle: true,
    showDesc: true
  }
};
```

**Android Alternative:**

Use XML styling instead. See [Android Styling Guide](https://docs.jwplayer.com/players/docs/android-styling-guide).

### 2. Audio Session Configuration

**iOS Only** - Controls background audio behavior

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  backgroundAudioEnabled: true,  // ⚠️ iOS ONLY
  category: 'Playback',          // ⚠️ iOS ONLY
  categoryOptions: [             // ⚠️ iOS ONLY
    'MixWithOthers',
    'DuckOthers'
  ],
  mode: 'MoviePlayback'          // ⚠️ iOS ONLY
};
```

**Audio Session Categories:**
- `Ambient`: Mix with other audio, silence when locked
- `SoloAmbient`: Default, silences other audio
- `Playback`: For media playback
- `Record`: For recording
- `PlayAndRecord`: For VoIP
- `MultiRoute`: Multiple audio routes

**Category Options:**
- `MixWithOthers`: Mix with other audio
- `DuckOthers`: Lower volume of other audio
- `AllowBluetooth`: Allow Bluetooth A2DP
- `DefaultToSpeaker`: Route to speaker by default
- `InterruptSpokenAudioAndMix`: Interrupt spoken audio
- `AllowAirPlay`: Allow AirPlay

### 3. Caption Styling

**iOS Only** - Customize caption appearance

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  styling: {  // ⚠️ iOS ONLY
    captionsStyle: {
      fontColor: '#FFFF00',
      fontSize: 16,
      backgroundColor: '#000000',
      backgroundOpacity: 75,
      windowColor: '#000000',
      windowOpacity: 0,
      edgeStyle: 'dropshadow',  // 'none' | 'dropshadow' | 'raised' | 'depressed' | 'uniform'
      fontFamily: 'Helvetica'
    }
  }
};
```

**Android:** Uses system accessibility settings for captions.

### 4. Interface Control

**iOS Only** - Control UI behavior

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  interfaceBehavior: 'normal',  // ⚠️ iOS ONLY: 'normal' | 'hidden' | 'onscreen'
  interfaceFadeDelay: 3,         // ⚠️ iOS ONLY: Seconds before UI fades
  hideUIGroups: [                // ⚠️ iOS ONLY
    'settings_menu',
    'casting_menu',
    'quality_submenu'
  ]
};
```

### 5. FairPlay DRM

**iOS Only** - FairPlay DRM

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  sources: [{
    file: 'https://example.com/video.m3u8',
    drm: {
      fairplay: {  // ⚠️ iOS ONLY
        processSpcUrl: 'https://drm-server.com/spc',
        certificateUrl: 'https://drm-server.com/cert'
      }
    }
  }],
  // Global FairPlay settings (iOS only)
  processSpcUrl: 'https://drm-server.com/spc',
  fairplayCertUrl: 'https://drm-server.com/cert',
  contentUUID: 'unique-content-id'
};
```

### 6. External Playback (AirPlay)

**iOS Only**

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  externalPlaybackSettings: {  // ⚠️ iOS ONLY
    playbackEnabled: true,
    usesExternalPlaybackWhileExternalScreenIsActive: true,
    videoGravity: 'resizeAspect'
  }
};
```

---

## Android-Specific Features

### 1. UI Configuration

**Android Only** - Granular UI control

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  uiConfig: {  // ⚠️ ANDROID ONLY
    hasOverlay: true,
    hasControlbar: true,
    hasCenterControls: true,
    hasNextUp: true,
    hasQualitySubMenu: true,
    hasCaptionsSubMenu: true,
    hasPlaybackRatesSubMenu: true,
    hasMenu: true,
    hasAds: true
  }
};
```

**⚠️ Important:** When using `uiConfig`, ALL unspecified elements default to `false`. You must explicitly enable each element you want visible.

**iOS Alternative:** Use `hideUIGroups` or `interfaceBehavior`.

### 2. TextureView

**Android Only**

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  useTextureView: true  // ⚠️ ANDROID ONLY
};
```

**Use Case:** Required for applying transformations (rotation, scaling) to the video view.

### 3. Ad Rules

**Android Primary** (iOS has limited support)

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  playlist: [...],
  advertising: {
    client: 'vast',
    schedule: [...],
    rules: {
      startOn: 2,           // ✅ Both platforms
      frequency: 2,         // ✅ Both platforms
      timeBetweenAds: 300,  // ⚠️ ANDROID ONLY
      startOnSeek: 'pre'    // ⚠️ ANDROID ONLY
    }
  }
};
```

**Platform Support:**
- `startOn`: ✅ iOS, ✅ Android
- `frequency`: ✅ iOS, ✅ Android
- `timeBetweenAds`: ❌ iOS, ✅ Android
- `startOnSeek`: ❌ iOS, ✅ Android

### 4. Widevine DRM

**Android Only** - Widevine DRM

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  sources: [{
    file: 'https://example.com/video.mpd',
    drm: {
      widevine: {  // ⚠️ ANDROID ONLY
        url: 'https://license-server.com/license',
        keySetId: 'optional-key-id'
      }
    }
  }]
};
```

### 5. HTTP Headers

**Android Only**

```typescript
const playlistItem: JWPlaylistItem = {
  file: 'https://example.com/video.m3u8',
  httpheaders: {  // ⚠️ ANDROID ONLY
    'Authorization': 'Bearer token',
    'Custom-Header': 'value'
  }
};
```

### 6. Cross-Protocol Redirects

**Android Only**

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  allowCrossProtocolRedirectsSupport: true  // ⚠️ ANDROID ONLY
};
```

### 7. Display Title/Description

**Android Only** (iOS always shows based on `showTitle`/`showDesc` in styling)

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  displaytitle: true,        // ⚠️ ANDROID ONLY
  displaydescription: true   // ⚠️ ANDROID ONLY
};
```

---

## Cross-Platform Best Practices

### 1. Use Conditional Configuration

```typescript
import { Platform } from 'react-native';

const config: JWPlayerConfig = {
  license: Platform.OS === 'ios' ? IOS_LICENSE : ANDROID_LICENSE,
  file: 'https://example.com/video.m3u8',
  autostart: true,
  
  // iOS-specific features
  ...(Platform.OS === 'ios' && {
    styling: {
      colors: { buttons: '#FF0000' }
    },
    backgroundAudioEnabled: true
  }),
  
  // Android-specific features
  ...(Platform.OS === 'android' && {
    uiConfig: {
      hasControlbar: true,
      hasOverlay: true
    },
    useTextureView: true
  })
};
```

### 2. Unified Naming Convention

Always use the **recommended** cross-platform naming:

```typescript
// ✅ GOOD - Cross-platform
const config: JWPlayerConfig = {
  advertising: {
    client: 'dai',
    imaDaiSettings: {
      videoId: 'tears-of-steel',
      cmsId: '2528370'
    },
    imaSdkSettings: {
      language: 'en'
    }
  }
};

// ❌ AVOID - Platform-specific naming
const config: JWPlayerConfig = {
  advertising: {
    client: 'GoogleIMADAI',  // iOS-specific
    googimadai: {             // iOS-specific
      videoID: '...',         // Uppercase (iOS)
      cmsID: '...'            // Uppercase (iOS)
    }
  }
};
```

### 3. Handle DRM Properly

```typescript
import { Platform } from 'react-native';

const getDrmConfig = () => {
  if (Platform.OS === 'ios') {
    return {
      fairplay: {
        processSpcUrl: 'https://drm.example.com/fps',
        certificateUrl: 'https://drm.example.com/cert'
      }
    };
  } else {
    return {
      widevine: {
        url: 'https://drm.example.com/widevine'
      }
    };
  }
};

const config: JWPlayerConfig = {
  license: Platform.OS === 'ios' ? IOS_LICENSE : ANDROID_LICENSE,
  sources: [{
    file: Platform.OS === 'ios' 
      ? 'https://example.com/fairplay.m3u8'
      : 'https://example.com/widevine.mpd',
    label: '1080p',
    drm: getDrmConfig()
  }]
};
```

### 4. Test on Both Platforms

Always test configurations on both iOS and Android devices:

```typescript
// Helper function for testing
export const validateConfig = (config: JWPlayerConfig): string[] => {
  const warnings: string[] = [];
  
  if (Platform.OS === 'android' && config.styling) {
    warnings.push('`styling` is iOS-only. Use XML styling on Android.');
  }
  
  if (Platform.OS === 'ios' && config.uiConfig) {
    warnings.push('`uiConfig` is Android-only. iOS uses native UI.');
  }
  
  if (Platform.OS === 'ios' && config.useTextureView) {
    warnings.push('`useTextureView` is Android-only.');
  }
  
  return warnings;
};
```

---

## Common Gotchas

### 1. IMA DAI Naming Confusion

❌ **Wrong:**
```typescript
// Mixing iOS and Android naming
advertising: {
  client: 'GoogleIMADAI',  // iOS naming
  imaDaiSettings: {        // Android naming
    videoID: '...',        // iOS casing
    cmsId: '...'           // Android casing
  }
}
```

✅ **Correct:**
```typescript
advertising: {
  client: 'dai',        // Cross-platform
  imaDaiSettings: {     // Preferred
    videoId: '...',     // camelCase
    cmsId: '...'        // camelCase
  }
}
```

### 2. UI Configuration Defaults

❌ **Wrong (Android):**
```typescript
// Only specifying some UI elements
uiConfig: {
  hasControlbar: true
  // Oops! All other elements are now hidden
}
```

✅ **Correct (Android):**
```typescript
// Specify ALL elements you want visible
uiConfig: {
  hasOverlay: true,
  hasControlbar: true,
  hasCenterControls: true,
  hasNextUp: true,
  hasQualitySubMenu: true,
  hasCaptionsSubMenu: true,
  hasMenu: true,
  hasAds: true
}
```

### 3. License Keys

❌ **Wrong:**
```typescript
// Using same license for both platforms
const config = {
  license: 'SINGLE_LICENSE_KEY'  // Won't work!
};
```

✅ **Correct:**
```typescript
import { Platform } from 'react-native';

const config = {
  license: Platform.OS === 'ios' 
    ? process.env.IOS_LICENSE_KEY
    : process.env.ANDROID_LICENSE_KEY
};
```

### 4. Styling vs UI Config

❌ **Wrong:**
```typescript
// Trying to use both
const config = {
  styling: { ... },   // iOS
  uiConfig: { ... }   // Android
  // Will be ignored on wrong platform
};
```

✅ **Correct:**
```typescript
const config = {
  ...(Platform.OS === 'ios' && {
    styling: { ... }
  }),
  ...(Platform.OS === 'android' && {
    uiConfig: { ... }
  })
};
```

### 5. Ad Rules Assumptions

❌ **Wrong:**
```typescript
// Assuming full ad rules support on iOS
advertising: {
  client: 'vast',
  rules: {
    startOn: 2,
    timeBetweenAds: 300  // iOS doesn't support this!
  }
}
```

✅ **Correct:**
```typescript
advertising: {
  client: 'vast',
  rules: {
    startOn: 2,
    frequency: 2,
    ...(Platform.OS === 'android' && {
      timeBetweenAds: 300,
      startOnSeek: 'pre'
    })
  }
}
```

---

## Summary Table

| Feature | iOS | Android | Recommendation |
|---------|-----|---------|----------------|
| **Styling** | ✅ Full | ❌ Use XML | Platform-specific |
| **UI Config** | ❌ Use hideUIGroups | ✅ Full | Platform-specific |
| **Background Audio** | ✅ Full | ❌ | iOS only |
| **Caption Styling** | ✅ Full | ❌ System settings | iOS only |
| **FairPlay DRM** | ✅ | ❌ | iOS only |
| **Widevine DRM** | ❌ | ✅ | Android only |
| **Ad Rules (full)** | ⚠️ Limited | ✅ Full | Platform-aware |
| **HTTP Headers** | ❌ | ✅ | Android only |
| **TextureView** | ❌ | ✅ | Android only |
| **AirPlay** | ✅ | ❌ | iOS only |
| **IMA DAI** | ✅ | ✅ | Use `imaDaiSettings` |
| **VAST/IMA** | ✅ | ✅ | Fully cross-platform |

---

## See Also

- [Configuration Reference](./CONFIG-REFERENCE.md) - Complete config documentation
- [Migration Guide](./MIGRATION-GUIDE.md) - Upgrading from legacy configs
- [TypeScript Example](../Example/app/jsx/screens/TypeScriptExample.tsx) - Example with platform-specific configurations

---

*Last Updated: October 2025*  
*SDK Version: JWPlayer React Native*

