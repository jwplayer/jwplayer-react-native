# Props

This wrapper implements the native methods exposed by the [Android](https://sdk.jwplayer.com/android/v4/reference/com/jwplayer/pub/api/JsonHelper.html) and [iOS](https://sdk.jwplayer.com/ios/v4/reference/Classes/JWJSONParser.html) SDK for parsing JSON objects into player configs. This allows for easy parsing of the [JW Delivery API](https://docs.jwplayer.com/platform/reference/embed-content-with-the-delivery-api#delivery-api-v2-endpoints) into easy to use configurations.

| Prop | Type | Description | Default |
| --- | --- | --- | --- |
| `config` | Object | **(REQUIRED)** `JWConfig` object<br /><br />See: [Config](#config) | `undefined` |
| `controls` | Boolean | Determines if player controls are displayed | `true` |

<br /><br />

## Config
With the exception of `license` and `playlist`, all other fields are optional.

| Field | Type | Platform |  Additional Notes |
| --- | --- | --- | --- |
| `license`                                                                                | string |A, I | **REQUIRED** Platform-specific license key ([Android](https://docs.jwplayer.com/players/docs/android-overview#requirements) \| [iOS](https://docs.jwplayer.com/players/docs/ios-overview#requirements))|
| [`playlist`](https://docs.jwplayer.com/players/reference/setup-options#playlist)         | JwPlaylistItem[ ] &#124; string | A, I | **REQUIRED** |
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



|Field                             |Description        |Type|Optional|Platform Specific|
|----------------------------------|-------------------|----|--------|-----------------|
|pid                               |player ID          |string|TRUE    |                 |
|mute                              |                   |boolean|TRUE    |                 |
|forceLegacyConfig                 |non-jw json        |boolean|TRUE    |                 |
|useTextureView                    |                   |boolean|TRUE    |A                |
|autostart                         |                   |boolean|TRUE    |                 |
|nextupoffset                      |                   |string &#124; number|TRUE    |                 |
|repeat                            |                   |boolean|TRUE    |                 |
|allowCrossProtocolRedirectsSupport|                   |boolean|TRUE    |A                |
|displaytitle                      |                   |boolean|TRUE    |                 |
|displaydescription                |                   |boolean|TRUE    |                 |
|stretching                        |                   |JwStretching|TRUE    |                 |
|thumbnailPreview                  |                   |JwThumbnailPreview|TRUE    |                 |
|preload                           |                   |boolean|TRUE    |                 |
|playlist                          |                   |JwPlaylistItem[ ] &#124; string|TRUE    |                 |
|sources                           |                   |JwSource[ ]|TRUE    |                 |
|file                              |                   |string|TRUE    |                 |
|playlistIndex                     |                   |number|TRUE    |                 |
|related                           |                   |JwRelatedConfig|TRUE    |                 |
|uiConfig                          |                   |JwUiConfig|TRUE    |                 |
|logoView                          |                   |JwLogoView|TRUE    |                 |
|advertising                       |                   |JwAdvertisingConfig|TRUE    |                 |
|playbackRates                     |                   |number[ ]|TRUE    |                 |
|playbackRateControls              |                   |boolean|TRUE    |                 |
|license                           |non-jw json        |string|FALSE   |                 |
