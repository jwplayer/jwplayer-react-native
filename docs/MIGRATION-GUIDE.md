# Migration Guide

Guide for migrating to the unified configuration system in JWPlayer React Native.

## Table of Contents

- [Overview](#overview)
- [Breaking Changes](#breaking-changes)
- [Deprecated Types](#deprecated-types)
- [Migration Steps](#migration-steps)
- [Common Migration Patterns](#common-migration-patterns)
- [Troubleshooting](#troubleshooting)

---

## Overview

### What Changed?

The JWPlayer React Native wrapper has been updated with a **unified type system** that provides:

- ✅ Single source of truth for types
- ✅ Better cross-platform consistency
- ✅ Improved TypeScript support
- ✅ Clearer platform-specific documentation
- ✅ More discoverable configuration options

### Is Migration Required?

**No!** This is a **non-breaking change**. Your existing code will continue to work.

However, we recommend migrating to the new types for:
- Better TypeScript autocomplete
- Clearer error messages
- Future-proof code
- Access to newly documented features

---

## Breaking Changes

### ❌ NONE

This release has **zero breaking changes**. All existing configurations will continue to work as before.

---

## Deprecated Types

The following types are deprecated but still functional:

### Type Aliases

| Deprecated | Use Instead | Status |
|------------|-------------|--------|
| `JwConfig` | `JWPlayerConfig` or `Config` | ⚠️ Deprecated |
| `JwStretching` | `Stretching` | ⚠️ Deprecated |
| `JwThumbnailPreview` | `ThumbnailPreview` | ⚠️ Deprecated |
| `JwAdvertisingConfig` | `JWAdvertisingConfig` | ⚠️ Deprecated |
| `JwAdRules` | `JWAdRules` | ⚠️ Deprecated |
| `JwImaSdkSettings` | `JWImaSdkSettings` | ⚠️ Deprecated |
| `JwImaDaiSettings` | `JWImaDaiSettings` | ⚠️ Deprecated |
| `JwAdBreak` | `JWAdBreak` | ⚠️ Deprecated |
| `JwPlaylistItem` | `JWPlaylistItem` | ⚠️ Deprecated |
| `JwSource` | `JWSource` | ⚠️ Deprecated |
| `JwTrack` | `JWTrack` | ⚠️ Deprecated |
| `JwDrm` | `JWDrm` | ⚠️ Deprecated |
| `JwRelatedConfig` | `JWRelatedConfig` | ⚠️ Deprecated |
| `JwUiConfig` | `JWUiConfig` | ⚠️ Deprecated |
| `JwLogoView` | `JWLogoView` | ⚠️ Deprecated |

### Legacy Objects

These interfaces from older versions are still supported:

| Deprecated | Use Instead |
|------------|-------------|
| `Config` (old) | `JWPlayerConfig` |
| `PlaylistItem` (old) | `JWPlaylistItem` |
| `Source` (old) | `JWSource` |
| `Track` (old) | `JWTrack` |
| `Advertising` (old) | `JWAdvertisingConfig` |
| `Related` (old) | `JWRelatedConfig` |
| `Styling` (old) | `JWStyling` |

---

## Migration Steps

### Step 1: Update Imports (Optional)

**Before:**
```typescript
import JWPlayer, { JwConfig } from '@jwplayer/jwplayer-react-native';
```

**After:**
```typescript
import JWPlayer, { JWPlayerConfig } from '@jwplayer/jwplayer-react-native';
// Or use the Config alias
import JWPlayer, { Config } from '@jwplayer/jwplayer-react-native';
```

### Step 2: Update Type Annotations

**Before:**
```typescript
const config: JwConfig = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8'
};
```

**After:**
```typescript
const config: JWPlayerConfig = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8'
};

// Or use the shorter alias
const config: Config = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8'
};
```

### Step 3: Update Field Names (Recommended)

**Before (still works):**
```typescript
const config = {
  license: 'YOUR_LICENSE',
  playlist: [{
    file: 'https://example.com/video.m3u8',
    mediaid: 'video-123',           // lowercase
    starttime: 10,                  // lowercase
    adschedule: [...]               // lowercase
  }]
};
```

**After (recommended):**
```typescript
const config = {
  license: 'YOUR_LICENSE',
  playlist: [{
    file: 'https://example.com/video.m3u8',
    mediaId: 'video-123',           // camelCase ✅
    starttime: 10,                  // or startTime
    adSchedule: [...]               // camelCase ✅
  }]
};
```

### Step 4: Standardize Advertising Naming

**Before (platform-specific):**
```typescript
// iOS-specific naming
const iosConfig = {
  advertising: {
    client: 'GoogleIMADAI',
    googimadai: {
      videoID: 'tears-of-steel',
      cmsID: '2528370'
    }
  }
};

// Android-specific naming
const androidConfig = {
  advertising: {
    client: 'IMA_DAI',
    imaDaiSettings: {
      videoId: 'tears-of-steel',
      cmsId: '2528370'
    }
  }
};
```

**After (cross-platform):**
```typescript
const config = {
  advertising: {
    client: 'dai',              // Works on both ✅
    imaDaiSettings: {           // Preferred naming ✅
      videoId: 'tears-of-steel', // camelCase ✅
      cmsId: '2528370'          // camelCase ✅
    }
  }
};
```

---

## Common Migration Patterns

### Pattern 1: Basic Video Config

**Before:**
```typescript
import JWPlayer, { JwConfig } from '@jwplayer/jwplayer-react-native';

const config: JwConfig = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8',
  autostart: true
};

export default () => (
  <JWPlayer config={config} style={{ flex: 1 }} />
);
```

**After:**
```typescript
import JWPlayer, { Config } from '@jwplayer/jwplayer-react-native';

const config: Config = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8',
  autostart: true
};

export default () => (
  <JWPlayer config={config} style={{ flex: 1 }} />
);
```

**Change:** Import `Config` instead of `JwConfig` (or use `JWPlayerConfig`).

### Pattern 2: IMA DAI Configuration

**Before:**
```typescript
const config = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8',
  advertising: {
    cmsID: '2528370',
    videoID: 'tears-of-steel',
    client: 'dai',
    adTagParameters: {
      cust_params: 'section=sports'
    }
  }
};
```

**After:**
```typescript
const config: Config = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8',
  advertising: {
    client: 'dai',
    imaDaiSettings: {           // Nested properly ✅
      cmsId: '2528370',         // camelCase ✅
      videoId: 'tears-of-steel', // camelCase ✅
      adTagParameters: {
        cust_params: 'section=sports'
      }
    }
  }
};
```

**Changes:**
- Move DAI settings into `imaDaiSettings` object
- Use camelCase for field names
- Ensure `client` field is present

### Pattern 3: Playlist with Mixed Content

**Before:**
```typescript
const config = {
  license: 'YOUR_LICENSE',
  autostart: true,
  playlist: [
    {
      file: 'https://example.com/video1.m3u8',
      title: 'Video 1',
      imaDaiSettings: {
        videoID: 'video-1',
        cmsID: '12345'
      }
    },
    {
      file: 'https://example.com/video2.m3u8',
      title: 'Video 2'
    }
  ],
  advertising: {
    client: 'dai'
  }
};
```

**After:**
```typescript
const config: Config = {
  license: 'YOUR_LICENSE',
  autostart: true,
  playlist: [
    {
      file: 'https://example.com/video1.m3u8',
      title: 'Video 1',
      imaDaiSettings: {
        videoId: 'video-1',     // camelCase ✅
        cmsId: '12345'          // camelCase ✅
      }
    },
    {
      file: 'https://example.com/video2.m3u8',
      title: 'Video 2'
    }
  ],
  advertising: {
    client: 'dai'
  }
};
```

**Changes:**
- Use camelCase for DAI settings in playlist items
- Add type annotation for better autocomplete

### Pattern 4: Platform-Specific Features

**Before:**
```typescript
const config = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8',
  styling: {  // iOS feature but not documented as such
    colors: {
      buttons: '#FF0000'
    }
  }
};
```

**After:**
```typescript
import { Platform } from 'react-native';
import { Config } from '@jwplayer/jwplayer-react-native';

const config: Config = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8',
  
  // Clearly marked as iOS-only
  ...(Platform.OS === 'ios' && {
    styling: {  // @platform ios
      colors: {
        buttons: '#FF0000'
      }
    }
  })
};
```

**Changes:**
- Platform-specific features now have `@platform` annotations in types
- Use conditional spreading for clarity
- TypeScript will show platform hints in autocomplete

### Pattern 5: Multiple Quality Sources

**Before:**
```typescript
const config = {
  license: 'YOUR_LICENSE',
  sources: [
    {
      file: 'https://example.com/1080p.mp4',
      label: '1080p',
      default: 'true'  // Wrong type!
    }
  ]
};
```

**After:**
```typescript
const config: Config = {
  license: 'YOUR_LICENSE',
  sources: [
    {
      file: 'https://example.com/1080p.mp4',
      label: '1080p',
      default: true  // Correct boolean ✅
    }
  ]
};
```

**Changes:**
- TypeScript will now catch type errors
- `default` must be boolean, not string

---

## Troubleshooting

### Issue: TypeScript Errors After Update

**Error:**
```
Property 'googimadai' does not exist on type 'JWAdvertisingConfig'
```

**Solution:**

The type still supports `googimadai` (iOS) and `imaDaiSettings` (Android). You may need to use a type assertion or update to the recommended `imaDaiSettings`:

```typescript
// Option 1: Use recommended naming
const config: Config = {
  advertising: {
    client: 'dai',
    imaDaiSettings: {  // ✅ Recommended
      videoId: '...',
      cmsId: '...'
    }
  }
};

// Option 2: Type assertion (if you must keep iOS naming)
const config = {
  advertising: {
    client: 'GoogleIMADAI',
    googimadai: {
      videoID: '...',
      cmsID: '...'
    }
  }
} as Config;
```

### Issue: Import Errors

**Error:**
```
Module '@jwplayer/jwplayer-react-native/types' not found
```

**Solution:**

Import from the main package instead:

```typescript
// ✅ Correct
import { Config, JWPlayerConfig } from '@jwplayer/jwplayer-react-native';

// ❌ Avoid (unless you need internal types)
import { Config } from '@jwplayer/jwplayer-react-native/types';
```

### Issue: Config Type Not Recognized

**Problem:** Old `Config` type conflicts with new one.

**Solution:**

Use the full `JWPlayerConfig` name to avoid conflicts:

```typescript
import { JWPlayerConfig } from '@jwplayer/jwplayer-react-native';

const config: JWPlayerConfig = {
  // ... your config
};
```

### Issue: Platform-Specific Feature Warnings

**Warning:** IDE shows "Property 'styling' is iOS-only"

**This is expected!** The new types include `@platform` annotations that help you catch platform-specific features.

**Solution:**

Use platform checks:

```typescript
import { Platform } from 'react-native';

const config: Config = {
  license: 'YOUR_LICENSE',
  file: 'https://example.com/video.m3u8',
  
  ...(Platform.OS === 'ios' && {
    styling: { ... }
  }),
  
  ...(Platform.OS === 'android' && {
    uiConfig: { ... }
  })
};
```

### Issue: Deprecated Type Warnings

**Warning:** `'JwConfig' is deprecated. Use 'Config' or 'JWPlayerConfig' instead`

**Solution:**

Update the type annotation:

```typescript
// Before
const config: JwConfig = { ... };

// After
const config: Config = { ... };
// or
const config: JWPlayerConfig = { ... };
```

These warnings won't affect functionality but should be addressed for future compatibility.

---

## Migration Checklist

Use this checklist when migrating your codebase:

- [ ] Update type imports from `JwConfig` to `Config` or `JWPlayerConfig`
- [ ] Standardize field naming to camelCase (mediaId, adSchedule, etc.)
- [ ] Update IMA DAI configs to use `imaDaiSettings` with camelCase fields
- [ ] Use cross-platform `client` values ('dai' instead of 'GoogleIMADAI' or 'IMA_DAI')
- [ ] Add platform checks for iOS/Android-only features
- [ ] Test configuration on both iOS and Android devices
- [ ] Update any custom type definitions that extend JWPlayer types
- [ ] Run TypeScript compiler to catch any type errors
- [ ] Update documentation/comments referencing old type names

---

## Benefits of Migration

### Before Migration

```typescript
// Old code - still works but less optimal
const config = {
  license: 'LICENSE',
  file: 'video.m3u8',
  advertising: {
    cmsID: '123',  // Direct on advertising object
    videoID: 'abc',
    client: 'GoogleIMADAI'  // Platform-specific
  }
};
```

**Issues:**
- ❌ No TypeScript autocomplete for DAI settings
- ❌ Platform-specific naming reduces portability
- ❌ Unclear structure (flat vs nested)
- ❌ IDE doesn't show available options

### After Migration

```typescript
// New code - recommended
const config: Config = {
  license: 'LICENSE',
  file: 'video.m3u8',
  advertising: {
    client: 'dai',  // Cross-platform ✅
    imaDaiSettings: {  // Clearly structured ✅
      cmsId: '123',    // camelCase ✅
      videoId: 'abc'   // camelCase ✅
    }
  }
};
```

**Benefits:**
- ✅ Full TypeScript autocomplete
- ✅ Cross-platform compatibility
- ✅ Clear structure and organization
- ✅ IDE shows all available options
- ✅ Platform-specific features clearly marked
- ✅ Better error messages

---

## Need Help?

- **General Config Questions**: See [CONFIG-REFERENCE.md](./CONFIG-REFERENCE.md)
- **Platform Differences**: Check [PLATFORM-DIFFERENCES.md](./PLATFORM-DIFFERENCES.md)
- **Type Definitions**: Review `types/` directory
- **Examples**: Check `Example/app/jsx/screens/` for updated examples

---

## Summary

- **No Breaking Changes**: Your existing code continues to work
- **Optional Migration**: Migrate at your own pace
- **Better Types**: New unified types provide better developer experience
- **Clear Documentation**: Platform-specific features are clearly marked
- **Future-Proof**: Prepared for future SDK updates

We recommend migrating incrementally, starting with new features and gradually updating existing configurations as you maintain your codebase.

---

*Last Updated: October 2025*  
*SDK Version: JWPlayer React Native*

