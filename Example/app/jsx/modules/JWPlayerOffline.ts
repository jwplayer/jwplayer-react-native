/**
 * JWPlayerOffline Module
 * 
 * Native module for downloading and playing offline DRM-protected videos
 */

import { NativeModules, NativeEventEmitter } from 'react-native';

const JWPlayerOfflineModule = NativeModules.RNJWPlayerOfflineModule;

export interface OfflineVideoConfig {
  mediaId: string;
  file: string;
  processSpcUrl?: string;
  certificateUrl?: string;
}

export interface OfflinePlaylistItem {
  mediaId: string;
  file: string;
  localURL: string;
}

export interface DownloadProgressEvent {
  mediaId: string;
  progress: number;
}

export interface DownloadCompleteEvent {
  mediaId: string;
  url: string;
}

export interface DownloadErrorEvent {
  mediaId: string;
  error: string;
}

class JWPlayerOffline {
  private eventEmitter: NativeEventEmitter;

  constructor() {
    this.eventEmitter = new NativeEventEmitter(JWPlayerOfflineModule);
  }

  /**
   * Start downloading a video for offline playback
   */
  downloadVideo(config: OfflineVideoConfig): Promise<boolean> {
    return JWPlayerOfflineModule.downloadVideo(config);
  }

  /**
   * Check if a video is already downloaded
   */
  isDownloaded(mediaId: string): Promise<boolean> {
    return JWPlayerOfflineModule.isDownloaded(mediaId);
  }

  /**
   * Get list of all downloaded videos
   */
  getDownloads(): Promise<Array<{ mediaId: string }>> {
    return JWPlayerOfflineModule.getDownloads();
  }

  /**
   * Delete a downloaded video
   */
  deleteDownload(mediaId: string): Promise<boolean> {
    return JWPlayerOfflineModule.deleteDownload(mediaId);
  }

  /**
   * Get the playlist item for offline playback
   */
  getOfflinePlaylistItem(mediaId: string): Promise<OfflinePlaylistItem> {
    return JWPlayerOfflineModule.getOfflinePlaylistItem(mediaId);
  }

  /**
   * Listen for download progress events
   */
  onDownloadProgress(callback: (event: DownloadProgressEvent) => void) {
    return this.eventEmitter.addListener('onDownloadProgress', callback);
  }

  /**
   * Listen for download complete events
   */
  onDownloadComplete(callback: (event: DownloadCompleteEvent) => void) {
    return this.eventEmitter.addListener('onDownloadComplete', callback);
  }

  /**
   * Listen for download error events
   */
  onDownloadError(callback: (event: DownloadErrorEvent) => void) {
    return this.eventEmitter.addListener('onDownloadError', callback);
  }
}

export default new JWPlayerOffline();
