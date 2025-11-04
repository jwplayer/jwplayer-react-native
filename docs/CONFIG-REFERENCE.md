# JWPlayer React Native Configuration Reference

Complete reference for the unified configuration system supporting both iOS and Android platforms.

## Table of Contents

- [Overview](#overview)
- [Quick Start](#quick-start)
- [Core Configuration](#core-configuration)
  - [Media Content](#media-content)
  - [Playback Settings](#playback-settings)
  - [Advertising](#advertising)
  - [Related Content](#related-content)
- [Platform-Specific Features](#platform-specific-features)
  - [iOS Only](#ios-only-features)
  - [Android Only](#android-only-features)
- [Advertising Configuration](#advertising-configuration-detailed)
- [Playlist Items](#playlist-items)
- [Complete Examples](#complete-examples)

---

## Overview

The JWPlayer React Native wrapper uses a unified configuration system that works across both iOS (`JWJSONParser`) and Android (`JsonHelper`) platforms. The configuration is passed as a JavaScript object and automatically parsed by the native SDK.

### Key Features

- ✅ **Single Configuration Object**: One config works on both platforms
- ✅ **Type Safety**: Full TypeScript support with autocomplete
- ✅ **Platform Annotations**: Clear documentation of platform-specific features
- ✅ **Backward Compatible**: Existing configs continue to work
- ✅ **Flexible Naming**: Supports multiple naming conventions where needed

### Import Types

```typescript
import { JWPlayerConfig } from '@jwplayer/jwplayer-react-native';
// Or import from types directory
import type { JWPlayerConfig } from '@jwplayer/jwplayer-react-native/types';
```

---

## Quick Start

### Minimal Configuration

```typescript
import JWPlayer from '@jwplayer/jwplayer-react-native';

const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY', // Required
  file: 'https://example.com/video.m3u8'
};

<JWPlayer config={config} />
```

### With Autostart and Poster

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  image: 'https://example.com/poster.jpg',
  autostart: true,
  mute: false
};
```

---

## Core Configuration

### Media Content

#### Single File

Use `file` for a single video:

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  title: 'My Video',
  description: 'Video description',
  image: 'https://example.com/poster.jpg'
};
```

**Supported Fields:**
- `file` (string): URL of the media file
- `title` (string): Video title  
- `description` (string): Video description
- `image` (string): Poster image URL
- `tracks` (JWTrack[]): Caption/thumbnail tracks

#### Multiple Quality Sources

Use `sources` for multiple quality levels:

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  sources: [
    {
      file: 'https://example.com/video-1080p.mp4',
      label: '1080p',
      default: true
    },
    {
      file: 'https://example.com/video-720p.mp4',
      label: '720p'
    },
    {
      file: 'https://example.com/video-480p.mp4',
      label: '480p'
    }
  ]
};
```

**JWSource Fields:**
- `file` (string): Source URL - **Required**
- `label` (string): Quality label (e.g., "1080p")
- `default` (boolean): Whether this is the default quality
- `type` (string): Media type (e.g., "mp4", "hls")
- `drm` (JWDrm): DRM configuration

#### Playlist

Use `playlist` for multiple videos:

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  playlist: [
    {
      file: 'https://example.com/video1.m3u8',
      title: 'Video 1',
      image: 'https://example.com/poster1.jpg'
    },
    {
      file: 'https://example.com/video2.m3u8',
      title: 'Video 2',
      image: 'https://example.com/poster2.jpg'
    }
  ],
  autostart: true
};
```

**Or load from a remote playlist:**

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  playlist: 'https://cdn.jwplayer.com/v2/media/MEDIAID?format=json',
  autostart: true
};
```

**Platform Support:** iOS, Android  
**Mutually Exclusive:** Can only use ONE of: `file`, `sources`, or `playlist`

---

### Playback Settings

#### Basic Playback

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  
  autostart: true,              // Auto-play on load
  mute: false,                   // Initial mute state
  repeat: false,                 // Loop content
  preload: 'auto',               // Preload behavior: 'auto' | 'none'
  stretching: 'uniform',         // Video scaling: 'uniform' | 'fill' | 'exactfit' | 'none'
  playlistIndex: 0,              // Start playlist at index (0-based)
  
  // Playback speed controls
  playbackRates: [0.5, 0.75, 1, 1.25, 1.5, 2],
  playbackRateControls: true,
  
  // Bandwidth limit (bits per second)
  bitrateUpperBound: 2000000
};
```

**Field Reference:**

| Field | Type | Default | Description | Platforms |
|-------|------|---------|-------------|-----------|
| `autostart` | boolean | false | Auto-play on load | iOS, Android |
| `mute` | boolean | false | Mute audio initially | iOS, Android |
| `repeat` | boolean | false | Loop content | iOS, Android |
| `preload` | 'auto' \| 'none' | 'auto' | Preload behavior | iOS, Android |
| `stretching` | Stretching | 'uniform' | Video scaling mode | iOS, Android |
| `playlistIndex` | number | 0 | Starting playlist index | iOS, Android |
| `playbackRates` | number[] | - | Available speed rates (0.25-4.0) | iOS, Android |
| `playbackRateControls` | boolean | false | Show speed controls | iOS, Android |
| `bitrateUpperBound` | number | - | Max bitrate (bps) | iOS, Android |
| `pid` / `playerId` | string | - | Player ID for analytics (8 chars) | iOS, Android |

---

### Advertising

See [Advertising Configuration (Detailed)](#advertising-configuration-detailed) section below for complete advertising documentation.

**Quick Example - IMA DAI VOD:**

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  advertising: {
    client: 'dai',
    imaDaiSettings: {
      videoId: 'tears-of-steel',
      cmsId: '2528370',
      streamType: 'hls'
    }
  }
};
```

---

### Related Content

Configure the related content overlay that appears after playback:

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  related: {
    file: 'https://example.com/related.json',    // Related feed URL
    onclick: 'play',                              // 'play' | 'link'
    oncomplete: 'show',                           // 'show' | 'hide' | 'autoplay'
    autoplaytimer: 10                             // Countdown seconds
  }
};
```

**Platform Notes:**
- iOS: Full support with additional options (`heading`, `autoplayMessage`)
- Android: Basic support with `file`, `onclick`, `oncomplete`, `autoplaytimer`

---

## Platform-Specific Features

### iOS Only Features

#### Styling

Customize colors, fonts, and UI appearance (iOS only):

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  styling: {
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
    showDesc: true,
    captionsStyle: {
      fontColor: '#FFFF00',
      fontSize: 16,
      backgroundColor: '#000000',
      backgroundOpacity: 75,
      edgeStyle: 'dropshadow'
    }
  }
};
```

**⚠️ Android:** Use XML styling instead. See [Android Styling Guide](https://docs.jwplayer.com/players/docs/android-styling-guide)

#### Background Audio

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  backgroundAudioEnabled: true,
  category: 'Playback',
  categoryOptions: ['MixWithOthers', 'DuckOthers'],
  mode: 'MoviePlayback'
};
```

#### Interface Control

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  interfaceBehavior: 'normal',        // 'normal' | 'hidden' | 'onscreen'
  interfaceFadeDelay: 3,               // Seconds before UI fades
  hideUIGroups: ['settings_menu', 'casting_menu']
};
```

#### FairPlay DRM

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  sources: [{
    file: 'https://example.com/video.m3u8',
    drm: {
      fairplay: {
        processSpcUrl: 'https://your-drm-server.com/process-spc',
        certificateUrl: 'https://your-drm-server.com/certificate'
      }
    }
  }]
};
```

---

### Android Only Features

#### UI Configuration

Control which UI elements are visible (Android only):

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  uiConfig: {
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

**⚠️ Note:** When using `uiConfig`, all unspecified elements default to `false`. Specify all elements you want visible.

#### Android-Specific Settings

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  useTextureView: true,                         // Use TextureView instead of SurfaceView
  allowCrossProtocolRedirectsSupport: true,     // Allow HTTP->HTTPS redirects
  displaytitle: true,
  displaydescription: true,
  thumbnailPreview: 102                         // Thumbnail preview quality (101-103)
};
```

#### Widevine DRM

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  sources: [{
    file: 'https://example.com/video.mpd',
    drm: {
      widevine: {
        url: 'https://your-drm-server.com/license',
        keySetId: 'optional-key-set-id'
      }
    }
  }]
};
```

---

## Advertising Configuration (Detailed)

The wrapper supports multiple advertising systems across both platforms.

### Supported Ad Types

| Ad Type | iOS | Android | Client Value |
|---------|-----|---------|--------------|
| VAST with Schedule | ✅ | ✅ | `'vast'` |
| VMAP | ✅ | ✅ | `'vast'` |
| Google IMA | ✅ | ✅ | `'googima'` or `'IMA'` |
| Google IMA DAI | ✅ | ✅ | `'dai'` or `'IMA_DAI'` or `'GoogleIMADAI'` |

### IMA DAI Configuration

#### VOD Stream (Video on Demand)

Requires `videoId` + `cmsId`:

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  advertising: {
    client: 'dai',
    imaDaiSettings: {
      videoId: 'tears-of-steel',
      cmsId: '2528370',
      streamType: 'hls',  // 'hls' or 'dash'
      apiKey: 'your-api-key',  // Optional
      adTagParameters: {
        'custom_param': 'value'
      }
    },
    imaSdkSettings: {
      language: 'en',
      autoPlayAdBreaks: true
    },
    skipoffset: 5  // Skip button after 5 seconds
  }
};
```

#### Live Stream

Requires `assetKey`:

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/live.m3u8',
  advertising: {
    client: 'dai',
    imaDaiSettings: {
      assetKey: 'sN_IYUG8STe1ZzhIIE_ksA',
      streamType: 'hls'
    }
  }
};
```

**Platform Note:** Both iOS and Android support `imaDaiSettings`. iOS also accepts `googimadai` as an alias.

### VAST Advertising

#### With Schedule

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  advertising: {
    client: 'vast',
    schedule: [
      {
        offset: 'pre',
        tag: 'https://example.com/preroll.xml'
      },
      {
        offset: '50%',  // Midroll at 50%
        tag: 'https://example.com/midroll.xml',
        skipoffset: 5
      },
      {
        offset: 'post',
        tag: 'https://example.com/postroll.xml'
      }
    ],
    skipoffset: 5,
    admessage: 'Ad - {remaining}',
    skipmessage: 'Skip in {remaining}',
    skiptext: 'Skip Ad'
  }
};
```

#### With VMAP

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  advertising: {
    client: 'vast',
    tag: 'https://example.com/vmap.xml',  // VMAP URL
    skipoffset: 5
  }
};
```

### Google IMA

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  advertising: {
    client: 'googima',
    schedule: [
      {
        offset: 'pre',
        tag: 'https://pubads.g.doubleclick.net/gampad/ads?...'
      }
    ],
    imaSdkSettings: {
      language: 'en',
      autoPlayAdBreaks: true,
      maxRedirects: 5,
      isDebugMode: false
    }
  }
};
```

### Ad Rules (Android Primary)

Control ad playback across a playlist:

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  playlist: [...],
  advertising: {
    client: 'vast',
    schedule: [...],
    rules: {
      startOn: 2,           // Start ads on 2nd playlist item
      frequency: 2,         // Show ads every 2 items
      timeBetweenAds: 300,  // Min 5 minutes between midrolls
      startOnSeek: 'pre'    // Show preroll after seeking
    }
  }
};
```

**Platform Note:** Android has full support. iOS has limited support for `startOn` and `frequency`.

---

## Playlist Items

Complete configuration for playlist items:

```typescript
const playlistItem: JWPlaylistItem = {
  // Media source (Required: file OR sources)
  file: 'https://example.com/video.m3u8',
  // OR
  sources: [
    { file: 'https://example.com/1080p.mp4', label: '1080p' },
    { file: 'https://example.com/720p.mp4', label: '720p' }
  ],
  
  // Metadata
  title: 'Video Title',
  description: 'Video description',
  image: 'https://example.com/poster.jpg',
  mediaId: 'unique-id',
  
  // Playback
  starttime: 0,      // Start position in seconds
  duration: 600,     // Duration in seconds
  
  // Tracks
  tracks: [
    {
      file: 'https://example.com/captions-en.vtt',
      kind: 'captions',
      label: 'English',
      locale: 'en',
      default: true
    }
  ],
  
  // Item-level advertising
  imaDaiSettings: {
    videoId: 'video-id',
    cmsId: 'cms-id'
  },
  
  // Related content
  recommendations: 'https://example.com/related.json',
  
  // Chromecast
  userInfo: {
    customData: 'value'
  },
  
  // DRM (platform-specific)
  drm: {
    fairplay: {  // iOS
      processSpcUrl: '...',
      certificateUrl: '...'
    },
    widevine: {  // Android
      url: '...',
      keySetId: '...'
    }
  }
};
```

---

## Complete Examples

### Basic Video with Captions

```typescript
import JWPlayer, { JWPlayerConfig } from '@jwplayer/jwplayer-react-native';

const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  file: 'https://example.com/video.m3u8',
  title: 'Sample Video',
  image: 'https://example.com/poster.jpg',
  tracks: [
    {
      file: 'https://example.com/captions-en.vtt',
      kind: 'captions',
      label: 'English',
      locale: 'en',
      default: true
    },
    {
      file: 'https://example.com/captions-es.vtt',
      kind: 'captions',
      label: 'Español',
      locale: 'es'
    }
  ],
  autostart: true
};

export default () => (
  <JWPlayer config={config} style={{ flex: 1 }} />
);
```

### IMA DAI with Playlist

```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE_KEY',
  autostart: true,
  playlist: [
    {
      title: 'VOD with Ads',
      file: 'https://example.com/vod.m3u8',
      imaDaiSettings: {
        videoId: 'tears-of-steel',
        cmsId: '2528370'
      }
    },
    {
      title: 'Live Stream with Ads',
      file: 'https://example.com/live.m3u8',
      imaDaiSettings: {
        assetKey: 'sN_IYUG8STe1ZzhIIE_ksA'
      }
    },
    {
      title: 'Video without Ads',
      file: 'https://example.com/no-ads.m3u8'
    }
  ],
  advertising: {
    client: 'dai'
  }
};
```

### Multi-Quality with DRM

```typescript
import { Platform } from 'react-native';

const config: JWPlayerConfig = {
  license: Platform.OS === 'ios' ? 'IOS_LICENSE' : 'ANDROID_LICENSE',
  sources: [
    {
      file: Platform.OS === 'ios' 
        ? 'https://example.com/fairplay.m3u8'
        : 'https://example.com/widevine.mpd',
      label: '1080p',
      default: true,
      drm: Platform.OS === 'ios' ? {
        fairplay: {
          processSpcUrl: 'https://drm.example.com/spc',
          certificateUrl: 'https://drm.example.com/cert'
        }
      } : {
        widevine: {
          url: 'https://drm.example.com/license'
        }
      }
    }
  ],
  autostart: true
};
```

---

## See Also

- [Platform Differences Guide](./PLATFORM-DIFFERENCES.md) - Detailed platform-specific information
- [Migration Guide](./MIGRATION-GUIDE.md) - Upgrading from legacy configs
- [Props Documentation](./props.md) - Component props reference
- [iOS JSON Parser Docs](../ios-json-parser/README.md) - iOS parser details
- [Android JSON Parser Docs](../android-json-parser/README.md) - Android parser details

---

## Need Help?

- **Configuration Issues**: Check [PLATFORM-DIFFERENCES.md](./PLATFORM-DIFFERENCES.md)
- **Migration Questions**: See [MIGRATION-GUIDE.md](./MIGRATION-GUIDE.md)
- **TypeScript Errors**: Import types from `'@jwplayer/jwplayer-react-native/types'`
- **Platform-Specific**: Review [iOS](../ios-json-parser/) or [Android](../android-json-parser/) parser docs

---

*Last Updated: October 2025*  
*SDK Version: JWPlayer React Native*

