# Props

This wrapper implements the native methods exposed by the [Android](https://sdk.jwplayer.com/android/v4/reference/com/jwplayer/pub/api/JsonHelper.html) and [iOS](https://sdk.jwplayer.com/ios/v4/reference/Classes/JWJSONParser.html) SDK for parsing JSON objects into player configs. This allows for easy parsing of the [JW Delivery API](https://docs.jwplayer.com/platform/reference/embed-content-with-the-delivery-api#delivery-api-v2-endpoints) into easy to use configurations.


| Field | Platform |  Additional Notes |
| --- | --- | --- |
| `pid` string | A, I | Unique identifier of the player |
| <a href="https://docs.jwplayer.com/players/reference/setup-options#mute" target="_blank">`mute`</a> boolean | A, I | |
| `forceLegacyConfig` boolean | A, I | Determines whether to use the legacy configuration settings |
| `useTextureView` boolean | A | |
| <a href="https://docs.jwplayer.com/players/reference/setup-options#autostart" target="_blank">`autostart`</a> boolean | A, I | |


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
