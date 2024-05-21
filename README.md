# jwplayer-react-native

[![Version](/badges/version.svg)](https://github.com/jwplayer/jwplayer-react-native) [![NPM version](https://img.shields.io/npm/v/%40jwplayer%2Fjwplayer-react-native)](https://www.npmjs.com/package/@jwplayer/jwplayer-react-native) [![License](/badges/license.svg)](/LICENSE)

⚠️ This **README** is for `jwplayer-react-native` version `1.0.0` and higher, for previous versions from the original author, [Chaim Paneth](https://github.com/chaimPaneth) via [Orthodox Union](https://www.ou.org/), see [react-native-jw-media-player](https://github.com/chaimPaneth/react-native-jw-media-player). Beginning with version `0.2.0`, this library uses [JWP's `JWPlayerKit ` (iOS)]((https://developer.jwplayer.com/jwplayer/docs/ios-get-started)) and [SDK 4 (Android)]((https://developer.jwplayer.com/jwplayer/docs/android-get-started)).

<br />

[Prerequisites](#prerequisites) | [Installation](#installation) | [Usage](#usage) | [Advanced Topics](#advanced-topics) | [Contributing](#contributing) | [Issues](#issues)

<br />

The `jwplayer-react-native` library is a bridge that enables using the native JWP Android and iOS SDKs in React Native applications.

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

   npm: `npm i @jwplayer/jwplayer-react-native --save`

   yarn: `yarn add @jwplayer/jwplayer-react-native`

2. In **android/build.gradle**, add the JWP Maven repository inside the `allprojects` block.

   ```groovy
   allprojects {
       repositories {
           ...
           maven {
               url 'https://mvn.jwplayer.com/content/repositories/releases/'
           }
   ```

For more details and guidance regarding configuration and requirements, see the [JWP Android SDK documentation](https://docs.jwplayer.com/players/docs/android-overview#requirements).

<br />

### iOS

Follow these steps to add the library to your iOS project:
1. From the project directory in terminal, add the **jwplayer-react-native** library. You can use npm or yarn.

    npm: `npm i @jwplayer/jwplayer-react-native --save`

    yarn: `yarn add @jwplayer/jwplayer-react-native`

2. Change the directory to the iOS folder of your React Native project.
   ```
   cd ios/
   ```

3. Install the iOS dependencies with CocoaPods.
     ```
   pod install
   ```

For more details and guidance regarding configuration and requirements, see the [JWP iOS SDK documentation](https://docs.jwplayer.com/players/docs/ios-overview#requirements).

<br /><br />

## Usage

The following example shows how you can enhance your React Native application by seamlessly integrating the multimedia playback functionalities of the JWP mobile SDKs.

Follow these steps to configure the media playback experience in your app:

1. Use the following example as a guide to configure the media playback experience. Be sure to remove all instances of `...` from the code.


  ```javascript
  ...

  import JWPlayer, { JWPlayerState } from '@jwplayer/jwplayer-react-native';

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
    tracks: [
      {
        file: 'http://file.com/english.vtt',
        label: 'en',
        default: true
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
    styling: { // only (mostly) compatible with iOS
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
      onLoaded={event => this.onLoaded(event)}
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

ℹ️ See [Legacy Readme](/docs/legacy_readme.md) for all the available `config` prop fields when using a legacy configuration.

<br />

### Example Project

This repository contains the **Example** project. This project showcases several basic implementations of the `<JWPlayer>` view and can be used as a resource while working with the `jwplayer-react-native` library:

- Test pull requests (PRs) or modifications
- Experiment with the available media playback features
- Validate your configurations in a known, working application
- Demonstrate issues within a sanitary environment when submitting bugs

<br />

Follow these steps to run the example project:

1. Checkout this repository.
2. From the **Example** directory, run `yarn` or `npm i`.
3. From the **Example/ios** directory, install the iOS dependencies.
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

[Advertising](#advertising) | [Background Audio](#background-audio) | [Casting](#casting) | [DRM](#drm) | [Picture in Picture (PiP)](#picture-in-picture-pip) | [Styling](#styling)

<br />

### Advertising

[Android](#android-advertising) | [iOS](#ios-advertising)

#### Android Advertising

Follow this step to set up advertising with IMA or DAI:

1. In the **app/build.gradle** `ext{}`, add `RNJWPlayerUseGoogleIMA = true`. This setting will add the following dependencies: `com.google.ads.interactivemedia.v3:interactivemedia:3.31.0` and `com.google.android.gms:play-services-ads-identifier:18.0.1`.

<br />

#### iOS Advertising

Follow this step to set up advertising with IMA or DAI:

1. In the **Podfile** `ext{}`, add `$RNJWPlayerUseGoogleIMA = true`. This setting will add `GoogleAds-IMA-iOS-SDK` to the pod.

<br /><br />

### Background Audio

Background Audio allows your viewers to play audio while they are no longer actively using your app or have locked their device.

Follow these steps to enable background audio sessions:

1. Set `backgroundAudioEnabled` to `true` in the `config`.
2. Ensure that background audio is set for [Android](https://docs.jwplayer.com/players/docs/android-enable-background-audio) or [iOS](https://docs.jwplayer.com/players/docs/ios-player-backgrounding-reference#configure-audio-playback).

<br /><br />

### Casting

[Android](#android-casting) | [iOS](#ios-casting)

JWP enables casting by default with a casting button.

<br />

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

<br /><br />

### DRM

Enable digital rights management (DRM) protected playback.

💡 Check out the **DRMExample** in the **Example** app. The **DRMExample** cannot be run in the Simulator. Additionally, the window will not show on an Android Emulator.

<br />

#### Android DRM

Only Widevine is supported.

Follow these steps to enable DRM:
1. Set up your Android app for [DRM playback](https://developer.jwplayer.com/jwplayer/docs/android-play-drm-protected-content).
2. Define `config.playlist` with the [JWP signed URL](https://docs.jwplayer.com/platform/docs/protection-studio-drm-generate-a-signed-content-url-for-drm-playback) of the media to play in the player.

*❗️DO NOT sign and store your API secerets from your application.❗️*

If you use a different provider for DRM or this does not work for your use case, conforming to a similiar format as a JWP signed URL response is optimal, such as adding the `drm` field to the `sources` for a playlist item).

<br />

#### iOS DRM

Only Fairplay is supported.

The below is currently not fully supported by the iOS SDK, so please refer to `Example` app, or the [legacy DRM documentation](docs/legacy_readme.md#drm). For now, you must still provide the DRM asset in the `config.playlist` as a legacy object `[{fairplayCertUrl: string,processSpcUrl: string,file: string}]`

> Follow these steps to enable DRM:
> 1. Set up your iOS app for [DRM playback](https://developer.jwplayer.com/jwplayer/docs/ios-play-drm-protected-content).
> 2. Define `config.playlist` with the [JWP signed URL](https://docs.jwplayer.com/platform/docs/protection-studio-drm-generate-a-signed-content-url-for-drm-playback) of the media to play in the player.
>
> *❗️DO NOT sign and store your API secerets from your application.❗️*
> 
If you use a different provider for DRM or this does not work for your use case, conforming to a similiar format as a JWP signed URL response is optimal, such as adding the `drm` field to the `sources` for a playlist item).

<br /><br />

### Picture in Picture (PiP)

[Android](#android-pip) | [iOS](#ios-pip)

#### Android PiP

1. Read and understand the requirements for PiP in the [Android SDK](https://docs.jwplayer.com/players/docs/android-invoke-picture-in-picture-playback).
2. In the activity where the player is embedded, add the following code.
   ```java
   @Override
   public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
    super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

    Intent intent = new Intent("onPictureInPictureModeChanged");
    intent.putExtra("isInPictureInPictureMode", isInPictureInPictureMode);
    intent.putExtra("newConfig", newConfig);
    this.sendBroadcast(intent);
   }
   ```

<br />

#### iOS PiP

1. Read and understand the requirements for PiP in the [iOS SDK](https://docs.jwplayer.com/players/docs/ios-invoke-picture-in-picture-playback). PiP mode is enabled by JWP for the `PlayerViewController`.
2. (viewOnly:true only) Set the `pipEnabled` prop to `true`.
3. (viewOnly:true only) Call `togglePIP()` to enable or disable PiP.

<br /><br />

### Styling

[Android](#android-styling) | [iOS](#ios-styling)

#### Android Styling
The `styling` prop will not work when using the modern prop convention that matches the JWP Delivery API. Even when using the `forceLegacyConfig` prop, Android may not respect your choices.

Android styling is best handled through overring JWP IDs in your apps resources files. See the documentation [here](https://docs.jwplayer.com/players/docs/android-styling-guide). 

A sample of overring a color via XML can be seen in this [colors file](Example/android/app/src/main/res/values/colors.xml). The color specified here is the default, but if you wish to change it, the color will be updated on the player.

<br />

#### iOS Styling
You can use the styling elements as defined in the [Legacy Readme](/docs/legacy_readme.md#styling). 

<br /><br />

## Contributing

- All contributions should correlate to an open issue. We hope to avoid doubling the work between contributors. 
- Changes shouldn't be a one-off solution for your use case.
- Keep work small as required. Large PRs aren't fun for anyone and will take longer to review.

<br /><br />

## Issues

- Follow the format for Bugs/Features/Questions.
- If submitting a bug, always attempt to recreate the issue in our Example app
- Provide as much relevant information as possible
- For the quickest support, reach out to JW support via this [link](https://support.jwplayer.com/hc/en-us/requests/new) after creating an issue on this repo. Providing the `Issue` number and additional, potentially proprietary/sensitive information. 

