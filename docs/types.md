# Collection of types
A better definition of types for easy reading. All intended to be used with `JwConfig` and best implemented by parsing JWP Delivery API response.

## Types of types
There will be interfaces and types defined below. Types will be formated Values/Type, and should be treated as an enum. (see [JwStretching](#JwStretching))

### JwStretching
|Values                            |Type               |
|----------------------------------|-------------------|
|uniform                           |String             |
|fill                              |String             |
|exactfit                          |String             |
|none                              |String             |

### JwThumbnailPreview
|Values                            |Type               |
|----------------------------------|-------------------|
|101                               |number             |
|102                               |number             |
|103                               |number             |

### JwPlaylistItem
|Field                             |Description        |Type                    |Optional|Platform Specific|
|----------------------------------|-------------------|------------------------|--------|-----------------|
|title                             |                   |string                  |TRUE    |                 |
|description                       |                   |string                  |TRUE    |                 |
|file                              |                   |string                  |TRUE    |                 |
|image                             |                   |string                  |TRUE    |                 |
|mediaid                           |                   |string                  |TRUE    |                 |
|feedid                            |                   |string                  |TRUE    |                 |
|recommendations                   |                   |string                  |TRUE    |                 |
|starttime                         |                   |number                  |TRUE    |                 |
|duration                          |                   |number                  |TRUE    |                 |
|tracks                            |                   |JwTrack[]               |TRUE    |                 |
|sources                           |                   |JwSource[]              |TRUE    |                 |
|externalMetadata                  |                   |JwExternalMetadata[]    |TRUE    |                 |
|adschedule                        |                   |JwAdBreak[]             |TRUE    |                 |
|schedule                          |                   |[key: string]: JwAdBreak|TRUE    |                 |
|imaDaiSettings                    |                   |JwImaDaiSettings        |TRUE    |                 |
|httpheaders                       |                   |[key: string]: string   |TRUE    |                 |

### JwTrack
|Field                             |Description        |Type                    |Optional|Platform Specific|
|----------------------------------|-------------------|------------------------|--------|-----------------|
|id                                |                   |string                  |TRUE    |                 |
|file                              |                   |string                  |TRUE    |                 |
|kind                              |                   |string                  |TRUE    |                 |
|label                             |                   |string                  |TRUE    |                 |
|default                           |                   |boolean                 |TRUE    |                 |

### JwSource
|Field                             |Description        |Type                    |Optional|Platform Specific|
|----------------------------------|-------------------|------------------------|--------|-----------------|
|drm                               |                   |JwDrm                   |TRUE    |                 |
|file                              |                   |string                  |TRUE    |                 |
|label                             |                   |string                  |TRUE    |                 |
|default                           |                   |string                  |TRUE    |                 |
|type                              |                   |string                  |TRUE    |                 |
|httpheaders                       |                   |[key: string]: string   |TRUE    |                 |

### JwDrm
|Field                             |Description        |Type                    |Optional|Platform Specific|
|----------------------------------|-------------------|------------------------|--------|-----------------|
|widevine                          |                   |JwWidevine              |TRUE    |                 |
|fairplay                          |                   |JwFairplay              |TRUE    |                 |

### JwWidevine
|Field                             |Description        |Type                    |Optional|Platform Specific|
|----------------------------------|-------------------|------------------------|--------|-----------------|
|url                               |                   |string                  |TRUE    |Android          |
|keySetId                          |                   |string                  |TRUE    |Android          |

### JwFairplay
|Field                             |Description        |Type                    |Optional|Platform Specific|
|----------------------------------|-------------------|------------------------|--------|-----------------|
|processSpcUrl                     |                   |string                  |TRUE    |iOS              |
|certificateUrl                    |                   |string                  |TRUE    |iOS              |

### JwRelatedConfig
|Field                             |Description        |Type                    |Optional|Platform Specific|
|----------------------------------|-------------------|------------------------|--------|-----------------|
|file                              |                   |string                  |        |                 |
|oncomplete                        |                   |JwRelatedOnComplete     |        |                 |
|onclick                           |                   |JwOnRelatedClick        |        |                 |
|autoplaytimer                     |                   |number                  |        |                 |

### JwRelatedOnComplete
|Values                            |Type               |
|----------------------------------|-------------------|
|hide                              |string             |
|show                              |string             |
|none                              |string             |
|autoplay                          |string             |

### JwOnRelatedClick
|Values                            |Type               |
|----------------------------------|-------------------|
|play                              |string             |
|link                              |string             |

### JwExternalMetadata
|Field                             |Description        |Type  |Optional|Platform Specific|
|----------------------------------|-------------------|------|--------|-----------------|
|startTime                         |double             |number|TRUE    |                 |
|endTime                           |double             |number|TRUE    |                 |
|id                                |int                |number|FALSE   |                 |

### JwImaSdkSettings
|Field                             |Description        |Type  |Optional|Platform Specific|
|----------------------------------|-------------------|------|--------|-----------------|
|sessionId                         |                   |string|TRUE    |                 |
|ppid                              |                   |string|TRUE    |                 |
|autoPlayAdBreaks                  |                   |boolean|TRUE    |                 |
|language                          |                   |string|TRUE    |                 |
|maxRedirects                      |int                |number|TRUE    |                 |
|playerType                        |                   |string|TRUE    |                 |
|playerVersion                     |                   |string|TRUE    |                 |
|isDebugMode                       |                   |boolean|TRUE    |                 |
|doesRestrictToCustomPlayer        |                   |boolean|TRUE    |                 |


### JwImaDaiSettings
|Field                             |Description        |Type  |Optional|Platform Specific|
|----------------------------------|-------------------|------|--------|-----------------|
|videoID                           |                   |string|TRUE    |                 |
|cmsID                             |                   |string|TRUE    |                 |
|assetKey                          |                   |string|TRUE    |                 |
|apiKey                            |                   |string|TRUE    |                 |
|streamType                        |                   |string|TRUE    |                 |
|adTagParameters                   |                   |[key: string]: string|TRUE    |                 |

### JwLogoView
|Field                             |Description        |Type  |Optional|Platform Specific|
|----------------------------------|-------------------|------|--------|-----------------|
|imageFile                         |                   |string|FALSE   |                 |
|fades                             |                   |boolean|FALSE   |                 |
|margin                            |                   |number|TRUE    |                 |
|position                          |                   |JwLogoPosition|TRUE    |                 |
|webLink                           |                   |string|FALSE   |                 |

### VmapAdvertisingConfig
|Field                             |Description        |Type  |Optional|Platform Specific|
|----------------------------------|-------------------|------|--------|-----------------|
|cuetext                           |                   |string|TRUE    |                 |
|adpodmessage                      |                   |string|TRUE    |                 |
|vpaidcontrols                     |                   |boolean|TRUE    |                 |
|requestTimeout                    |                   |number|TRUE    |                 |
|creativeTimeout                   |                   |number|TRUE    |                 |
|conditionaladoptout               |                   |boolean|TRUE    |                 |
|schedule                          |Must bestring for VMAP|string|        |                 |
|rules                             |                   |JwAdRules|TRUE    |                 |
|allowedOmidVendors                |                   |string[]|TRUE    |                 |
|omidSupport                       |                   |string|TRUE    |                 |
|admessage                         |                   |string|TRUE    |                 |
|skipmessage                       |                   |string|TRUE    |                 |
|skiptext                          |                   |string|TRUE    |                 |
|skipoffset                        |                   |number|TRUE    |                 |

### JwAdRules
|Field                             |Description        |Type  |Optional|Platform Specific|
|----------------------------------|-------------------|------|--------|-----------------|
|startOn                           |                   |number|        |                 |
|frequency                         |                   |number|        |                 |
|timeBetweenAds                    |                   |number|        |                 |
|startOnSeek                       |                   |JwStartOnSeek|        |                 |

### JwStartOnSeek
|Values                            |Type               |
|----------------------------------|-------------------|
|pre                               |string             |
|none                              |string             |

### VastAdvertisingConfig
|Field                             |Description        |Type       |Optional|Platform Specific|
|----------------------------------|-------------------|-----------|--------|-----------------|
|cuetext                           |                   |string     |TRUE    |                 |
|adpodmessage                      |                   |string     |TRUE    |                 |
|vpaidcontrols                     |                   |boolean    |TRUE    |                 |
|requestTimeout                    |                   |number     |TRUE    |                 |
|creativeTimeout                   |                   |number     |TRUE    |                 |
|conditionaladoptout               |                   |boolean    |TRUE    |                 |
|schedule                          |                   |JwAdBreak[]|TRUE    |                 |
|rules                             |                   |JwAdRules  |TRUE    |                 |
|allowedOmidVendors                |                   |string[]   |TRUE    |                 |
|omidSupport                       |                   |string     |TRUE    |                 |
|admessage                         |                   |string     |TRUE    |                 |
|skipmessage                       |                   |string     |TRUE    |                 |
|skiptext                          |                   |string     |TRUE    |                 |
|skipoffset                        |                   |number     |TRUE    |                 |

### ImaVmapAdvertisingConfig
|Field                             |Description        |Type       |Optional|Platform Specific|
|----------------------------------|-------------------|-----------|--------|-----------------|
|imaSdkSettings                    |                   |JwImaSdkSettings|TRUE    |                 |
|tag                               |                   |string     |TRUE    |                 |

### ImaDaiAdvertisingConfig
|Field                             |Description        |Type       |Optional|Platform Specific|
|----------------------------------|-------------------|-----------|--------|-----------------|
|imaDaiSettings                    |                   |JwImaDaiSettings|TRUE    |                 |
|imaSdkSettings                    |                   |JwImaSdkSettings|TRUE    |                 |

### JwLogoPosition
|Values                            |Type               |
|----------------------------------|-------------------|
|topLeft                           |string             |
|topRight                          |string             |
|bottomLeft                        |string             |
|bottomRight                       |string             |

### JwAdvertisingConfig
|Values                            |Type               |
|----------------------------------|-------------------|
|VmapAdvertisingConfig             |VmapAdvertisingConfig|
|VastAdvertisingConfig             |VastAdvertisingConfig|
|ImaVmapAdvertisingConfig          |ImaVmapAdvertisingConfig|
|ImaAdvertisingConfig              |ImaAdvertisingConfig|
|ImaDaiAdvertisingConfig           |ImaDaiAdvertisingConfig|

### JwAdBreak
|Field                             |Description        |Type                 |Optional|Platform Specific|
|----------------------------------|-------------------|---------------------|--------|-----------------|
|ad                                |                   |string&#124;string[]      |TRUE    |                 |
|offset                            |                   |string               |TRUE    |                 |
|skipoffset                        |int                |number               |TRUE    |                 |
|type                              |                   |JwAdType             |TRUE    |                 |
|custParams                        |                   |[key: string]: string|TRUE    |                 |

### JwAdType
|Values                            |Type               |
|----------------------------------|-------------------|
|LINEAR                            |string             |
|NONLINEAR                         |string             |

### JwUiConfig
|Field                     |Description|Type   |Optional|Platform Specific|
|--------------------------|-----------|-------|--------|-----------------|
|hasOverlay                |           |boolean|TRUE    |Android          |
|hasControlbar             |           |boolean|TRUE    |Android          |
|hasCenterControls         |           |boolean|TRUE    |Android          |
|hasNextUp                 |           |boolean|TRUE    |Android          |
|hasSideSeek               |           |boolean|TRUE    |Android          |
|hasLogoView               |           |boolean|TRUE    |Android          |
|hasError                  |           |boolean|TRUE    |Android          |
|hasPlaylist               |           |boolean|TRUE    |Android          |
|hasQualitySubMenu         |           |boolean|TRUE    |Android          |
|hasCaptionsSubMenu        |           |boolean|TRUE    |Android          |
|hasPlaybackRatesSubMenu   |           |boolean|TRUE    |Android          |
|hasAudiotracksSubMenu     |           |boolean|TRUE    |Android          |
|hasMenu                   |           |boolean|TRUE    |Android          |
|hasPlayerControlsContainer|           |boolean|TRUE    |Android          |
|hasCastingMenu            |           |boolean|TRUE    |Android          |
|hasChapters               |           |boolean|TRUE    |Android          |
|hasAds                    |           |boolean|TRUE    |Android          |
