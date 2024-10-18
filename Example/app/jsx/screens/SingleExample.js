import React, {useRef} from 'react';
import {StatusBar, Button} from 'react-native';
import Player from '../components/Player';
import PlayerContainer from '../components/PlayerContainer';

export default () => {
  const playerRef = useRef([]);

  const onTime = e => {
    // var {position, duration} = e.nativeEvent;
    // eslint-disable-line
    // console.log('onTime was called with: ', position, duration);
  };

  const onCaptionsChanged = (e) => {
    console.log(e.nativeEvent)
  }

  const onFullScreen = () => {
    StatusBar.setHidden(true);
  };

  const onFullScreenExit = () => {
    StatusBar.setHidden(false);
  };

  let jwConfig = {
    "title": "Single Inline Linear Preroll",
    "file": "https://cdn.jwplayer.com/manifests/QpKIwnjT.m3u8"
    }

  const renderPlayer = () => {
    return (
      <Player
        ref={playerRef}
        style={{flex: 1}}
        config={{
          autostart: true,
          styling: {
            colors: {},
          },
          ...jwConfig
        }}
        onTime={onTime}
        onFullScreen={onFullScreen}
        onFullScreenExit={onFullScreenExit}
        onCaptionsChanged={onCaptionsChanged}
      />
    );
  };

  return (
    <>
    
    <PlayerContainer
      children={renderPlayer()}
      text="Welcome to jwplayer-react-native"
    />
    <Button
        title="getCurrentCaptions()"
        onPress={() => {
          // always returns a promise but result can be null
          var levels = playerRef.current.getCurrentCaptions();
          levels.then((res) => {
            console.log('The current caption index is: ', res);
          });
        }}>
    </Button>
    </>
  );
};
