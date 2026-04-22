import React, {useRef, useState} from 'react';
import {StyleSheet, Text, View, Pressable, Platform} from 'react-native';
import Player from '../components/Player';
import {globalStyles} from '../../ui/styles/global.style';

const SEGMENTS = [
  {
    title: 'Morning Show — Top of Hour',
    description: 'News roundup and weather.',
    image: 'https://picsum.photos/id/1015/640/360',
  },
  {
    title: 'Live Interview — Featured Guest',
    description: 'In-depth conversation about the day\'s headlines.',
    image: 'https://picsum.photos/id/1025/640/360',
  },
  {
    title: 'Music Hour — Segment 3',
    description: 'Back-to-back tracks with zero commercial interruption.',
    image: 'https://picsum.photos/id/1043/640/360',
  },
];

export default () => {
  const playerRef = useRef(null);
  const [segmentIndex, setSegmentIndex] = useState(0);
  const [lastEvent, setLastEvent] = useState(null);

  const jwConfig = {
    title: SEGMENTS[0].title,
    playlist: [
      {
        title: SEGMENTS[0].title,
        description: SEGMENTS[0].description,
        image: SEGMENTS[0].image,
        file: 'https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4',
      },
    ],
  };

  const applyNextSegment = () => {
    const next = (segmentIndex + 1) % SEGMENTS.length;
    setSegmentIndex(next);
    // refreshNotification: temporary workaround so the Android background-audio notification
    // rebuilds with the new metadata. Drop this flag once the JW Android SDK refreshes it natively.
    playerRef.current?.setPlaylistItemMetadata({
      ...SEGMENTS[next],
      refreshNotification: true,
    });
  };

  const onPlaylistItemMetadataChanged = e => {
    const {index, playlistItem} = e.nativeEvent;
    setLastEvent({index, item: playlistItem});
  };

  return (
    <View style={globalStyles.container}>
      <View style={globalStyles.subContainer}>
        <View style={globalStyles.playerContainer}>
          <Player
            ref={playerRef}
            style={{flex: 1}}
            config={{autostart: false, backgroundAudioEnabled: true, ...jwConfig}}
            onPlaylistItemMetadataChanged={onPlaylistItemMetadataChanged}
          />
        </View>
      </View>
      <Text style={styles.label}>
        Current segment: {SEGMENTS[segmentIndex].title}
      </Text>
      <Pressable style={styles.button} onPress={applyNextSegment}>
        <Text style={styles.buttonText}>Update metadata (cycle segments)</Text>
      </Pressable>
      {lastEvent ? (
        <Text style={styles.event}>
          onPlaylistItemMetadataChanged fired (index {lastEvent.index})
        </Text>
      ) : null}
    </View>
  );
};

const styles = StyleSheet.create({
  label: {
    textAlign: 'center',
    fontSize: 14,
    marginTop: 12,
    color: '#333',
    paddingHorizontal: 16,
  },
  button: {
    marginTop: 16,
    marginHorizontal: 24,
    backgroundColor: '#808080',
    borderRadius: 20,
  },
  buttonText: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 16,
    margin: 16,
    textAlign: 'center',
  },
  event: {
    marginTop: 12,
    textAlign: 'center',
    color: '#2a7',
    fontSize: 12,
    paddingHorizontal: 16,
    ...(Platform.OS === 'ios' && {fontFamily: 'Menlo'}),
  },
});
