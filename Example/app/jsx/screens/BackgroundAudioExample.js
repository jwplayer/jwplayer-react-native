import React, {useMemo, useRef, useState} from 'react';
import {StyleSheet, Text, View, Pressable, Platform} from 'react-native';
import Player from '../components/Player';
import {globalStyles} from '../../ui/styles/global.style';

// Custom background-audio notification buttons and audio focus.
// Android: the icons are drawables in this app (android/app/src/main/res/drawable/ic_rn_*.xml),
// and audio focus is managed by the JW SDK (`audioFocusHandling: 'sdk'`).
// iOS: buttons with `iosCommand` map to the lock-screen / Control Center feedback commands. In
// testing on iOS 27 only `like` is displayed; `bookmark` is accepted but not shown. Audio
// session interruptions (a call, Siri) arrive on onAudioFocusChange.
export default () => {
  const playerRef = useRef(null);
  const [liked, setLiked] = useState(false);
  // The handler reads the latest value through a ref, so it never acts on a stale `liked`.
  const likedRef = useRef(false);
  const [log, setLog] = useState([]);

  const add = line => setLog(prev => [line, ...prev].slice(0, 6));

  const buttons = isLiked => [
    {
      id: 'like',
      icon: 'ic_rn_like',
      title: isLiked ? 'Unlike' : 'Like',
      inCompactView: true,
      iosCommand: 'like',
      iosActive: isLiked,
    },
    {id: 'bookmark', icon: 'ic_rn_bookmark', title: 'Bookmark', iosCommand: 'bookmark'},
  ];

  // Keep the config object stable: a new object on every render would reconfigure the player.
  const config = useMemo(
    () => ({
      autostart: false,
      backgroundAudioEnabled: true,
      audioFocusHandling: 'sdk',
      notificationButtons: buttons(false),
      playlist: [
        {
          title: 'Background audio example',
          description: 'Press Home while playing to see the notification buttons.',
          file: 'https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4',
        },
      ],
    }),
    [],
  );

  const onNotificationButtonClick = e => {
    const {id} = e.nativeEvent;
    add(`notification button: ${id}`);
    if (id === 'like') {
      const next = !likedRef.current;
      likedRef.current = next;
      setLiked(next);
      playerRef.current?.setNotificationButtons(buttons(next));
    }
  };

  return (
    <View style={globalStyles.container}>
      <View style={globalStyles.subContainer}>
        <View style={globalStyles.playerContainer}>
          <Player
            ref={playerRef}
            style={{flex: 1}}
            config={config}
            onNotificationButtonClick={onNotificationButtonClick}
            onAudioFocusChange={e => add(`audio focus: ${e.nativeEvent.change}`)}
            onPause={e => add(`pause: ${e?.nativeEvent?.reason ?? 'unknown'}`)}
          />
        </View>
      </View>
      {Platform.OS === 'ios' ? (
        <Text style={styles.label}>
          Play, then lock the device: Like appears in the lock-screen controls. Bookmark is
          registered too, but iOS 27 does not show it.
        </Text>
      ) : null}
      <Text style={styles.label}>Liked: {liked ? 'yes' : 'no'}</Text>
      <Pressable
        style={styles.button}
        onPress={() => {
          playerRef.current?.setNotificationButtons([]);
          likedRef.current = false;
          setLiked(false);
        }}>
        <Text style={styles.buttonText}>Remove notification buttons</Text>
      </Pressable>
      {log.map((line, i) => (
        <Text key={i} style={styles.event}>
          {line}
        </Text>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  label: {textAlign: 'center', fontSize: 14, marginTop: 12, color: '#333'},
  button: {
    marginTop: 16,
    marginHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
    backgroundColor: '#1f6feb',
    alignItems: 'center',
  },
  buttonText: {color: '#fff', fontWeight: '600'},
  event: {textAlign: 'center', fontSize: 12, marginTop: 6, color: '#555'},
});
