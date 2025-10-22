import React, {useRef, useState} from 'react';
import {
  Alert,
  ScrollView,
  StatusBar,
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Platform,
} from 'react-native';
import JWPlayer, {JWPlayerConfig} from '@jwplayer/jwplayer-react-native';
import PlayerContainer from '../components/PlayerContainer';

/**
 * TypeScript Example Screen
 *
 * Demonstrates the unified configuration types with:
 * - Full TypeScript type safety
 * - Cross-platform IMA DAI configuration
 * - Platform-specific feature examples
 */

const licenseKey =
  Platform.OS === 'android'
    ? 'YOUR_ANDROID_LICENSE_KEY'
    : 'YOUR_IOS_LICENSE_KEY';
// Example 1: Basic video with IMA DAI (VOD)
const basicDAIConfig: JWPlayerConfig = {
  license: licenseKey,
  autostart: true,
  playlist: [
    {
      title: 'IMA DAI VOD Stream',
      file: 'http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8',
      image: 'https://cdn.jwplayer.com/thumbs/demo-live.jpg',
      description: 'VOD Stream with Dynamic Ad Insertion',
      ...(Platform.OS === 'ios'
        ? {
            daiSetting: {
              videoID: 'tears-of-steel',
              cmsID: '2528370',
            },
          }
        : {
            imaDaiSettings: {
              videoId: 'tears-of-steel',
              cmsId: '2528370',
              streamType: 'hls',
            },
          }),
    },
  ],
  advertising: {
    client: Platform.OS === 'ios' ? 'GoogleIMADAI' : 'IMA_DAI',
  },
};

// Example 2: IMA DAI Live Stream
const liveDAIConfig: JWPlayerConfig = {
  license: licenseKey,
  autostart: true,
  playlist: [
    {
      title: 'DAI - BB Bunny (Live) HLS - BipBop fallback',
      file: 'http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8',
      description: "This stream contains variables in it's Ad Tag.",
      ...(Platform.OS === 'ios'
        ? {
            daiSetting: {
              assetKey: 'c-rArva4ShKVIAkNfy6HUQ',
            },
          }
        : {
            imaDaiSettings: {
              assetKey: 'c-rArva4ShKVIAkNfy6HUQ',
            },
          }),
    },
  ],
};

// Example 3: VAST ads with schedule
const vastConfig: JWPlayerConfig = {
  license: licenseKey,
  autostart: true,
  playlist: [
    {
      title: 'Video with VAST Ads',
      file: 'https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4',
      image: 'https://cdn.jwplayer.com/thumbs/q1fx20VZ-480.jpg',
    },
  ],
  advertising: {
    client: 'vast',
    schedule: [
      {
        offset: 'pre',
        tag: 'https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=',
      },
    ],
    skipoffset: 5,
  },
};

// Example 4: Google IMA with inline ad
const imaConfig: JWPlayerConfig = {
  license: licenseKey,
  autostart: true,
  playlist: [
    {
      title: 'Single Inline Linear Preroll',
      file: 'https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4',
      adschedule: {
        adBreak1: {
          offset: 'pre',
          ad: {
            source: 'googima',
            tag: 'https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=',
          },
        },
      },
    },
  ],
  advertising: {
    client: 'googima',
  },
};

// Example 5: Platform-specific configuration
const platformSpecificConfig: JWPlayerConfig = {
  license: licenseKey,
  file: 'http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8',
  autostart: true,

  // iOS-specific features (with @platform ios annotation in types)
  ...(Platform.OS === 'ios' && {
    styling: {
      colors: {
        buttons: '#FF0000',
        backgroundColor: '#000000',
        fontColor: '#FFFFFF',
      },
      showTitle: true,
      showDesc: true,
    },
    backgroundAudioEnabled: true,
    category: 'Playback',
    categoryOptions: ['MixWithOthers', 'DuckOthers'],
  }),

  // Android-specific features (with @platform android annotation in types)
  ...(Platform.OS === 'android' && {
    uiConfig: {
      hasOverlay: true,
      hasControlbar: true,
      hasCenterControls: true,
      hasNextUp: true,
      hasQualitySubMenu: true,
      hasCaptionsSubMenu: true,
      hasPlaybackRatesSubMenu: true,
      hasMenu: true,
      hasAds: true,
    },
    useTextureView: true,
  }),
};

