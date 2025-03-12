package com.jwplayer.rnjwplayer;

import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.common.UIManagerType;
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

    private RNJWPlayerView getPlayerView(int reactTag) {
        int uiManagerType;
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            uiManagerType = UIManagerType.FABRIC;
        } else {
            uiManagerType = UIManagerType.DEFAULT;
        }
        UIManager uiManager = UIManagerHelper.getUIManager(mReactContext, uiManagerType);
        if (uiManager != null) {
            return (RNJWPlayerView) uiManager.resolveView(reactTag);
        } else {
            return null;
        }
    }

    @ReactMethod
    public void loadPlaylist(final int reactTag, final ReadableArray playlistItems) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
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
            }
        });
    }

    @ReactMethod
    public void loadPlaylistWithUrl(final int reactTag, final String playlistUrl) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
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
            }
        });
    }

    @ReactMethod
    public void play(final int reactTag) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().play();
            }
        });
    }

    @ReactMethod
    public void toggleSpeed(final int reactTag) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                double rate = playerView.mPlayerView.getPlayer().getPlaybackRate();
                if (rate < 2) {
                    playerView.mPlayerView.getPlayer().setPlaybackRate(rate += 0.5);
                } else {
                    playerView.mPlayerView.getPlayer().setPlaybackRate((float) 0.5);
                }
            }
        });
    }

    @ReactMethod
    public void togglePIP(final int reactTag) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                if (playerView.mPlayerView.getPlayer().isInPictureInPictureMode()) {
                    playerView.mPlayerView.getPlayer().exitPictureInPictureMode();
                } else {
                    playerView.mPlayerView.getPlayer().enterPictureInPictureMode();
                }
            }
        });
    }

    @ReactMethod
    public void setSpeed(final int reactTag, final float speed) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().setPlaybackRate(speed);
            }
        });
    }

    @ReactMethod
    public void getCurrentQuality(final int reactTag, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                int quality = playerView.mPlayerView.getPlayer().getCurrentQuality();
                promise.resolve(quality);
            } else {
                promise.reject("RNJW Error", "getCurrentQuality() Player is null");
            }
        });
    }

    @ReactMethod
    public void setCurrentQuality(final int reactTag, final int index) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().setCurrentQuality(index);
            }
        });
    }

    @ReactMethod
    public void getQualityLevels(final int reactTag, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                List<QualityLevel> qualityLevelsList = playerView.mPlayerView.getPlayer().getQualityLevels();
                if (qualityLevelsList == null) { //if qualitylevels are null than pass empty array.
                    promise.resolve(null);
                    return;
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
            } else {
                promise.reject("RNJW Error", "getQualityLevels() Player is null");
            }
        });
    }

    @ReactMethod
    public void pause(final int reactTag) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                if (!playerView.getIsCastActive()) {
                    playerView.mPlayerView.getPlayer().pause();
                    playerView.userPaused = true;
                }
            }
        });
    }

    @ReactMethod
    public void stop(final int reactTag) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                if (!playerView.getIsCastActive()) {
                    playerView.mPlayerView.getPlayer().stop();
                    playerView.userPaused = true;
                }
            }
        });
    }

    @ReactMethod
    public void seekTo(final int reactTag, final double time) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().seek(time);
            }
        });
    }

    @ReactMethod
    public void setPlaylistIndex(final int reactTag, final int index) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().playlistItem(index);
            }
        });
    }

    @ReactMethod
    public void setControls(final int reactTag, final boolean show) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().setControls(show);
            }
        });
    }

    @ReactMethod
    public void position(final int reactTag, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                promise.resolve((Double.valueOf(playerView.mPlayerView.getPlayer().getPosition()).intValue()));
            } else {
                promise.reject("RNJW Error", "Player is null");
            }
        });
    }

    @ReactMethod
    public void state(final int reactTag, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                PlayerState playerState = playerView.mPlayerView.getPlayer().getState();
                promise.resolve(stateToInt(playerState));
            } else {
                promise.reject("RNJW Error", "Player is null");
            }
        });
    }

    @ReactMethod
    public void setFullscreen(final int reactTag, final boolean fullscreen) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().setFullscreen(fullscreen, fullscreen);
            }
        });
    }

    @ReactMethod
    public void setVolume(final int reactTag, final int volume) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayerView != null) {
                playerView.mPlayerView.getPlayer().setVolume(volume);
            }
        });
    }

    @ReactMethod
    public void getAudioTracks(final int reactTag, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayer != null) {
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
            } else {
                promise.reject("RNJW Error", "Player is null");
            }
        });
    }

    @ReactMethod
    public void getCurrentAudioTrack(final int reactTag, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayer != null) {
                promise.resolve(playerView.mPlayer.getCurrentAudioTrack());
            } else {
                promise.reject("RNJW Error", "Player is null");
            }
        });
    }

    @ReactMethod
    public void setCurrentAudioTrack(final int reactTag, final int index) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayer != null) {
                playerView.mPlayer.setCurrentAudioTrack(index);
            }
        });
    }

    @ReactMethod
    public void setCurrentCaptions(final int reactTag, final int index) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayer != null) {
                playerView.mPlayer.setCurrentCaptions(index);
            }
        });
    }

    @ReactMethod
    public void getCurrentCaptions(final int reactTag, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(() -> {
            RNJWPlayerView playerView = getPlayerView(reactTag);
            if (playerView != null && playerView.mPlayer != null) {
                promise.resolve(playerView.mPlayer.getCurrentCaptions());
            } else {
                promise.reject("RNJW Error", "Player is null");
            }
        });
    }

    @ReactMethod
    public void resolveNextPlaylistItem(final int reactTag, final ReadableMap playlistItem) {
        try {
            UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
            uiManager.addUIBlock(new UIBlock() {
                public void execute(NativeViewHierarchyManager nvhm) {
                    RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

                    if (playerView != null) {
                        playerView.resolveNextPlaylistItem(playlistItem);
                    }
                }
            });
        } catch (IllegalViewOperationException e) {
            throw e;
        }
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