import React, { useRef, useEffect, useState } from 'react';
import { Text, StyleSheet, Platform } from 'react-native';
import Player from '../components/Player';
import PlayerContainer from '../components/PlayerContainer';

/* utils */
var RNFS = require('react-native-fs');

export default () => {
  const playerRef = useRef([]);
  const [localFile, setLocalFile] = useState(null);

  useEffect(() => {
    getLocalFile();
  }, []);

  const getLocalFile = async () => {
    if (Platform.OS === 'ios') {
      const data = await RNFS.readDir(RNFS.MainBundlePath)
      const local = data?.find(file => file.name === 'local_file.mp4')?.path;
      setLocalFile('file://' + local);
    } else {
      const data = await RNFS.readDirAssets('');
      const local = data?.find(file => file.name == 'local_file.mp4')?.path;
      setLocalFile('asset:///' + local);
    }
  };

  const renderPlayer = () => {
    return localFile ? (
      <Player
        ref={playerRef}
        style={{ flex: 1 }}
        config={{
          autostart: true,
          playlist: [
            {
              file: localFile,
            },
          ],
          styling: {
            colors: {},
          },
        }}
      />
    ) : (
      <Text style={styles.error}>Failed to load local file.</Text>
    );
  };

  return (
    <PlayerContainer
      children={renderPlayer()}
      text="Welcome to jwplayer-react-native"
    />
  );
};

const styles = StyleSheet.create({
  errorText: {
    textAlign: 'center',
    color: 'white',
    padding: 20,
    fontSize: 17,
  },
});
