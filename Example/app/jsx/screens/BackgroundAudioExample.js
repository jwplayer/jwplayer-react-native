import React, {useMemo, useRef, useState} from 'react';
import {StyleSheet, Text, View, Pressable, Platform} from 'react-native';
import Player from '../components/Player';
import {globalStyles} from '../../ui/styles/global.style';

// Android: custom background-audio notification buttons and SDK-managed audio focus.
// The icons are drawables in this app (android/app/src/main/res/drawable/ic_rn_*.xml).
export default () => {
  const playerRef = useRef(null);
  const [liked, setLiked] = useState(false);
  const [log, setLog] = useState([]);

  const add = line => setLog(prev => [line, ...prev].slice(0, 6));

  const buttons = isLiked => [
    {id: 'like', icon: 'ic_rn_like', title: isLiked ? 'Unlike' : 'Like', inCompactView: true},
    {id: 'bookmark', icon: 'ic_rn_bookmark', title: 'Bookmark'},
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
      const next = !liked;
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
      {Platform.OS !== 'android' ? (
        <Text style={styles.label}>These features are Android-only.</Text>
      ) : null}
      <Text style={styles.label}>Liked: {liked ? 'yes' : 'no'}</Text>
      <Pressable
        style={styles.button}
        onPress={() => {
          playerRef.current?.setNotificationButtons([]);
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
