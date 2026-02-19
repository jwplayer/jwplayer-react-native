/**
 * Offline Download Example
 *
 * Test screen for downloading and playing DRM-protected videos offline.
 * Update the TEST_VIDEOS array below with your DRM content to test.
 */

import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  Dimensions,
} from 'react-native';
import Player from '../components/Player';
import JWPlayerOffline, { OfflineVideoConfig } from '../modules/JWPlayerOffline';

// ---------------------------------------------------------------------------
// Test Videos
//
// Offline DRM downloads are currently implemented for iOS only (FairPlay).
// Add your FairPlay DRM-protected videos here for testing.
// ---------------------------------------------------------------------------
const TEST_VIDEOS: OfflineVideoConfig[] = [
  // {
  //   mediaId: 'my-video',
  //   file: 'https://content.jwplatform.com/v2/media/MEDIA_ID/playlist.m3u8?policy_id=POLICY&version=v2&token=TOKEN',
  //   processSpcUrl: 'https://content.jwplatform.com/v2/media/MEDIA_ID/license?drm=fairplay&policy_id=POLICY&version=v2&token=TOKEN',
  //   certificateUrl: 'https://content.jwplatform.com/v2/fairplay-streaming/certificate?policy_id=POLICY&version=v2&token=TOKEN',
  // },
  //
  // Non-DRM example (omit processSpcUrl / certificateUrl):
  // {
  //   mediaId: 'non-drm-test',
  //   file: 'https://cdn.jwplayer.com/manifests/example.m3u8',
  // },
];

// ---------------------------------------------------------------------------

interface DownloadStatus {
  mediaId: string;
  isDownloaded: boolean;
  progress: number;
  isDownloading: boolean;
}

