# Props

This wrapper implements the native methods exposed by the [Android](https://sdk.jwplayer.com/android/v4/reference/com/jwplayer/pub/api/JsonHelper.html) and [iOS](https://sdk.jwplayer.com/ios/v4/reference/Classes/JWJSONParser.html) SDK for parsing JSON objects into player configs.

| Prop | Type | Platform | Description | Default |
| --- | --- | --- | --- | --- |
| `config` | object | A, I | **(REQUIRED)** `JWConfig` object<br />See: [Config](#config) | `undefined` |
| `controls` | boolean | A, I | Determines if player controls are displayed | `true` |

<br /><br />

## Config
With the exception of `license` and `playlist`, all other fields are optional.

See [`types`](./types.md) for a definition of config types defined below

| Field | Type | Platform |  Additional Notes |
| --- | --- | --- | --- |
| `license`                                                                                | string |A, I | **(REQUIRED)** Platform-specific license key<br />([Android](https://docs.jwplayer.com/players/docs/android-overview#requirements) \| [iOS](https://docs.jwplayer.com/players/docs/ios-overview#requirements))|
| [`playlist`](https://docs.jwplayer.com/players/reference/setup-options#playlist)         | JwPlaylistItem[ ] &#124; string | A, I | **(REQUIRED)** |
| [`advertising`](https://docs.jwplayer.com/players/reference/advertising-config-ref)                                                                            |JwAdvertisingConfig | A, I | |
| `allowCrossProtocolRedirectsSupport`                                                     | boolean | A    | |
| [`autostart`](https://docs.jwplayer.com/players/reference/setup-options#autostart)       | boolean | A, I | |
| [`displaydescription`](https://docs.jwplayer.com/players/reference/setup-options#displaydescription) | boolean| A, I | |
| [`displaytitle`](https://docs.jwplayer.com/players/reference/setup-options#displaytitle) | boolean | A    | |
| [`file`](https://docs.jwplayer.com/players/reference/playlists#file)                     | string  | A, I | |
| `forceLegacyConfig`                                                                      | boolean | A, I | Determines whether to use the legacy configuration settings |
| `logoView`                                                                               | JwLogoView | A, I | |
| [`mute`](https://docs.jwplayer.com/players/reference/setup-options#mute)                 | boolean | A, I | |
| [`nextupoffset`](https://docs.jwplayer.com/players/reference/setup-options#nextupoffset) | string &#124; number | A, I | |
| `pid`                                                                                    | string  | A, I | Unique identifier of the player |
| [`playbackRateControls`](https://docs.jwplayer.com/players/reference/setup-options#playbackratecontrols) | boolean | A, I | |
| [`playbackRates`](https://docs.jwplayer.com/players/reference/setup-options#playbackrates)|number[ ] | A, I | |
| [`playlistIndex`](https://docs.jwplayer.com/players/reference/setup-options#playlistIndex) | number | A, I | |
| [`preload`](https://docs.jwplayer.com/players/reference/setup-options#preload)           | boolean | A, I | |
| [`related`](https://docs.jwplayer.com/players/reference/related-config-ref)              | JwRelatedConfig | A, I | |
| [`repeat`](https://docs.jwplayer.com/players/reference/setup-options#repeat) | boolean | A, I | |
| [`sources`](https://docs.jwplayer.com/players/reference/playlists#playlistsources)       | JwSource[ ] | A, I | |
| [`stretching`](https://docs.jwplayer.com/players/reference/setup-options#stretching)     | JwStretching | A, I | |
| `thumbnailPreview`                                                                       | JwThumbnailPreview| A, I | |
| `uiConfig`                                                                               | JwUiConfig | A, I | |
| `useTextureView`                                                                         | boolean | A    | |

