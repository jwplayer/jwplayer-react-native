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
        const isUpNextItem = index > 0;
        if (isUpNextItem) {
            const adSchedule = [
                {
                    offset: 'pre',
                    tag: 'https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=',
                },
            ];
            playerRef.current.resolveNextPlaylistItem({ ...otherItem, adSchedule});
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
        "file": "https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4"
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
                    "advertising": {
                        "client": "googima"
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
