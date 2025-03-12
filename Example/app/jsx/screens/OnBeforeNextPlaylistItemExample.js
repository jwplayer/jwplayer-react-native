import React, { useRef } from 'react';
import { StatusBar } from 'react-native';
import Player from '../components/Player';
import PlayerContainer from '../components/PlayerContainer';

export default () => {
    const playerRef = useRef([]);

    const onBeforeNextPlaylistItem = e => {
        console.log('onBeforeNextPlaylistItem was called with: ', e.nativeEvent);
        var playlistItem = JSON.parse(e.nativeEvent.playlistItem);
        var index = e.nativeEvent.index

        // Replace the 2nd item in the playlist with something new
        // This could also be a modified version of the current `playlistItem` raised in the callback
        if (index === 1) {
            playerRef.current.resolveNextPlaylistItem(otherItem);
        } else {
            playerRef.current.resolveNextPlaylistItem(playlistItem);
        }
    }

    const onFullScreen = () => {
        StatusBar.setHidden(true);
    };

    const onFullScreenExit = () => {
        StatusBar.setHidden(false);
    };

    let jwConfig = {
        "title": "Sample Playlist of Blender Projects",
        "playlist": "https://cdn.jwplayer.com/v2/playlists/NbzQThwn?format=json"
    }

    let otherItem = {
        "title": "Injected playlist item",
        "file": "http://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4"
    }

    const renderPlayer = () => {
        return (
            <Player
                ref={playerRef}
                style={{ flex: 1 }}
                config={{
                    autostart: true,
                    playlistItemCallbackEnabled: true,
                    styling: {
                        colors: {},
                    },
                    ...jwConfig
                }}
                onBeforeNextPlaylistItem={onBeforeNextPlaylistItem}
                onFullScreen={onFullScreen}
                onFullScreenExit={onFullScreenExit}
            />
        );
    };

    return (
        <PlayerContainer
            children={renderPlayer()}
            text="onBeforeNextPlaylistItem Example"
        />
    );
};
