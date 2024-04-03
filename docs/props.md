# Props

This wrapper implements the native methods exposed by the [Android](https://sdk.jwplayer.com/android/v4/reference/com/jwplayer/pub/api/JsonHelper.html) and [iOS](https://sdk.jwplayer.com/ios/v4/reference/Classes/JWJSONParser.html) SDK for parsing JSON objects into player configs. This allows for easy parsing of the [JW Delivery API](https://docs.jwplayer.com/platform/reference/embed-content-with-the-delivery-api#delivery-api-v2-endpoints) into easy to use configurations.

| Prop | Type | Description | Default |
| --- | --- | --- | --- |
| `config` | Object | **(REQUIRED)** `JWConfig` object, see: [Config](#config) | `undefined` |
| `controls` | Boolean | Determines if player controls are displayed | `true` |



## Config
With the exception of `license` and `playlist`, all other fields are optional.

| Field | Type | Platform |  Additional Notes |
| --- | --- | --- | --- |
| [`autostart`](https://docs.jwplayer.com/players/reference/setup-options#autostart)       | boolean | A, I |                                                             |
| `forceLegacyConfig`                                                                      | boolean | A, I | Determines whether to use the legacy configuration settings |
| [`mute`](https://docs.jwplayer.com/players/reference/setup-options#mute)</a>             | boolean | A, I |                                                             |
| [`nextupoffset`](https://docs.jwplayer.com/players/reference/setup-options#nextupoffset) | string &#124; number | A, I |
| `pid`                                                                                    | string  | A, I | Unique identifier of the player                             |
| `useTextureView`                                                                         | boolean | A    |                                                             |

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
