# react-native-jw-media-player

<img src="https://img.shields.io/badge/Version-1.0.0-blue?style=flat&color=blue />

⚠️ This **README** is for `react-native-jw-media-player` version `0.2.0` and higher, for previous version check out the [Old README](./Pre.0.2.0_README.md). Beginning with version `0.2.0`, this library uses [JWP's `JWPlayerKit ` (iOS)]((https://developer.jwplayer.com/jwplayer/docs/ios-get-started)) and [SDK 4 (Android)]((https://developer.jwplayer.com/jwplayer/docs/android-get-started)).

[Prerequisites](#prerequisites) | [Installation](#installation) | [Usage](#usage) | [Advanced Topics](#advanced-topics)


The `react-native-jw-media-player` library is a bridge that enables using the native JWP Android and iOS SDKs in React Native applications.

<img width="200" alt="sample" src="./images/1.png"> <img width="200" alt="sample" src="./images/2.png"> <img width="200" alt="sample" src="./images/3.png">

<br /><br />

## Prerequisites

Before installing and using the library, you need the following items:
- JWP [Account](https://jwplayer.com/pricing/)
- JWP License Key ([Android](https://docs.jwplayer.com/players/docs/android-overview#requirements) | [iOS](https://docs.jwplayer.com/players/docs/ios-overview#requirements))
- [React Native App](https://reactnative.dev/docs/getting-started)
- Package Manager ([npm](https://nodejs.org/en/download) | [yarn](https://yarnpkg.com/getting-started/install) )


<br /><br />

## Installation
[Android](#android) | [iOS](#ios)

### Android
Follow these steps to add the library to your Android project:
1. From the project directory in terminal, add the **jwplayer-react-native** library. You can use npm or yarn.

   npm: `npm i jwplayer-react-native --save`

   yarn: `yarn add jwplayer-react-native`

2. In **android/build.gradle**, add the JWP Maven repository inside the `allprojects` block.

   ```groovy
   allprojects {
       repositories {
           ...
           maven {
               url 'https://mvn.jwplayer.com/content/repositories/releases/'
           }
   ```


<br /><br />

### iOS

Follow these steps to add the library to your iOS project:
1. From the project directory in terminal, add the **jwplayer-react-native** library. You can use npm or yarn.

    npm: `npm i jwplayer-react-native --save`

    yarn: `yarn add jwplayer-react-native`

2. Change the directory to the iOS folder of your React Native project.
   ```
   cd ios/
   ```

3. Install the iOS dependencies with CocoaPods.
     ```
   pod install
   ```

<br /><br />

## Usage

The following example shows how you can enhance your React Native application by seamlessly integrating the multimedia playback functionalities of the JWP mobile SDKs.

Follow these steps to configure the media playback experience in your app:

1. Use the following example as a guide to configure the media playback experience. Be sure to remove all instances of `...` from the code.


  ```javascript
  ...

  import JWPlayer, { JWPlayerState } from 'react-native-jw-media-player';

  ...

  const styles = StyleSheet.create({
    container: {
      flex: 1,
    },
    player: {
      flex: 1,
    },
  });

  ...

  const playlistItem = {
    title: 'Track',
    mediaId: -1,
    image: 'http://image.com/image.png',
    description: 'My beautiful track',
    startTime: 0,
    file: 'http://file.com/file.mp3',
    autostart: true,
    repeat: false,
    displayDescription: true,
    displayTitle: true,
    tracks: [
      {
        file: 'http://file.com/english.vtt',
        label: 'en'
      },
      {
        file: 'http://file.com/spanish.srt',
        label: 'es'
      }
    ],
    sources: [
      {
        file: 'http://file.com/file.mp3',
        label: 'audio'
      },
      {
        file: 'http://file.com/file.mp4',
        label: 'video',
        default: true
      }
    ]
  }

  const config = {
    license:
      Platform.OS === 'android'
        ? 'YOUR_ANDROID_SDK_KEY'
        : 'YOUR_IOS_SDK_KEY',
    backgroundAudioEnabled: true,
    autostart: true,
    styling: {
      colors: {
        timeslider: {
          rail: "0000FF",
        },
      },
    },
    playlist: [playlistItem],
  }

  ...

  async isPlaying() {
    const playerState = await this.JWPlayer.playerState();
    return playerState === JWPlayerState.JWPlayerStatePlaying;
  }

  ...

  render() {

  ...

  <View style={styles.container}>
    <JWPlayer
      ref={p => (this.JWPlayer = p)}
      style={styles.player}
      config={config}
      onBeforePlay={() => this.onBeforePlay()}
      onPlay={() => this.onPlay()}
      onPause={() => this.onPause()}
      onIdle={() => console.log("onIdle")}
      onPlaylistItem={event => this.onPlaylistItem(event)}
      onSetupPlayerError={event => this.onPlayerError(event)}
      onPlayerError={event => this.onPlayerError(event)}
      onBuffer={() => this.onBuffer()}
      onTime={event => this.onTime(event)}
      onFullScreen={() => this.onFullScreen()}
      onFullScreenExit={() => this.onFullScreenExit()}
    />
  </View>

  ...

  }
  ```


2. Define `config.license` with your Android or iOS JWP license key.
3. Define `config.playlist` with the media to play in the player.
4. (Optional) Define the other values of the `config` prop.

<br />

ℹ️ See [Props](/docs/props.md) for all the available `config` prop fields.

<br /><br />

### Example Project

This repository contains the `Example` project. This project showcases several basic implementations of the `<JWPlayer>` view and can be used as a resource while working with the `react-native-jw-media-player` library:

- Test pull requests (PRs) or modifications
- Experiment with the available media playback features
- Validate your configurations in a known, working application
- Demonstrate issues within a sanitary environment when submitting bugs

<br />

Follow these steps to run the example project:

1. Checkout this repository.
2. From the `Example` directory, run `yarn` or `npm i`.
3. From the `Example/ios` directory, install the iOS dependencies.
   ```
   pod install
   ```
4. In Xcode,open **RNJWPlayer.xcworkspace**.
5. In **App.js**, within the `config` prop, add your JWP SDK license key.
6. Build and run the app for your preferred platform.
   ```
   yarn android
   ```

   OR

   ```
   yarn ios
   ```

<br />

ℹ️ You can also build and run the app with specific `react-native` commands.

<br /><br />

## Advanced Topics

[Background Audio](#background-audio) | [Casting](#casting)

### Background Audio

Background Audio allows your viewers to play audio while they are no longer actively using your app or have locked their device.

Follow these steps to enable background audio sessions:

1. Set `backgroundAudioEnabled` to `true` in the `config`.
2. Ensure that background audio is set for [Android](https://docs.jwplayer.com/players/docs/android-enable-background-audio) or [iOS](https://docs.jwplayer.com/players/docs/ios-player-backgrounding-reference#configure-audio-playback).

<br /><br />

### Casting

[Android](#android-casting) | [iOS](#ios-casting)

JWP enables casting by default with a casting button.

#### Android Casting

Follow these steps to enable casting:

1. In **app/build.gradle** in `ext{}`, add `RNJWPlayerUseGoogleCast = true`.
2. In **app/build.gradle**, add `com.google.android.gms:play-services-cast-framework:21.3.0`.
3. Create a class that overrides `OptionsProvider` in your Android codebase:
   1. See the reference file **android/src/main/java/com/appgoalz/rnjwplayer/CastOptionsProvider.java**.
   2. Replace `.setTargetActivityClassName(RNJWPlayerView.class.getName())` with your player Activity.
   3. Modify the file with any options necessary for your use case.
4. Add the `meta-data` to your **AndroidManifest.xml**.

   ```xml
    <meta-data
        android:name="com.google.android.gms.cast.framework.OPTIONS_PROVIDER_CLASS_NAME"
        android:value="path.to.CastOptionsProvider" />
   ```

<br />

#### iOS Casting

Follow these steps to enable casting:

1. [Enable casting to Chromecast devices](https://docs.jwplayer.com/players/docs/ios-enable-casting-to-chromecast-devices).
2. Add `$RNJWPlayerUseGoogleCast = true` to your **Podfile**. This setting will install `google-cast-sdk` pod.
3. Edit **Info.plist** with the following values.

  ```text
  'NSBluetoothAlwaysUsageDescription' => 'We will use your Bluetooth for media casting.',
  'NSBluetoothPeripheralUsageDescription' => 'We will use your Bluetooth for media casting.',
  'NSLocalNetworkUsageDescription' => 'We will use the local network to discover Cast-enabled devices on your WiFi network.',
  'Privacy - Local Network Usage Description' => 'We will use the local network to discover Cast-enabled devices on your WiFi network.'
  'NSMicrophoneUsageDescription' => 'We will use your Microphone for media casting.'
  ```

<br />

| Available Method | Description |
| --- | --- |
| `castState(GCKCastState)` | Gets the cast state<br />See: [GCKCastState](#gckcaststate) |

##### GCKCastState

```text
typedef NS_ENUM(NSUInteger, GCKCastState) {
  /** No Cast session is established, and no Cast devices are available. */
  GCKCastStateNoDevicesAvailable = 0,
  /** No Cast session is establishd, and Cast devices are available. */
  GCKCastStateNotConnected = 1,
  /** A Cast session is being established. */
  GCKCastStateConnecting = 2,
  /** A Cast session is established. */
  GCKCastStateConnected = 3,
};
```


`autoSelect`: boolean

`defaultTrack`: boolean

`groupId`: string

`name`: string

`language`: string

A video file can include multiple audio tracks. The onAudioTracks event is fired when the list of available AudioTracks is updated (happens shortly after a playlist item starts playing).

Once the AudioTracks list is available, use getAudioTracks to return an array of available AudioTracks.

Then use getCurrentAudioTrack or setCurrentAudioTrack(index) to view or change the current AudioTrack.

This is all handled automatically if using the default player controls, but these functions are helpful if you're implementing custom controls.

### Stretching

`uniform`: (default) Fits JW Player dimensions while maintaining aspect ratio

`exactFit`: Will fit JW Player dimensions without maintaining aspect ratio

`fill`: Will zoom and crop video to fill dimensions, maintaining aspect ratio

`none`: Displays the actual size of the video file. (Black borders)

##### Stretching Examples:

![Stretching Example](https://files.readme.io/ce19994-stretch-options.png)

(image from JW Player docs - here use `exactFit` instead of `exactfit`)

##### DRM

Checkout the official DRM docs [iOS](https://developer.jwplayer.com/jwplayer/docs/ios-play-drm-protected-content) & [Android](https://developer.jwplayer.com/jwplayer/docs/android-play-drm-protected-content).

As of now June 7, 2022 there is only support for **Fairplay** on iOS and **Widevine** for Android.

In short when passing a protected file URL in the `file` prop you will also need to pass additional props for the player to be able to decode your encrypted content; per each respective platform.

- iOS - your `processSpcUrl?: string` (License) and `fairplayCertUrl?: string` (Certificate) on the `config` prop. We will try to parse the `contentUUID` from the FPS server
  playback context (SPC), but you can override it by passing it in `config` along the other two props;
- Android - your `authUrl?: string` on each **PlaylistItem** in the `playlist` prop;

Checkout the DRMExample in the Example app. (The DRMExample cannot be run in the Simulator).

##### Advertising

### Important
When using an **IMA** ad client you need to do some additional setup.

- **iOS**: Add `$RNJWPlayerUseGoogleIMA = true` to your Podfile, this will add `GoogleAds-IMA-iOS-SDK` pod.

- **Android**: Add `RNJWPlayerUseGoogleIMA = true` in your *app/build.gradle* `ext {}` this will add `'com.google.ads.interactivemedia.v3:interactivemedia:3.29.0'`
        and `'com.google.android.gms:play-services-ads-identifier:18.0.1'`.

| Prop                          | Description                                                                                              | Type                                          | Availability            |
|-------------------------------|----------------------------------------------------------------------------------------------------------|-----------------------------------------------|-------------------------|
| **`adVmap`**                  | The URL of the ads VMAP XML.                                                                             | `String`                                      | All Clients (iOS only)             |
| **`adSchedule`**              | Array of tags and offsets for ads.                                                                       | `{tag: String, offset: String}[]`             | All Clients             |
| **`openBrowserOnAdClick`**    | Should the player open the browser when clicking on an ad.                                               | `Boolean`                                     | All Clients             |
| **`adClient`**                | The ad client. One of `vast`, `ima`, or `ima_dai`, check out [JWPlayerAdClients](#JWPlayerAdClients), defaults to `vast`.                                  | `'vast'`, `'ima'`, `'ima_dai'`               | All Clients             |
| **`adRules`**                 | Ad rules for VAST client.                                                                                | `{startOn: Number, frequency: Number, timeBetweenAds: Number, startOnSeek: 'none' \| 'pre'}` | VAST only               |
| **`imaSettings`**             | Settings specific to Google IMA SDK.                                                                     | `{locale: String, ppid: String, maxRedirects: Number, sessionID: String, debugMode: Boolean}` | IMA and IMA DAI         |
| **`companionAdSlots`**        | Array of objects representing companion ad slots.                                                        | `{viewId: String, size?: {width: Number, height: Number}}[]` | IMA only                |
| **`friendlyObstructions`**    | Array of objects representing friendly obstructions for viewability measurement.                         | `{viewId: String, purpose: 'mediaControls' \| 'closeAd' \| 'notVisible' \| 'other', reason?: String}[]` | IMA and IMA DAI         |
| **`googleDAIStream`**         | Stream configuration for Google DAI (Dynamic Ad Insertion).                                              | `{videoID?: String, cmsID?: String, assetKey?: String, apiKey?: String, adTagParameters?: {[key: string]: string}}` | IMA DAI only            |
| **`tag`**         | Vast xml URL.                                              | `String` | Vast only (iOS only)            |

##### Related

| Prop                  | Description                                                                                | Type                         |
| --------------------- | ------------------------------------------------------------------------------------------ | ---------------------------- |
| **`onClick`**         | Sets the related content onClick action using a JWRelatedOnClick. Defaults to `play`       | `'play', 'link'`             |
| **`onComplete`**      | Sets the related content onComplete action using a JWRelatedOnComplete. Defaults to `show` | `'show', 'hide', 'autoplay'` |
| **`heading`**         | Sets the related content heading using a String. Defaults to “Next up”.                    | `String`                     |
| **`url`**             | Sets the related content url using a URL.                                                  | `String`                     |
| **`autoplayMessage`** | Sets the related content autoplayMessage using a String. Defaults to `title`               | `String`                     |
| **`autoplayTimer`**   | Sets the related content autoplayTimer using a Int. Defaults to 10 seconds.                | `Int`                        |

### AudioSessions iOS

Check out the official apple docs for more information on [AVAudioSessions](https://developer.apple.com/documentation/avfaudio/avaudiosession?language=objc). Below is a summarized description of each value.

##### AudioSessionCategory

**Available on iOS**

| Prop                | Description                                                                                          |
| ------------------- | ---------------------------------------------------------------------------------------------------- |
| **`Ambient`**       | Use this category for background sounds such as rain, car engine noise, etc. Mixes with other music. |
| **`SoloAmbient`**   | Use this category for background sounds. Other music will stop playing.                              |
| **`Playback`**      | Use this category for music tracks.                                                                  |
| **`Record`**        | Use this category when recording audio.                                                              |
| **`PlayAndRecord`** | Use this category when recording and playing back audio.                                             |
| **`MultiRoute`**    | Use this category when recording and playing back audio.                                             |

##### AudioSessionCategoryOptions

**Available on iOS**

| Prop                             | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`MixWithOthers`**              | Controls whether other active audio apps will be interrupted or mixed with when your app's audio session goes active. Details depend on the category.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **`DuckOthers`**                 | Controls whether or not other active audio apps will be ducked when when your app's audio session goes active. An example of this is a workout app, which provides periodic updates to the user. It reduces the volume of any music currently being played while it provides its status.                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| **`AllowBluetooth`**             | Allows an application to change the default behavior of some audio session categories with regard to whether Bluetooth Hands-Free Profile (HFP) devices are available for routing. The exact behavior depends on the category.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| **`DefaultToSpeaker`**           | Allows an application to change the default behavior of some audio session categories with regard to the audio route. The exact behavior depends on the category.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| **`InterruptSpokenAudioAndMix`** | When a session with InterruptSpokenAudioAndMixWithOthers set goes active, then if there is another playing app whose session mode is AVAudioSessionModeSpokenAudio (for podcast playback in the background, for example), then the spoken-audio session will be interrupted. A good use of this is for a navigation app that provides prompts to its user: it pauses any spoken audio currently being played while it plays the prompt.                                                                                                                                                                                                                                                                                                               |
| **`AllowBluetoothA2DP`**         | Allows an application to change the default behavior of some audio session categories with regard to whether Bluetooth Advanced Audio Distribution Profile (A2DP) devices are available for routing. The exact behavior depends on the category.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| **`AllowAirPlay`**               | Allows an application to change the default behavior of some audio session categories with regard to showing AirPlay devices as available routes. This option applies to various categories in the same way as AVAudioSessionCategoryOptionAllowBluetoothA2DP; see above for details.                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **`OverrideMutedMicrophone`**    | Some devices include a privacy feature that mutes the built-in microphone at a hardware level under certain conditions e.g. when the Smart Folio of an iPad is closed. The default behavior is to interrupt the session using the built-in microphone when that microphone is muted in hardware. This option allows an application to opt out of the default behavior if it is using a category that supports both input and output, such as AVAudioSessionCategoryPlayAndRecord, and wants to allow its session to stay activated even when the microphone has been muted. The result would be that playback continues as normal, and microphone sample buffers would continue to be produced but all microphone samples would have a value of zero. |

##### AudioSessionMode

**Available on iOS**

| Prop                 | Description                                                                                                                                                                                                                                                                                                                                                                                             |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`Default`**        | The default mode.                                                                                                                                                                                                                                                                                                                                                                                       |
| **`VoiceChat`**      | Only valid with AVAudioSessionCategoryPlayAndRecord. Appropriate for Voice over IP (VoIP) applications. Reduces the number of allowable audio routes to be only those that are appropriate for VoIP applications and may engage appropriate system-supplied signal processing. Has the side effect of setting AVAudioSessionCategoryOptionAllowBluetooth.                                               |
| **`VideoChat`**      | Only valid with kAudioSessionCategory_PlayAndRecord. Reduces the number of allowable audio routes to be only those that are appropriate for video chat applications. May engage appropriate system-supplied signal processing. Has the side effect of setting AVAudioSessionCategoryOptionAllowBluetooth and AVAudioSessionCategoryOptionDefaultToSpeaker.                                              |
| **`GameChat`**       | Set by Game Kit on behalf of an application that uses a GKVoiceChat object; valid only with the AVAudioSessionCategoryPlayAndRecord category. Do not set this mode directly. If you need similar behavior and are not using a GKVoiceChat object, use AVAudioSessionModeVoiceChat instead.                                                                                                              |
| **`VideoRecording`** | Only valid with AVAudioSessionCategoryPlayAndRecord or AVAudioSessionCategoryRecord Modifies the audio routing options and may engage appropriate system-supplied signal processing.                                                                                                                                                                                                                    |
| **`Measurement`**    | Appropriate for applications that wish to minimize the effect of system-supplied signal processing for input and/or output audio signals.                                                                                                                                                                                                                                                               |
| **`MoviePlayback`**  | Engages appropriate output signal processing for movie playback scenarios. Currently only applied during playback over built-in speaker.                                                                                                                                                                                                                                                                |
| **`SpokenAudio`**    | Appropriate for applications which play spoken audio and wish to be paused (via audio session interruption) rather than ducked if another app (such as a navigation app) plays a spoken audio prompt. Examples of apps that would use this are podcast players and audio books. For more information, see the related category option AVAudioSessionCategoryOptionInterruptSpokenAudioAndMixWithOthers. |
| **`VoicePrompt`**    | Appropriate for applications which play spoken audio and wish to be paused (via audio session interruption) rather than ducked if another app (such as a navigation app) plays a spoken audio prompt. Examples of apps that would use this are podcast players and audio books. For more information, see the related category option AVAudioSessionCategoryOptionInterruptSpokenAudioAndMixWithOthers. |

##### Picture-in-picture

Picture in picture mode is enabled by JW on iOS for the PlayerViewController, however when setting the `viewOnly` prop to true you will also need to set the `pipEnabled` prop to true, and call the `togglePIP` method to enable / disable PIP.
For Android you will have to add the following code in your `MainActivity.java`

```
@Override
public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
  super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

  Intent intent = new Intent("onPictureInPictureModeChanged");
  intent.putExtra("isInPictureInPictureMode", isInPictureInPictureMode);
  intent.putExtra("newConfig", newConfig);
  this.sendBroadcast(intent);
}
```

## Available methods

| Func                        | Description                                                                                                                                                                             | Argument                                         |
| --------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------ |
| **`seekTo`**                | Tells the player to seek to position, use in onPlaylistItem callback so player finishes buffering file.                                                                                 | `Int`                                            |
| **`togglePIP`**             | Enter or exist Picture in picture mode.                                                                                                                                                 | `none`                                           |
| **`play`**                  | Starts playing.                                                                                                                                                                         | `none`                                           |
| **`pause`**                 | Pauses playing.                                                                                                                                                                         | `none`                                           |
| **`stop`**                  | Stops the player completely.                                                                                                                                                            | `none`                                           |
| **`playerState`**           | Returns promise that then returns the current state of the player. Check out the [JWPlayerState](#JWPlayerState) Object.                                                                | `none`                                           |
| **`position`**              | Returns promise that then returns the current position of the player in seconds.                                                                                                        | `none`                                           |
| **`toggleSpeed`**           | Toggles the player speed one of `0.5`, `1.0`, `1.5`, `2.0`.                                                                                                                             | `none`                                           |
| **`setSpeed`**              | Sets the player speed.                                                                                                                                                                  | `Double`                                         |
| **`setVolume`**             | Sets the player volume.                                                                                                                                                                 | `Double`                                         |
| **`setPlaylistIndex`**      | Sets the current playing item in the loaded playlist.                                                                                                                                   | `Int`                                            |
| **`setControls`**           | Sets the display of all the control buttons on the player.                                                                                                                              | `Boolean`                                        |
| **`setVisibility`**         | _(iOS only)_ Sets the visibility of specific control buttons on the player. You pass `visibility: Boolean` && `controls` that is an array of [JWControlType](#JWControlType)            | `visibility: Boolean, controls: JWControlType[]` |
| **`setLockScreenControls`** | _(iOS only)_ Sets the locks screen controls for the currently playing media, can be used to control what player to show the controls for.                                               | `Boolean`                                        |
| **`setFullScreen`**         | Set full screen.                                                                                                                                                                        | `Boolean`                                        |
| **`loadPlaylist`**          | Loads a playlist. (Using this function before the player has finished initializing may result in assert crash or blank screen, put in a timeout to make sure JWPlayer is mounted).      | `[PlaylistItems]`                                |
| **`loadPlaylistItem`**      | Loads a playlist item. (Using this function before the player has finished initializing may result in assert crash or blank screen, put in a timeout to make sure JWPlayer is mounted). | [PlaylistItem](#PlaylistItem)                    |
| **`getAudioTracks`**        | Returns promise that returns an array of [AudioTracks](#AudioTrack)                                                                                                                     | `none`                                           |
| **`getCurrentAudioTrack`**  | Returns promise that returns the index of the current audio track in array returned by getAudioTracks                                                                                   | `none`                                           |
| **`setCurrentAudioTrack`**  | Sets the current audio track to the audio track at the specified index in the array returned by getAudioTracks                                                                          | `Int`                                            |
| **`setCurrentCaptions`**    | Turns off captions when argument is 0. Setting argument to another integer, sets captions to track at playlistItem.tracks[integer - 1]                                                  | `Int`                                            |

## Available callbacks

###### All Callbacks with data are wrapped in native events for instance this is how to get the data from `onTime` callback ->

```
  onTime(event) {
    const {position, duration} = event.nativeEvent;
  }
```

| Func                            | Description                                                                                                                                                                                                                | Argument                                                                                                                                                                                                                                                                                                                        |
| ------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`onPlaylist`**                | A new playlist is loaded.                                                                                                                                                                                                  | `[playlistItem]` see [PlaylistItem](#PlaylistItem)                                                                                                                                                                                                                                                                              |
| **`onPlayerReady`**             | The player has finished setting up and is ready to play.                                                                                                                                                                   | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onBeforePlay`**              | Right before playing.                                                                                                                                                                                                      | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onBeforeComplete`**          | Right before playing completed and is starting to play.                                                                                                                                                                    | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onComplete`**                | Right after media playing is completed.                                                                                                                                                                                    | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onPlay`**                    | Player started playing.                                                                                                                                                                                                    | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onPause`**                   | Player paused playing.                                                                                                                                                                                                     | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onSeek`**                    | Seek event requested from user.                                                                                                                                                                                            | `{position: Double, offset: Double}`                                                                                                                                                                                                                                                                                            |
| **`onSeeked`**                  | Player finished seeking to a new position.                                                                                                                                                                                 | On **iOS** `none`, On **Android** `{position: Double}`                                                                                                                                                                                                                                                                          |
| **`onRateChanged`**                  | Player speed was changed by the user from the settings menu.                                                                                                                                                                                 | On **iOS** `{rate: Double, at: Double}`, On **Android** `{rate: Double, at: Double}`                                                                                                                                                                                                                                                                          |
| **`onSetupPlayerError`**        | Player faced and error while setting up the player.                                                                                                                                                                        | `{error: String}`                                                                                                                                                                                                                                                                                                               |
| **`onPlayerError`**             | Player faced an error after setting up the player but when attempting to start playing.                                                                                                                                    | `{error: String}`                                                                                                                                                                                                                                                                                                               |
| **`onBuffer`**                  | The player is buffering.                                                                                                                                                                                                   | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onTime`**                    | Interval callback for every millisecond playing.                                                                                                                                                                           | `{position: Double, duration: Double}`                                                                                                                                                                                                                                                                                          |
| **`onFullScreenRequested`**     | User clicked on the fullscreen icon. Use this to resize the container view for the player, if your not using `nativeFullScreen` prop. (Make use of https://github.com/yamill/react-native-orientation for fullscreen mode) | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onFullScreen`**              | Player went into fullscreen. Use this to resize the container view for the player, if your not using `nativeFullScreen` prop. (Make use of https://github.com/yamill/react-native-orientation for fullscreen mode)         | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onFullScreenExitRequested`** | User clicked on the fullscreen icon to exit.                                                                                                                                                                               | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onFullScreenExit`**          | Player exited fullscreen.                                                                                                                                                                                                  | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onPlaylistComplete`**        | Player finished playing playlist items.                                                                                                                                                                                    | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onPlaylistItem`**            | When starting to play a playlist item.                                                                                                                                                                                     | JW type playlist item see docs [ios](https://developer.jwplayer.com/sdk/ios/reference/Protocols/JWPlaylistItemEvent.html), [android](https://developer.jwplayer.com/sdk/android/reference/com/longtailvideo/jwplayer/events/PlaylistItemEvent.html) contains additional index of current playing item in playlist 0 for default |
| **`onAudioTracks`**             | The list of available audio tracks is updated (happens shortly after a playlist item starts playing).                                                                                                                      | `none`                                                                                                                                                                                                                                                                                                                          |
| **`onControlBarVisible`**       | When the visibility of the player changes.                                                                                                                                                                                 | `{nativeEvent: {visible: Boolean}}`                                                                                                                                                                                                                                                                                             |

### Background Audio

This package supports Background audio sessions by setting the `backgroundAudioEnabled` prop on the [PlaylistItem](#PlaylistItem), just follow the JWPlayer docs for background session.

Here for Android https://developer.jwplayer.com/jwplayer/docs/android-audiotrack#background-audio although this package handles background audio playing in android as is and you shouldn't have to make any additional changes.

Here for iOS https://developer.jwplayer.com/jwplayer/docs/ios-behavior#background-audio under Background Audio section.

For iOS you will have to enable `audio` in **Signing & Capabilities** under `background modes`.

### Casting

JWPlayer enables casting by default with a casting button (if you pass the `viewOnly` prop in the player config on iOS then you will need to enable casting by yourself).

###### iOS

1: Follow the instruction [here](https://developer.jwplayer.com/jwplayer/docs/ios-enable-casting-to-chromecast-devices) on the official JWPlayer site.

2: Add `$RNJWPlayerUseGoogleCast = true` to your Podfile, this will install `google-cast-sdk` pod.

3: Edit your `Info.plist` with the following values:

```
'NSBluetoothAlwaysUsageDescription' => 'We will use your Bluetooth for media casting.',
'NSBluetoothPeripheralUsageDescription' => 'We will use your Bluetooth for media casting.',
'NSLocalNetworkUsageDescription' => 'We will use the local network to discover Cast-enabled devices on your WiFi network.',
'Privacy - Local Network Usage Description' => 'We will use the local network to discover Cast-enabled devices on your WiFi network.'
'NSMicrophoneUsageDescription' => 'We will use your Microphone for media casting.'
```

4: Enable _Access WiFi Information_ capability under `Signing & Capabilities`

###### Android
1: Add `RNJWPlayerUseGoogleCast = true` to your *app/build.gradle* in `ext {}`, this will add `com.google.android.gms:play-services-cast-framework:21.3.0`.

#### Available methods

##### For iOS

| Func            | Description          | Argument                                      |
| --------------- | -------------------- | --------------------------------------------- |
| **`castState`** | Gets the cast state. | `int` check out [GCKCastState](#GCKCastState) |

##### GCKCastState

```
typedef NS_ENUM(NSUInteger, GCKCastState) {
  /** No Cast session is established, and no Cast devices are available. */
  GCKCastStateNoDevicesAvailable = 0,
  /** No Cast session is establishd, and Cast devices are available. */
  GCKCastStateNotConnected = 1,
  /** A Cast session is being established. */
  GCKCastStateConnecting = 2,
  /** A Cast session is established. */
  GCKCastStateConnected = 3,
};
```

#### Available callbacks

##### For iOS

| Func                                   | Description                                           | Payload                                           |
| -------------------------------------- | ----------------------------------------------------- | ------------------------------------------------- |
| **`onCastingDevicesAvailable`**        | Casting were devices discovered and became available. | `{devices: [{name: string, identifier: string}}]` |
| **`onConnectedToCastingDevice`**       | Connected to cast device.                             | `{device: {name: string, identifier: string}}`    |
| **`onDisconnectedFromCastingDevice`**  | Disconnected from cast device.                        | `{error: Error}`                                  |
| **`onConnectionTemporarilySuspended`** | Disconnected temporarily from cast device.            | `none`                                            |
| **`onConnectionRecovered`**            | Connection to cast device recovered                   | `none`                                            |
| **`onCasting`**                        | Started casting                                       | `none`                                            |
| **`onConnectionFailed`**               | Connection to cast device failed.                     | `{error: Error}`                                  |
| **`onCastingEnded`**                   | Casting ended.                                        | `{error: Error}`                                  |
| **`onCastingFailed`**                  | Casting failed.                                       | `{error: Error}`                                  |

##### For Android

| Func            | Description            | Payload                                                 |
| --------------- | ---------------------- | ------------------------------------------------------- |
| **`onCasting`** | Casting event occurred | `{active: Boolean, available: Boolean, device: String}` |

#### [CHANGELOG](https://github.com/chaimPaneth/react-native-jw-media-player/releases)
