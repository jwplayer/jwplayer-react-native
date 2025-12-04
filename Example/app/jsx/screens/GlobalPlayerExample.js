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
      file: "https://content.jwplatform.com/videos/bkaovAYt-52qL9xLP.mp4" // Adding a default file
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
      url: 'https://content.bitsontherun.com/videos/bkaovAYt-52qL9xLP.mp4'
    },
    {
      id: '2',
      title: 'Elephant Dream',
      url: 'https://content.bitsontherun.com/videos/3XnJSIm4-52qL9xLP.mp4'
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
            file: 'https://content.jwplatform.com/videos/bkaovAYt-52qL9xLP.mp4',
            title: 'Episode 1: Big Buck Bunny',
          },
          {
            file: 'https://content.jwplatform.com/videos/3XnJSIm4-52qL9xLP.mp4',
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
        video: 'https://content.jwplatform.com/videos/bkaovAYt-52qL9xLP.mp4',
        autoplay: true,
      },
      isPaused: false,
      isOpen: true
    }, () => {
      // Wait a moment for player to be ready, then go fullscreen
      setTimeout(() => {
        this.playerRef.current?.setFullscreen(true);
        
        // After 5 seconds in fullscreen, switch to a new video
        setTimeout(() => {
          const newConfig = {
            license: Platform.select({
              ios: IOS_API_KEY,
              android: ANDROID_API_KEY,
            }),
            controls: true,
            pipEnabled: true,
            fullScreenOnLandscape: false,
            backgroundAudioEnabled: true,
            playlist: [{
              file: 'https://content.jwplatform.com/videos/3XnJSIm4-52qL9xLP.mp4',
              title: 'Fullscreen Test Video 2 - Elephant Dream',
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
            autostart: true
          };
          
          this.playerRef.current?.recreatePlayerWithConfig(newConfig);
          
          // Update state to reflect new video
          this.setState({
            video: {
              id: 'fullscreen-test-2',
              title: 'Fullscreen Test Video 2',
              video: 'https://content.jwplatform.com/videos/3XnJSIm4-52qL9xLP.mp4',
              autoplay: true,
            }
          });
        }, 5000);
      }, 1000);
    });
  };

  // DRM Configuration Example
  // NOTE: Replace these placeholder values with your own DRM-protected content
  // For JWPlayer DRM setup, visit: https://docs.jwplayer.com/platform/docs/protection-studio-drm-integrations
  drmConfig = {
    android: {
      // Android uses Widevine DRM with DASH manifest
      file: "https://your-content-url.com/media/MEDIA_ID/manifest.mpd",
      title: "Your DRM Protected Video",
      mediaid: "YOUR_MEDIA_ID",
      sources: [{
        file: "https://your-content-url.com/media/MEDIA_ID/manifest.mpd",
        type: "application/dash+xml",
        drm: {
          widevine: {
            url: "https://your-license-server.com/license?drm=widevine"
          }
        }
      }]
    },
    ios: {
      // iOS uses FairPlay DRM with HLS manifest
      // IMPORTANT: processSpcUrl and certificateUrl will be extracted to the TOP LEVEL
      // of the player config (see loadDRMContent method below)
      file: "https://your-content-url.com/media/MEDIA_ID/playlist.m3u8",
      drm: {
        fairplay: {
          processSpcUrl: "https://your-license-server.com/license?drm=fairplay",
          certificateUrl: "https://your-certificate-server.com/certificate"
        }
      },
      type: "application/vnd.apple.mpegurl",
      title: "Your DRM Protected Video",
      mediaId: "YOUR_MEDIA_ID"
    }
  };

  // Load DRM content example
  // NOTE: This is a stub - replace drmConfig values above with your own DRM credentials
  loadDRMContent = () => {
    const playlistItem = Platform.select({
      android: this.drmConfig.android,
      ios: this.drmConfig.ios
    });
    
    // Build DRM config
    // iOS IMPORTANT: processSpcUrl and certificateUrl must be at the TOP LEVEL of config
    // This is how the native iOS player expects FairPlay credentials
    const config = {
      license: Platform.select({
        ios: IOS_API_KEY,
        android: ANDROID_API_KEY,
      }),
      playlist: [playlistItem],
      controls: true,
      autostart: true,
      // iOS DRM: Extract FairPlay URLs to top level
      ...(Platform.OS === 'ios' && playlistItem.drm?.fairplay ? {
        processSpcUrl: playlistItem.drm.fairplay.processSpcUrl,
        certificateUrl: playlistItem.drm.fairplay.certificateUrl,
      } : {}),
    };
    
    // If player is already mounted, use it directly
    if (this.state.isOpen && this.playerRef.current) {
      this.playerRef.current.recreatePlayerWithConfig(config);
      return;
    }
    
    // Otherwise, mount the player first with a simple config
    this.setState({
      video: {
        id: 'drm-content',
        video: 'https://content.jwplatform.com/videos/bkaovAYt-52qL9xLP.mp4',
        title: 'Loading DRM Content...',
        autoplay: false
      },
      isPaused: false,
      isOpen: true
    }, () => {
      // Wait for player to fully mount
      setTimeout(() => {
        if (this.playerRef.current) {
          this.playerRef.current.recreatePlayerWithConfig(config);
        } else {
          console.error('ERROR: playerRef.current is still null after mounting!');
        }
      }, 500);
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
          
          <TouchableOpacity 
            style={[styles.button, styles.drmTestButton]}
            onPress={this.loadDRMContent}>
            <Text style={styles.buttonText}>🔐 Test DRM Content (Requires Setup)</Text>
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
  drmTestButton: {
    backgroundColor: '#16A085',
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
