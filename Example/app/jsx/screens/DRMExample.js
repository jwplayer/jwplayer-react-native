import React, { useRef, useEffect, useState } from 'react';
import { StyleSheet, Text, Linking, Platform } from 'react-native';
import Player from '../components/Player';
import DeviceInfo from 'react-native-device-info';
import PlayerContainer from '../components/PlayerContainer';

export default () => {
  const playerRef = useRef([]);
  const [isEmulator, setIsEmulator] = useState(false);

  /**
   * `jwSignedConfigPlaylist` is an example of the return for a JW Platform signed URL as
   * described here https://docs.jwplayer.com/platform/docs/protection-studio-drm-generate-a-signed-content-url-for-drm-playback
   * 
   * For the legacy examples, see `renderIOSPlayer` and `renderAndroidPlayer`
   */
  const jwSignedConfigPlaylist = {
    "title": "Shaka Test",
    "image": "https://shaka-player-demo.appspot.com/assets/poster.jpg",
    "description": "test test",
    "playlist": [
      {
        "title": "Shaka Test",
        "mediaid": "testtest", // REQUIRED -- This style of config is intended to be used with JW Studio DRM
        "image": "https://shaka-player-demo.appspot.com/assets/poster.jpg",
        "description": "",
        "sources": [
          {
            "drm": {
              "widevine": {
                "url": "https://cwip-shaka-proxy.appspot.com/no_auth"
              }
            },
            "file": "https://storage.googleapis.com/shaka-demo-assets/sintel-widevine/dash.mpd",
            "type": "application/dash+xml"
          },
          {
            "drm": {
              "fairplay": {
                "processSpcUrl": "https://fps.ezdrm.com/api/licenses",
                "certificateUrl": "https://fps.ezdrm.com/demo/video/eleisure.cer"
              }
            },
            "file": "https://fps.ezdrm.com/demo/video/ezdrm.m3u8",
            "type": "application/vnd.apple.mpegurl"
          }
        ]
      }
    ]
  }

  // legacy
  const renderIOSPlayer = () => {
    const EZDRMLicenseAPIEndpoint = 'https://fps.ezdrm.com/api/licenses';
    const EZDRMCertificateEndpoint =
      'https://fps.ezdrm.com/demo/video/eleisure.cer';
    const EZDRMVideoEndpoint = 'https://fps.ezdrm.com/demo/video/ezdrm.m3u8';

    return (
      <Player
        ref={playerRef}
        style={{ flex: 1 }}
        config={{
          autostart: true,
          forceLegacyConfig: true,
          playlist: [
            {
              fairplayCertUrl: EZDRMCertificateEndpoint,
              processSpcUrl: EZDRMLicenseAPIEndpoint,
              file: EZDRMVideoEndpoint,
            },
          ],
          styling: {
            colors: {},
          },
        }}
      />
    );
  };

  const renderPlayer = () => {
    return (
      <Player
        ref={playerRef}
        style={{ flex: 1 }}
        config={{
          styling: {
            colors: {},
          },
          ...jwSignedConfigPlaylist
        }}
      />
    );
  }

  // legacy
  const renderAndroidPlayer = () => {
    const AuthUrl = 'https://cwip-shaka-proxy.appspot.com/no_auth';
    const StreamUrl =
      'https://storage.googleapis.com/shaka-demo-assets/sintel-widevine/dash.mpd';

    return (
      <Player
        ref={playerRef}
        style={{ flex: 1 }}
        config={{
          autostart: true,
          forceLegacyConfig: true,
          playlist: [
            {
              authUrl: AuthUrl,
              file: StreamUrl,
              image: 'https://shaka-player-demo.appspot.com/assets/poster.jpg',
            },
          ],
          styling: {
            colors: {},
          },
        }}
      />
    );
  };

  useEffect(() => {
    getIsEmulator();
  }, []);

  const getIsEmulator = async () => {
    const emulator = await DeviceInfo.isEmulator();
    setIsEmulator(emulator);
  };

  return (
    <PlayerContainer
      children={
        isEmulator && Platform.OS === 'ios' ? (
          <Text style={styles.errorText}>
            {"DRM Doesn't work in the simulator. Check out "}
            <Text
              style={{ textDecorationLine: 'underline', color: '#ff0000' }}
              onPress={() =>
                Linking.openURL(
                  'https://reactnative.dev/docs/running-on-device',
                )
              }>
              this
            </Text>
            {' link to run on a real device.'}
          </Text>
        ) : (
          renderPlayer()
        )
      }
      text="Welcome to jwplayer-react-native"
    />
  );
};

const styles = StyleSheet.create({
  text: {
    fontSize: 18,
    margin: 40,
  },
  errorText: {
    textAlign: 'center',
    color: 'white',
    padding: 20,
    fontSize: 17,
  },
});
