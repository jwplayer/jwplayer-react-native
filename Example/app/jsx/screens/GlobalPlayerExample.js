import React from 'react';
import { View, StyleSheet, TouchableOpacity, Text, Platform } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import JWPlayer from '@jwplayer/jwplayer-react-native';
import {IOS_API_KEY, ANDROID_API_KEY} from '@env';

// Video Component
class VideoPlayer extends React.Component {
  playerRef = this.props.playerRef ?? React.createRef();
  state = {
    config: null,
    isReady: false,
    currentPlaylistItem: undefined,
  };

    initialConfig = {
    license: Platform.select({
      ios: IOS_API_KEY,
      android: ANDROID_API_KEY,
    }),
    controls: true,
    pipEnabled: true,
    fullScreenOnLandscape: false,
    backgroundAudioEnabled: true,
    playlist: [{
      title: "placeholder",
      file: "http://content.bitsontherun.com/videos/bkaovAYt-52qL9xLP.mp4" // Adding a default file
    }],
    styling: {
      colors: {
        timeslider: {
          progress: '925EC4',
          thumb: '73499D',
          rail: '7E50AB',
        },
      },
    },
    autostart: this.props.autoplay,
  };

  componentDidMount() {
    this.setPlaylist();
  }

  componentDidUpdate(prevProps) {
    if (
      prevProps.id !== this.props.id ||
      prevProps.video !== this.props.video
    ) {
      this.setPlaylist();
    }
  }

  setPlaylist() {
    if (!this.props.video) return;

    const config = { ...this.initialConfig };
    
    // Check if video is an array (multi-episode playlist) or single video
    if (Array.isArray(this.props.video)) {
      // Multi-episode playlist
      config.playlist = this.props.video;
    } else {
      // Single video
      const playlistItem = {
        file: this.props.video,
        mediaId: this.props.id,
      };
      config.playlist = [playlistItem];
    }

    // Use recreatePlayerWithConfig on both platforms (API parity achieved!)
    // iOS: Always recreates the player
    // Android: Intelligently reconfigures (preserves player instance when possible)
    this.playerRef.current?.recreatePlayerWithConfig(config);
    
    // Update local state to track current config
    this.setState({ 
      currentPlaylistItem: config.playlist[0],
      isReady: false,
      config
    });

    // Handle autoplay
    if (this.props.autoplay) {
      this.playerRef.current?.play();
    }
  }


  render() {
    if (!this.state.config) return null;

    return (
      <View style={styles.container}>
        <JWPlayer
          ref={this.playerRef}
          config={this.state.config}
          onPause={this.props.onPause}
          onPlay={this.props.onPlay}
          onComplete={this.props.onComplete}
          onPlayerError={(e) => {
            console.log('Player error:', e.nativeEvent);
            this.props.onError?.(e.nativeEvent);
          }}
          onPlayerReady={() => this.setState({ isReady: true })}
          style={styles.player}
        />
      </View>
    );
  }
}

// Main Screen Component
class GlobalPlayerExample extends React.Component {
  playerRef = React.createRef();
  
  state = {
    video: null,
    isOpen: false,
    isPaused: false,
    position: 'full', // 'full' | 'minimized'
  };

  // Sample videos to demonstrate functionality
  sampleVideos = [
    {
      id: '1',
      title: 'Big Buck Bunny',
      url: 'http://content.bitsontherun.com/videos/bkaovAYt-52qL9xLP.mp4'
    },
    {
      id: '2',
      title: 'Elephant Dream',
      url: 'http://content.bitsontherun.com/videos/3XnJSIm4-52qL9xLP.mp4'
    }
  ];

  setVideo = (videoData) => {
    // Don't update if it's the same video
    if (videoData?.id === this.state.video?.id) return;
    
    this.setState({
      video: {
        id: videoData.id,
        title: videoData.title,
        video: videoData.url,
        autoplay: true,
      },
      isPaused: false,
      isOpen: true
    });
  };

  // Test multi-episode playlist (Issue #188 - auto-progression test)
  playMultiEpisodePlaylist = () => {
    this.setState({
      video: {
        id: 'playlist',
        title: 'Episode Series',
        video: [
          {
            file: 'http://content.bitsontherun.com/videos/bkaovAYt-52qL9xLP.mp4',
            title: 'Episode 1: Big Buck Bunny',
          },
          {
            file: 'http://content.bitsontherun.com/videos/3XnJSIm4-52qL9xLP.mp4',
            title: 'Episode 2: Elephant Dream',
          }
        ],
        autoplay: true,
      },
      isPaused: false,
      isOpen: true
    });
  };

