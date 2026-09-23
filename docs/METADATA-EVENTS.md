# Metadata Events

`jwplayer-react-native` surfaces every kind of metadata the native JW Player SDKs expose through two callbacks that mirror the [web player's `meta` and `metadataCueParsed` events](https://docs.jwplayer.com/players/reference/metadata-events):

| Prop | Fires when | Web player equivalent |
|------|-----------|-----------------------|
| **`onMetadataCueParsed`** | A metadata cue is first parsed from the manifest or a media segment, ahead of playback reaching it. Useful for building a cue timeline up front. | `on('metadataCueParsed')` |
| **`onMeta`** | Playback enters the time range of a cue, or non-timed media / access-log metadata is received. | `on('meta')` |

Both callbacks deliver the same payload shape, so one handler type covers everything:

```ts
{
  metadataType: 'id3' | 'emsg' | 'date-range' | 'program-date-time' | 'external' | 'media' | 'access-log' | 'unknown',
  metadataTime?: number,      // cue start, seconds relative to the stream (omitted when unknown or negative)
  programDateTime?: string,   // ISO 8601, only on program-date-time (hoisted like the web player)
  metadata?: { ... },         // per-type body, see below
  // media only, flat like the web player:
  duration?, height?, width?, frameRate?, seekRange?, drm?
}
```

As with every other callback, the payload is wrapped in `nativeEvent`:

```jsx
import JWPlayer from '@jwplayer/jwplayer-react-native';

<JWPlayer
  config={config}
  onMetadataCueParsed={({nativeEvent}) => {
    console.log('cue parsed', nativeEvent.metadataType, nativeEvent.metadataTime, nativeEvent.metadata);
  }}
  onMeta={({nativeEvent}) => {
    switch (nativeEvent.metadataType) {
      case 'id3':
        console.log('ID3', nativeEvent.metadata.title ?? nativeEvent.metadata);
        break;
      case 'date-range': {
        const scte = nativeEvent.metadata.attributes.find(a => a.name.startsWith('SCTE35'));
        if (scte) console.log('SCTE-35 marker', scte.name, scte.value);
        break;
      }
      case 'program-date-time':
        console.log('wall clock', nativeEvent.programDateTime);
        break;
      case 'external':
        console.log('reached cue point', nativeEvent.metadata.identifier);
        break;
    }
  }}
/>
```

The TypeScript definitions in `index.d.ts` model the payload as a discriminated union (`MetaEventProps` / `MetadataCueParsedEventProps`), so narrowing on `metadataType` gives you the right `metadata` shape.

The **Metadata Events** screen in the [Example app](../Example/app/jsx/screens/MetadataExample.js) plays public test streams for each type and logs every event on screen.

---

## Platform coverage

| `metadataType` | Source | `onMetadataCueParsed` | `onMeta` |
|----------------|--------|:---------------------:|:--------:|
| `id3` | ID3 timed metadata in HLS / TS segments | Android | iOS, Android |
| `date-range` | `#EXT-X-DATERANGE` (including SCTE-35 attributes) | iOS, Android | iOS, Android |
| `program-date-time` | `#EXT-X-PROGRAM-DATE-TIME` | iOS, Android | iOS, Android |
| `emsg` | DASH event message boxes | Android | Android |
| `external` | `externalMetadata` cue points from your config | iOS | iOS†, Android |
| `media` | Media / track information | – | iOS, Android |
| `access-log` | `AVPlayer` access-log samples | – | iOS |
| `unknown` | Forward-compatibility fallback for Android cue types this wrapper does not classify yet (none with the current SDK) | Android | Android |

The web player also emits `scte-35` (for `#EXT-X-CUE-OUT` / `#EXT-X-CUE-IN` tags) and `discontinuity`. Neither native SDK exposes those, so they are not part of this API. SCTE-35 markers carried in `#EXT-X-DATERANGE` attributes **are** delivered, inside `date-range` events.

† **Known iOS SDK limitation (JWPlayerKit 4.28.0):** the wrapper forwards the playback-time `external` event as soon as the SDK dispatches it, but in our device testing the SDK only dispatched the parse-time event (`onMetadataCueParsed`) and never the playback-time one, for MP4 and HLS content alike. Tracked as SDK-12315. Until the SDK fix lands, drive playback-time logic for external cues on iOS from `onMetadataCueParsed` plus `onTime`. Android fires `onMeta` for external cues as expected.

---

## Payloads by type

### `id3`

ID3 frames embedded in the stream, flattened into a `{ frameId: value }` map exactly like the web player:

```json
{
  "metadataType": "id3",
  "metadataTime": 12.5,
  "metadata": {
    "TIT2": "Song title",
    "title": "Song title",
    "TXXX": { "segment-id": "abc123" },
    "PRIV": { "com.example.owner": "AAECAw==" }
  }
}
```

- Frames that carry a description or owner (`TXXX`, `WXXX`, `PRIV`, `GEOB`, `APIC`, `COMM`) nest their value under it: `{ "TXXX": { "<description>": value } }`.
- Binary payloads (`PRIV`, `GEOB.data`, `APIC.pictureData`, unrecognised frames) are **base64** strings.
- The web player's friendly aliases are included when the matching frame is present: `title` (`TIT2`/`TT2`), `artist` (`TPE1`/`TP1`), `album` (`TALB`/`TAL`), `url` (`WXXX`). They are always strings.
- **iOS** reports `metadataTime` (the frame's start). The iOS SDK keys frames by id, so two frames with the same id in one group collapse to one entry.
- **Android** does not know the cue time for playback-time ID3 frames, so `metadataTime` is omitted on `onMeta`. On `onMetadataCueParsed` it is omitted as well.
- **Android** keeps every frame in a group: when the same id appears both with and without a description, the description-less value nests under the empty-string key (`{ "TXXX": { "": "a", "segment-id": "b" } }`). A text frame carrying several values (ID3v2.4) is reported as an array; its alias is the first value.

### `date-range`

`#EXT-X-DATERANGE` tags, including SCTE-35 markers carried as attributes:

```json
{
  "metadataType": "date-range",
  "metadataTime": 30,
  "metadata": {
    "tag": "EXT-X-DATERANGE",
    "id": "splice-6FFFFFF0",
    "start": 30,
    "end": 45,
    "duration": 15,
    "startDate": "2024-05-01T12:00:30.000Z",
    "endDate": "2024-05-01T12:00:45.000Z",
    "attributes": [
      { "name": "ID", "value": "splice-6FFFFFF0" },
      { "name": "START-DATE", "value": "2024-05-01T12:00:30.000Z" },
      { "name": "PLANNED-DURATION", "value": 15 },
      { "name": "SCTE35-OUT", "value": "0xFC302000000000000000FFF00F05000000007FFFFE0000000000000000000000" }
    ]
  }
}
```

- `attributes` is an array of `{ name, value }` (the web player's shape), so arbitrary `X-` attributes are preserved. iOS keeps manifest order and repeated names; Android receives the attributes from its SDK as a map, so they arrive sorted by name and a repeated name keeps only its last value.
- `startDate` and `endDate` are re-emitted as UTC ISO 8601 on both platforms, whatever offset the manifest used.
- `id`, `startDate`, `endDate` are lifted out of the attributes for convenience. On both platforms `duration` is `PLANNED-DURATION` when present, otherwise the `DURATION` attribute, otherwise `end - start`.
- **iOS** re-encodes binary attributes such as `SCTE35-OUT` / `SCTE35-IN` / `SCTE35-CMD` as `0x…` hex strings, matching how they appear in the manifest. Numeric attributes arrive as numbers. The iOS SDK formats the `START-DATE` / `END-DATE` *attribute strings* in the device's local time zone while still appending `Z`, so read dates from `metadata.startDate` / `metadata.endDate` (correct UTC) rather than from the attributes (SDK-12315).
- **iOS** computes `start` / `end` relative to the content start date of the variant playlist AVPlayer selected. If the variants of a stream disagree on their first `EXT-X-PROGRAM-DATE-TIME`, the reported `start` shifts accordingly and `onMeta` fires when that shifted position is reached.
- **Android** passes every attribute as the string from the manifest and adds `content`, the raw tag text.

### `program-date-time`

`#EXT-X-PROGRAM-DATE-TIME` tags. `programDateTime` is both inside `metadata` and hoisted to the top level, as on the web:

```json
{
  "metadataType": "program-date-time",
  "metadataTime": 0,
  "programDateTime": "2024-05-01T12:00:00.000Z",
  "metadata": {
    "programDateTime": "2024-05-01T12:00:00.000Z",
    "start": 0,
    "end": 10
  }
}
```

Android adds `content` (the raw tag text) inside `metadata`.

### `emsg` (Android only)

DASH event message boxes. One event is emitted per message, like the web player:

```json
{
  "metadataType": "emsg",
  "metadata": {
    "id": 1,
    "schemeIdUri": "urn:scte:scte35:2013:bin",
    "value": "",
    "duration": 4,
    "messageData": "/DAgAAAAAAAA..."
  }
}
```

- `duration` is in seconds, or `null` when the box has no duration.
- `messageData` is base64. Decode it with your preferred base64 library if you need the bytes.
- `metadataTime` is not available from the Android SDK for emsg boxes.
- The iOS SDK does not expose emsg boxes, so this type never fires on iOS.

### `external`

Cue points you supply through `externalMetadata` on a playlist item (or at the config level on iOS). Each cue point round-trips as an event when playback enters it (`onMeta`, both platforms) and, on iOS, when it is registered (`onMetadataCueParsed`).

```js
const config = {
  playlist: [{
    file: 'https://example.com/video.m3u8',
    externalMetadata: [
      // An integer-string identifier is all a cross-platform config needs.
      {identifier: '1', startTime: 5, endTime: 10},
      {identifier: '2', startTime: 30, endTime: 35},
    ],
  }],
};
```

The iOS SDK reads `identifier` and the Android SDK reads the integer `id`; the wrapper derives each from the other before the config reaches the SDK, so you can supply either. Items the current platform cannot represent (a non-integer identifier on Android, or a missing `startTime` / `endTime`) are dropped with a `console.warn` rather than being handed to the SDK, where they would otherwise invalidate the whole config (Android) or surface as an empty placeholder cue (iOS).

```json
{
  "metadataType": "external",
  "metadataTime": 5,
  "metadata": {
    "identifier": "1",
    "id": 1,
    "start": 5,
    "end": 10
  }
}
```

- `identifier` is always a string. `id` is present when the identifier is an integer (always on Android).
- Both SDKs keep only the first **5 items per playlist item**: Android logs a warning (`Only 5 External Metadata are allowed`), iOS drops the rest silently.

### `media`

Media information, flat like the web player's `media` event:

```json
{
  "metadataType": "media",
  "duration": 634.5,
  "height": 720,
  "width": 1280,
  "frameRate": 29.97,
  "seekRange": { "start": 0, "end": 634.5 },
  "drm": null
}
```

- **iOS** fires this once the asset's media metadata is known and reports `duration`, `height`, `width`, `frameRate`, `seekRange` and `drm` (`'fairplay'` or `null`). Live streams may report `null` for a non-finite duration.
- **Android** fires this when the selected video or audio track format is reported (first frame and quality / audio-track changes). It carries `height`, `width`, `frameRate` when known plus a `metadata` object with track details: `videoBitrate`, `videoId`, `videoMimeType`, `droppedFrames`, `audioBitrate`, `audioChannels`, `audioSamplingRate`, `audioId`, `audioMimeType`, `language`. Unknown fields are omitted.

### `access-log` (iOS only)

Periodic `AVPlayerItemAccessLog` samples. Unknown values are omitted.

```json
{
  "metadataType": "access-log",
  "metadata": {
    "observedBitrate": 4213000,
    "indicatedBitrate": 3500000,
    "droppedFrames": 0
  }
}
```

This event can fire frequently during playback. Filter on `metadataType` early in your `onMeta` handler if you do not need it.

### `unknown`

Reserved for Android. It is the fallback for in-playlist cue types a future Android SDK may report that this wrapper does not classify yet; with the currently pinned SDK every cue type maps to one of the types above, so it does not fire. `metadata.content` holds the raw tag text when available.

---

## Notes

- Values that cannot be represented in JSON are converted before crossing the bridge: dates become ISO 8601 strings, byte payloads become base64 (or `0x…` hex for HLS date-range attributes on iOS), and non-finite numbers become `null`.
- On Android every event also carries `message` (`"onMeta"` / `"onMetadataCueParsed"`), consistent with the other Android callbacks.
- Ordering between `onMetadataCueParsed` and `onMeta` is not guaranteed for cues near the current position. Treat `onMetadataCueParsed` as "known ahead of time" and `onMeta` as "happening now".
- These callbacks are independent of `onPlaylistItemMetadataChanged`, which reports changes to the playlist item's title / description / image (lock-screen metadata), not timed metadata.
