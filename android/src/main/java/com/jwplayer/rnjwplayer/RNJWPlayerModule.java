package com.jwplayer.rnjwplayer;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;
import com.jwplayer.pub.api.JWPlayer;
import com.jwplayer.pub.api.PlayerState;
import com.jwplayer.pub.api.configuration.PlayerConfig;
import com.jwplayer.pub.api.media.adaptive.QualityLevel;
import com.jwplayer.pub.api.media.audio.AudioTrack;

import java.util.List;

public class RNJWPlayerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;

    private static final String TAG = "RNJWPlayerModule";

    public RNJWPlayerModule(ReactApplicationContext reactContext) {
        super(reactContext);

        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @ReactMethod
    public void loadPlaylist(final int reactTag, final ReadableArray playlistItems) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            JWPlayer player = playerView.mPlayerView.getPlayer();
            PlayerConfig oldConfig = player.getConfig();
            PlayerConfig config = new PlayerConfig.Builder()
                    .autostart(oldConfig.getAutostart())
                    .nextUpOffset(oldConfig.getNextUpOffset())
                    .repeat(oldConfig.getRepeat())
                    .relatedConfig(oldConfig.getRelatedConfig())
                    .displayDescription(oldConfig.getDisplayDescription())
                    .displayTitle(oldConfig.getDisplayTitle())
                    .advertisingConfig(oldConfig.getAdvertisingConfig())
                    .stretching(oldConfig.getStretching())
                    .uiConfig(oldConfig.getUiConfig())
                    .playlist(Util.createPlaylist(playlistItems))
                    .allowCrossProtocolRedirects(oldConfig.getAllowCrossProtocolRedirects())
                    .preload(oldConfig.getPreload())
                    .useTextureView(oldConfig.useTextureView())
                    .thumbnailPreview(oldConfig.getThumbnailPreview())
                    .mute(oldConfig.getMute())
                    .build();
            player.setup(config);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void loadPlaylistWithUrl(final int reactTag, final String playlistUrl) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            JWPlayer player = playerView.mPlayerView.getPlayer();
            PlayerConfig oldConfig = player.getConfig();
            PlayerConfig config = new PlayerConfig.Builder()
                    .autostart(oldConfig.getAutostart())
                    .nextUpOffset(oldConfig.getNextUpOffset())
                    .repeat(oldConfig.getRepeat())
                    .relatedConfig(oldConfig.getRelatedConfig())
                    .displayDescription(oldConfig.getDisplayDescription())
                    .displayTitle(oldConfig.getDisplayTitle())
                    .advertisingConfig(oldConfig.getAdvertisingConfig())
                    .stretching(oldConfig.getStretching())
                    .uiConfig(oldConfig.getUiConfig())
                    .playlistUrl(playlistUrl)
                    .allowCrossProtocolRedirects(oldConfig.getAllowCrossProtocolRedirects())
                    .preload(oldConfig.getPreload())
                    .useTextureView(oldConfig.useTextureView())
                    .thumbnailPreview(oldConfig.getThumbnailPreview())
                    .mute(oldConfig.getMute())
                    .build();
            player.setup(config);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void play(final int reactTag) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().play();
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void toggleSpeed(final int reactTag) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            double rate = playerView.mPlayerView.getPlayer().getPlaybackRate();
            if (rate < 2) {
                playerView.mPlayerView.getPlayer().setPlaybackRate(rate += 0.5);
            } else {
                playerView.mPlayerView.getPlayer().setPlaybackRate((float) 0.5);
            }
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void togglePIP(final int reactTag) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            if (playerView.mPlayerView.getPlayer().isInPictureInPictureMode()) {
                playerView.mPlayerView.getPlayer().exitPictureInPictureMode();
            } else {
                playerView.mPlayerView.getPlayer().enterPictureInPictureMode();
            }
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setSpeed(final int reactTag, final float speed) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().setPlaybackRate(speed);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void getCurrentQuality(final int reactTag, final Promise promise) {
        new JWPlayerUIBlock(reactTag, promise, mReactContext, playerView -> {
            int quality = playerView.mPlayerView.getPlayer().getCurrentQuality();
            promise.resolve(quality);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setCurrentQuality(final int reactTag, final int index) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().setCurrentQuality(index);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void getQualityLevels(final int reactTag, final Promise promise) {
        new JWPlayerUIBlock(reactTag, promise, mReactContext, playerView -> {
            List<QualityLevel> qualityLevelsList = playerView.mPlayerView.getPlayer().getQualityLevels();
            if (qualityLevelsList == null) {
                promise.resolve(null);
                return null;
            }
            WritableArray qualityLevels = Arguments.createArray();
            for (int i = 0; i < qualityLevelsList.size(); i++) {
                WritableMap qualityLevel = Arguments.createMap();
                QualityLevel level = qualityLevelsList.get(i);
                qualityLevel.putInt("playListPosition", level.getPlaylistPosition());
                qualityLevel.putInt("bitRate", level.getBitrate());
                qualityLevel.putString("label", level.getLabel());
                qualityLevel.putInt("height", level.getHeight());
                qualityLevel.putInt("width", level.getWidth());
                qualityLevel.putInt("index", level.getTrackIndex());
                qualityLevels.pushMap(qualityLevel);
            }
            promise.resolve(qualityLevels);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void pause(final int reactTag) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            if (!playerView.getIsCastActive()) {
                playerView.mPlayerView.getPlayer().pause();
                playerView.userPaused = true;
            }
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void stop(final int reactTag) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            if (!playerView.getIsCastActive()) {
                playerView.mPlayerView.getPlayer().stop();
                playerView.userPaused = true;
            }
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void seekTo(final int reactTag, final double time) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().seek(time);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setPlaylistIndex(final int reactTag, final int index) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().playlistItem(index);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setControls(final int reactTag, final boolean show) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().setControls(show);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void position(final int reactTag, final Promise promise) {
        new JWPlayerUIBlock(reactTag, promise, mReactContext, playerView -> {
            promise.resolve((Double.valueOf(playerView.mPlayerView.getPlayer().getPosition()).intValue()));
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void state(final int reactTag, final Promise promise) {
        new JWPlayerUIBlock(reactTag, promise, mReactContext, playerView -> {
            PlayerState playerState = playerView.mPlayerView.getPlayer().getState();
            promise.resolve(stateToInt(playerState));
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setFullscreen(final int reactTag, final boolean fullscreen) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().setFullscreen(fullscreen, fullscreen);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setVolume(final int reactTag, final int volume) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayerView.getPlayer().setVolume(volume);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void getAudioTracks(final int reactTag, final Promise promise) {
        new JWPlayerUIBlock(reactTag, promise, mReactContext, playerView -> {
            List<AudioTrack> audioTrackList = playerView.mPlayer.getAudioTracks();
            WritableArray audioTracks = Arguments.createArray();
            if (audioTrackList != null) {
                for (int i = 0; i < audioTrackList.size(); i++) {
                    WritableMap audioTrack = Arguments.createMap();
                    AudioTrack track = audioTrackList.get(i);
                    audioTrack.putString("name", track.getName());
                    audioTrack.putString("language", track.getLanguage());
                    audioTrack.putString("groupId", track.getGroupId());
                    audioTrack.putBoolean("defaultTrack", track.isDefaultTrack());
                    audioTrack.putBoolean("autoSelect", track.isAutoSelect());
                    audioTracks.pushMap(audioTrack);
                }
            }
            promise.resolve(audioTracks);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void getCurrentAudioTrack(final int reactTag, final Promise promise) {
        new JWPlayerUIBlock(reactTag, promise, mReactContext, playerView -> {
            promise.resolve(playerView.mPlayer.getCurrentAudioTrack());
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setCurrentAudioTrack(final int reactTag, final int index) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayer.setCurrentAudioTrack(index);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void setCurrentCaptions(final int reactTag, final int index) {
        new JWPlayerUIBlock(reactTag, null, mReactContext, playerView -> {
            playerView.mPlayer.setCurrentCaptions(index);
            return null;
        }).addToUIManager();
    }

    @ReactMethod
    public void getCurrentCaptions(final int reactTag, final Promise promise) {
        new JWPlayerUIBlock(reactTag, promise, mReactContext, playerView -> {
            promise.resolve(playerView.mPlayer.getCurrentCaptions());
            return null;
        }).addToUIManager();
    }

    private int stateToInt(PlayerState playerState) {
      switch (playerState) {
        case IDLE:
          return 0;
        case BUFFERING:
          return 1;
        case PLAYING:
          return 2;
        case PAUSED:
          return 3;
        case COMPLETE:
          return 4;
        case ERROR:
          return 5;
        default:
          return -1;
       }
    }
}