const TypeScriptExample: React.FC = () => {
  const playerRef = useRef<any>(null);
  const [selectedConfig, setSelectedConfig] = useState<JWPlayerConfig | null>(
    null,
  );
  const [configName, setConfigName] = useState<string>('');

  const onTime = (_e: any) => {
    // const { position, duration } = _e.nativeEvent;
    // console.log('onTime:', position, duration);
  };

  const onFullScreen = () => {
    StatusBar.setHidden(true);
  };

  const onFullScreenExit = () => {
    StatusBar.setHidden(false);
  };

  const onPlayerReady = () => {
    console.log('Player is ready!');
  };

  const onPlayerError = (error: any) => {
    console.error('Player error:', error);
    Alert.alert('Player Error', JSON.stringify(error.nativeEvent));
  };

  const selectConfig = (config: JWPlayerConfig, name: string) => {
    setSelectedConfig(config);
    setConfigName(name);
  };

  const resetSelection = () => {
    setSelectedConfig(null);
    setConfigName('');
  };

  // Show config selection screen if no config is selected
  if (!selectedConfig) {
    return (
      <ScrollView style={styles.container}>
        <View style={styles.selectionContainer}>
          <Text style={styles.title}>Select a Configuration to Test</Text>
          <Text style={styles.subtitle}>
            Choose one of the configurations below to load the player with full
            TypeScript type safety
          </Text>

          <View style={styles.configList}>
            <TouchableOpacity
              style={styles.configCard}
              onPress={() => selectConfig(basicDAIConfig, 'IMA DAI VOD')}>
              <Text style={styles.configTitle}>IMA DAI VOD</Text>
              <Text style={styles.configDescription}>
                Video on Demand with Google Dynamic Ad Insertion
              </Text>
              <Text style={styles.configDetails}>
                • Uses unified imaDaiSettings{'\n'}• Cross-platform compatible
                {'\n'}• VideoId + CmsId configuration
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.configCard}
              onPress={() => selectConfig(liveDAIConfig, 'IMA DAI Live')}>
              <Text style={styles.configTitle}>IMA DAI Live</Text>
              <Text style={styles.configDescription}>
                Live stream with Google Dynamic Ad Insertion
              </Text>
              <Text style={styles.configDetails}>
                • Uses assetKey for live streams{'\n'}• Real-time ad insertion
                {'\n'}• Cross-platform compatible
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.configCard}
              onPress={() => selectConfig(vastConfig, 'VAST Ads')}>
              <Text style={styles.configTitle}>VAST Ads</Text>
              <Text style={styles.configDescription}>
                VAST ads with preroll schedule
              </Text>
              <Text style={styles.configDetails}>
                • VAST ad schedule{'\n'}• Preroll configuration{'\n'}• Skip
                after 5 seconds
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.configCard}
              onPress={() => selectConfig(imaConfig, 'Google IMA')}>
              <Text style={styles.configTitle}>Google IMA</Text>
              <Text style={styles.configDescription}>
                Google IMA inline preroll ad
              </Text>
              <Text style={styles.configDetails}>
                • Google IMA SDK{'\n'}• Single preroll ad{'\n'}• Ad schedule
                configuration
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.configCard}
              onPress={() =>
                selectConfig(platformSpecificConfig, 'Platform Specific')
              }>
              <Text style={styles.configTitle}>
                Platform Specific ({Platform.OS})
              </Text>
              <Text style={styles.configDescription}>
                Demonstrates platform-specific features
              </Text>
              <Text style={styles.configDetails}>
                {Platform.OS === 'ios'
                  ? '• iOS styling configuration\n• Background audio support\n• Audio session management'
                  : '• Android UI configuration\n• TextureView support\n• Granular UI control'}
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    );
  }

  // Show player with selected config
  return (
    <View style={styles.container}>
      <PlayerContainer
        children={
          <JWPlayer
            ref={playerRef}
            style={styles.player}
            config={selectedConfig}
            onTime={onTime}
            onFullScreen={onFullScreen}
            onFullScreenExit={onFullScreenExit}
            onPlayerReady={onPlayerReady}
            onPlayerError={onPlayerError}
          />
        }
        text={`TypeScript Example - ${configName}`}
      />

      <View style={styles.infoBar}>
        <View style={styles.infoContent}>
          <Text style={styles.infoText}>Configuration: {configName}</Text>
          <Text style={styles.infoSubtext}>
            Using unified JWPlayerConfig types
          </Text>
        </View>
        <TouchableOpacity style={styles.resetButton} onPress={resetSelection}>
          <Text style={styles.resetButtonText}>Change Config</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
  player: {
    flex: 1,
  },
  selectionContainer: {
    flex: 1,
    padding: 20,
    backgroundColor: '#1a1a1a',
  },
  title: {
    color: '#fff',
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
    textAlign: 'center',
  },
  subtitle: {
    color: '#999',
    fontSize: 14,
    marginBottom: 25,
    textAlign: 'center',
    lineHeight: 20,
  },
  configList: {
    flex: 1,
  },
  configCard: {
    backgroundColor: '#2a2a2a',
    padding: 15,
    marginBottom: 15,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#444',
  },
  configTitle: {
    color: '#fff',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  configDescription: {
    color: '#ccc',
    fontSize: 14,
    marginBottom: 10,
    lineHeight: 18,
  },
  configDetails: {
    color: '#999',
    fontSize: 12,
    lineHeight: 18,
    marginTop: 5,
  },
  infoBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#1a1a1a',
    paddingVertical: 15,
    paddingHorizontal: 20,
    borderTopWidth: 1,
    borderTopColor: '#333',
  },
  infoContent: {
    flex: 1,
  },
  infoText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
    marginBottom: 3,
  },
  infoSubtext: {
    color: '#999',
    fontSize: 11,
    fontStyle: 'italic',
  },
  resetButton: {
    backgroundColor: '#007AFF',
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 8,
    marginLeft: 10,
  },
  resetButtonText: {
    color: '#fff',
    fontSize: 13,
    fontWeight: '600',
  },
});

export default TypeScriptExample;
