import React, {useRef} from 'react';
import {StyleSheet, View, Text} from 'react-native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import Player from '../components/Player';

// Demonstrates the fix for GitHub issue #93.
//
// On iOS, when a JWPlayer is rendered under a @react-navigation/bottom-tabs
// navigator, JWPlayerKit's quality / audio / captions menu used to be hidden
// behind the native tab bar — the menu inflated within the player VC's
// presentation context, which is clipped by the tab bar chrome.
//
// The fix lives in the bridge (ios/RNJWPlayer/RNJWPlayerViewController.swift):
// `present(_:animated:completion:)` is overridden on RNJWPlayerViewController
// to forward presentations up to the top-most view controller in the key
// window, so menus draw over the tab bar.
//
// To verify: open this screen, tap the kebab / settings icon in the player
// controls, choose Quality or Audio & Subtitles. The Close button should be
// visible above the tab bar.

const Tab = createBottomTabNavigator();

const jwConfig = {
  title: 'Bottom Tab Overlap Repro',
  playlist: [
    {
      title: 'Sample',
      file: 'https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4',
      tracks: [
        {
          file: 'https://playertest.longtailvideo.com/caption-files/captions-en.vtt',
          label: 'English',
          kind: 'captions',
          default: true,
        },
      ],
    },
  ],
};

const PlayerScreen = () => {
  const playerRef = useRef(null);

  return (
    <View style={styles.tabContainer}>
      <Player
        ref={playerRef}
        style={StyleSheet.absoluteFill}
        config={{
          autostart: true,
          styling: {colors: {}},
          ...jwConfig,
        }}
      />
    </View>
  );
};

const InfoTab = () => (
  <View style={styles.placeholder}>
    <Text style={styles.placeholderTitle}>Issue #93</Text>
    <Text style={styles.placeholderText}>
      Open the Player tab, tap the kebab menu in the player controls, then
      choose Quality or Audio & Subtitles. The menu's Close button should
      render above the native tab bar.
    </Text>
  </View>
);

export default () => {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: '#EC0041',
      }}>
      <Tab.Screen
        name="Player"
        component={PlayerScreen}
        options={{tabBarLabel: 'Player'}}
      />
      <Tab.Screen
        name="Info"
        component={InfoTab}
        options={{tabBarLabel: 'Info'}}
      />
    </Tab.Navigator>
  );
};

const styles = StyleSheet.create({
  tabContainer: {
    flex: 1,
    backgroundColor: 'black',
  },
  placeholder: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
    padding: 24,
  },
  placeholderTitle: {
    fontSize: 22,
    fontWeight: '700',
    marginBottom: 12,
  },
  placeholderText: {
    fontSize: 15,
    textAlign: 'center',
    lineHeight: 22,
    color: '#333',
  },
});