const OfflineDownloadExample: React.FC = () => {
  const [downloads, setDownloads] = useState<DownloadStatus[]>([]);
  const [selectedVideo, setSelectedVideo] = useState<string | null>(null);
  const [offlineFile, setOfflineFile] = useState<string | null>(null);
  const [selectedDrmConfig, setSelectedDrmConfig] = useState<OfflineVideoConfig | null>(null);

  useEffect(() => {
    loadDownloadStatus();

    const progressListener = JWPlayerOffline.onDownloadProgress((event) => {
      console.log('Download progress:', event);
      setDownloads(prev => prev.map(d =>
        d.mediaId === event.mediaId
          ? { ...d, progress: event.progress, isDownloading: true }
          : d
      ));
    });

    const completeListener = JWPlayerOffline.onDownloadComplete((event) => {
      console.log('Download complete:', event);
      setDownloads(prev => prev.map(d =>
        d.mediaId === event.mediaId
          ? { ...d, isDownloaded: true, isDownloading: false, progress: 100 }
          : d
      ));
      Alert.alert('Success', `Video ${event.mediaId} downloaded successfully!`);
    });

    const errorListener = JWPlayerOffline.onDownloadError((event) => {
      console.error('Download error:', event);
      setDownloads(prev => prev.map(d =>
        d.mediaId === event.mediaId
          ? { ...d, isDownloading: false }
          : d
      ));
      Alert.alert('Error', `Download failed: ${event.error}`);
    });

    return () => {
      progressListener.remove();
      completeListener.remove();
      errorListener.remove();
    };
  }, []);

  const loadDownloadStatus = async () => {
    const statuses = await Promise.all(
      TEST_VIDEOS.map(async (video) => {
        const isDownloaded = await JWPlayerOffline.isDownloaded(video.mediaId);
        return {
          mediaId: video.mediaId,
          isDownloaded,
          progress: isDownloaded ? 100 : 0,
          isDownloading: false,
        };
      })
    );
    setDownloads(statuses);
  };

  const startDownload = async (video: OfflineVideoConfig) => {
    try {
      setDownloads(prev => prev.map(d =>
        d.mediaId === video.mediaId
          ? { ...d, isDownloading: true, progress: 0 }
          : d
      ));
      await JWPlayerOffline.downloadVideo(video);
    } catch (error) {
      console.error('Failed to start download:', error);
      Alert.alert('Error', 'Failed to start download');
      setDownloads(prev => prev.map(d =>
        d.mediaId === video.mediaId
          ? { ...d, isDownloading: false }
          : d
      ));
    }
  };

  const deleteDownload = async (mediaId: string) => {
    try {
      await JWPlayerOffline.deleteDownload(mediaId);
      await loadDownloadStatus();
      if (selectedVideo === mediaId) {
        setSelectedVideo(null);
        setOfflineFile(null);
      }
      Alert.alert('Success', 'Download deleted');
    } catch (error) {
      console.error('Failed to delete download:', error);
      Alert.alert('Error', 'Failed to delete download');
    }
  };

  const playOfflineVideo = async (mediaId: string) => {
    try {
      const playlistItem = await JWPlayerOffline.getOfflinePlaylistItem(mediaId);
      console.log('Playing offline video:', playlistItem);

      if (!playlistItem.file) {
        throw new Error('No file URL returned');
      }

      const videoConfig = TEST_VIDEOS.find(v => v.mediaId === mediaId);

      setSelectedVideo(mediaId);
      setOfflineFile(playlistItem.file);
      setSelectedDrmConfig(videoConfig || null);

      console.log('DRM config for playback:', {
        processSpcUrl: videoConfig?.processSpcUrl ? 'SET' : 'NONE',
        certificateUrl: videoConfig?.certificateUrl ? 'SET' : 'NONE',
      });
    } catch (error) {
      console.error('Failed to get offline playlist:', error);
      Alert.alert('Error', `Failed to load offline video: ${error.message}`);
    }
  };

  const getDownloadStatus = (mediaId: string) => {
    return downloads.find(d => d.mediaId === mediaId);
  };

  const renderVideoItem = (video: OfflineVideoConfig, index: number) => {
    const status = getDownloadStatus(video.mediaId);

    return (
      <View key={video.mediaId} style={styles.videoItem}>
        <Text style={styles.videoTitle}>Test Video {index + 1}</Text>
        <Text style={styles.videoId}>{video.mediaId}</Text>

        {status?.isDownloading && (
          <View style={styles.progressContainer}>
            <Text style={styles.progressText}>
              Downloading: {status.progress.toFixed(1)}%
            </Text>
            <View style={styles.progressBar}>
              <View
                style={[styles.progressFill, { width: `${status.progress}%` }]}
              />
            </View>
          </View>
        )}

        <View style={styles.buttonRow}>
          {!status?.isDownloaded && !status?.isDownloading && (
            <TouchableOpacity
              style={[styles.button, styles.downloadButton]}
              onPress={() => startDownload(video)}
            >
              <Text style={styles.buttonText}>Download</Text>
            </TouchableOpacity>
          )}

          {status?.isDownloaded && (
            <>
              <TouchableOpacity
                style={[styles.button, styles.playButton]}
                onPress={() => playOfflineVideo(video.mediaId)}
              >
                <Text style={styles.buttonText}>Play Offline</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[styles.button, styles.deleteButton]}
                onPress={() => deleteDownload(video.mediaId)}
              >
                <Text style={styles.buttonText}>Delete</Text>
              </TouchableOpacity>
            </>
          )}
        </View>
      </View>
    );
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Offline Download Test</Text>
        <Text style={styles.subtitle}>
          Test downloading and playing DRM-protected videos offline
        </Text>
      </View>

      <View style={styles.instructions}>
        <Text style={styles.instructionsTitle}>Instructions:</Text>
        <Text style={styles.instructionsText}>
          1. Update TEST_VIDEOS array with your DRM content{'\n'}
          2. Tap "Download" to download video offline{'\n'}
          3. Once downloaded, tap "Play Offline" to test playback{'\n'}
          4. Use "Delete" to remove downloaded content
        </Text>
      </View>

      {offlineFile && (
        <View style={styles.playerWrapper}>
          <View style={styles.playerContainer}>
            <Player
              config={{
                playlist: [{
                  file: offlineFile,
                  mediaId: selectedVideo,
                }],
                autostart: true,
                ...(selectedDrmConfig?.processSpcUrl && {
                  processSpcUrl: selectedDrmConfig.processSpcUrl,
                }),
                ...(selectedDrmConfig?.certificateUrl && {
                  certificateUrl: selectedDrmConfig.certificateUrl,
                }),
              }}
              onPlayerError={(e) => console.log('Player error:', e)}
              onPlay={() => console.log('Playing offline video')}
            />
          </View>
        </View>
      )}

      <View style={styles.videoList}>
        <Text style={styles.sectionTitle}>Test Videos</Text>
        {TEST_VIDEOS.map((video, index) => renderVideoItem(video, index))}
      </View>

      {TEST_VIDEOS.length === 0 && (
        <View style={styles.emptyState}>
          <Text style={styles.emptyText}>
            No test videos configured.{'\n'}
            Update the TEST_VIDEOS array in OfflineDownloadExample.tsx
          </Text>
        </View>
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 14,
    color: '#666',
  },
  instructions: {
    margin: 16,
    padding: 16,
    backgroundColor: '#fff3cd',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ffc107',
  },
  instructionsTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#856404',
    marginBottom: 8,
  },
  instructionsText: {
    fontSize: 14,
    color: '#856404',
    lineHeight: 20,
  },
  playerContainer: {
    backgroundColor: '#000',
    alignItems: 'center',
    marginVertical: 16,
  },
  playerContainer: {
    height: 300,
    width: Dimensions.get('window').width - 40,
  },
  videoList: {
    padding: 16,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 12,
  },
  videoItem: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 8,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  videoTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  videoId: {
    fontSize: 12,
    color: '#666',
    marginBottom: 12,
  },
  progressContainer: {
    marginBottom: 12,
  },
  progressText: {
    fontSize: 14,
    color: '#007AFF',
    marginBottom: 4,
  },
  progressBar: {
    height: 4,
    backgroundColor: '#e0e0e0',
    borderRadius: 2,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#007AFF',
  },
  buttonRow: {
    flexDirection: 'row',
    gap: 8,
  },
  button: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 6,
    alignItems: 'center',
  },
  downloadButton: {
    backgroundColor: '#007AFF',
  },
  playButton: {
    backgroundColor: '#34C759',
  },
  deleteButton: {
    backgroundColor: '#FF3B30',
  },
  buttonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  emptyState: {
    padding: 40,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    lineHeight: 20,
  },
});

export default OfflineDownloadExample;