  // Test fullscreen video switch (Issue #192 - simulates customer complaint)
  testFullscreenSwitch = () => {
    // First, load and play a video
    this.setState({
      video: {
        id: 'fullscreen-test-1',
        title: 'Fullscreen Test Video 1',
        video: 'http://content.bitsontherun.com/videos/bkaovAYt-52qL9xLP.mp4',
        autoplay: true,
      },
      isPaused: false,
      isOpen: true
    }, () => {
      // Wait a moment for player to be ready, then go fullscreen
      setTimeout(() => {
        console.log('Entering fullscreen...');
        this.playerRef.current?.setFullscreen(true);
        
        // After 5 seconds in fullscreen, switch to a new video
        setTimeout(() => {
          console.log('Switching video in fullscreen using recreatePlayerWithConfig...');
          const newConfig = {
            ...this.initialConfig,
            playlist: [{
              file: 'http://content.bitsontherun.com/videos/3XnJSIm4-52qL9xLP.mp4',
              title: 'Fullscreen Test Video 2 - Elephant Dream',
            }],
            autostart: true
          };
          
          this.playerRef.current?.recreatePlayerWithConfig(newConfig);
          
          // Update state to reflect new video
          this.setState({
            video: {
              id: 'fullscreen-test-2',
              title: 'Fullscreen Test Video 2',
              video: 'http://content.bitsontherun.com/videos/3XnJSIm4-52qL9xLP.mp4',
              autoplay: true,
            }
          });
        }, 5000);
      }, 1000);
    });
  };

  handlePause = () => {
    if (this.state.isPaused) {
      this.playerRef.current?.play();
    } else {
      this.playerRef.current?.pause();
    }
    this.setState(prevState => ({ isPaused: !prevState.isPaused }));
  };

  togglePosition = () => {
    this.setState(prevState => ({
      position: prevState.position === 'full' ? 'minimized' : 'full'
    }));
  };

  handleError = (error) => {
    console.log('Error playing video:', error);
  };

  render() {
    const { video, isOpen, isPaused, position } = this.state;
    const { insets } = this.props;

    return (
      <View style={[
        styles.mainContainer,
        Platform.OS === 'android' && {paddingBottom: insets.bottom},
      ]}>
        <Text style={styles.header}>Global Player Example</Text>
        <Text style={styles.description}>
          This example demonstrates a global video player that can be minimized and controlled from anywhere in the app.
        </Text>
        
        <View style={styles.buttonContainer}>
          {this.sampleVideos.map((videoData) => (
            <TouchableOpacity
              key={videoData.id}
              style={styles.button}
              onPress={() => this.setVideo(videoData)}>
              <Text style={styles.buttonText}>Play {videoData.title}</Text>
            </TouchableOpacity>
          ))}
          
          <TouchableOpacity
            style={[styles.button, styles.playlistButton]}
            onPress={this.playMultiEpisodePlaylist}>
            <Text style={styles.buttonText}>🎬 Play Episode Series (Auto-Progress Test)</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={[styles.button, styles.fullscreenTestButton]}
            onPress={this.testFullscreenSwitch}>
            <Text style={styles.buttonText}>⛶ Fullscreen Switch Test (Issue #192)</Text>
          </TouchableOpacity>
        </View>

        {video && isOpen && (
          <View style={[
            styles.globalContainer,
            position === 'minimized' && styles.minimized
          ]}>
            <View style={styles.controls}>
              <TouchableOpacity onPress={this.handlePause}>
                <Text>{isPaused ? 'Play' : 'Pause'}</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={this.togglePosition}>
                <Text>{position === 'full' ? 'Minimize' : 'Maximize'}</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => this.setState({ isOpen: false })}>
                <Text>Close</Text>
              </TouchableOpacity>
            </View>
            <VideoPlayer
              id={video.id}
              video={video.video}
              playerRef={this.playerRef}
              autoplay={video.autoplay}
              onPlay={() => this.setState({ isPaused: false })}
              onPause={() => this.setState({ isPaused: true })}
              onError={this.handleError}
            />
          </View>
        )}
      </View>
    );
  }
}

// Wrap with hook to use safe area insets
export default function GlobalPlayerExampleWrapper() {
  const insets = useSafeAreaInsets();
  return <GlobalPlayerExample insets={insets} />;
}

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
    backgroundColor: 'white',
    padding: 20,
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  description: {
    fontSize: 16,
    marginBottom: 20,
    color: '#666',
  },
  buttonContainer: {
    marginVertical: 20,
  },
  button: {
    backgroundColor: '#7E50AB',
    padding: 15,
    borderRadius: 8,
    marginVertical: 5,
  },
  playlistButton: {
    backgroundColor: '#E67E22',
    marginTop: 15,
  },
  fullscreenTestButton: {
    backgroundColor: '#E74C3C',
    marginTop: 10,
  },
  buttonText: {
    color: 'white',
    textAlign: 'center',
    fontSize: 16,
  },
  container: {
    aspectRatio: 16 / 9,
    backgroundColor: '#000',
    borderRadius: 3,
    overflow: 'hidden',
  },
  player: {
    flex: 1,
  },
  globalContainer: {
    position: 'absolute',
    width: '100%',
    left: 0,
    bottom: 0,
    zIndex: 1000,
    backgroundColor: '#f0f0f0',
    borderTopLeftRadius: 8,
    borderTopRightRadius: 8,
  },
  minimized: {
    width: '50%',
    right: 0,
    left: undefined,
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
});
