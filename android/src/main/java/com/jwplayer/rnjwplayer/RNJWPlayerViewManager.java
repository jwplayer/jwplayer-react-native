package com.jwplayer.rnjwplayer;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nonnull;

public class RNJWPlayerViewManager extends SimpleViewManager<RNJWPlayerView> {

  public static final String REACT_CLASS = "RNJWPlayerView";

  private final ReactApplicationContext mAppContext;

  private static final String TAG = "RNJWPlayerViewManager";

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  public RNJWPlayerViewManager(ReactApplicationContext context) {
    mAppContext = context;
  }

  @Override
  public RNJWPlayerView createViewInstance(ThemedReactContext context) {
    return new RNJWPlayerView(context, mAppContext);
  }

  @ReactProp(name = "config")
  public void setConfig(RNJWPlayerView view, ReadableMap prop) {
    view.setConfig(prop);
  }

  @ReactProp(name = "controls")
  public void setControls(RNJWPlayerView view, Boolean controls) {
    if (view == null || view.mPlayerView == null) {
      return;
    }
    // Add null check for getPlayer() to prevent crashes
    try {
      if (view.mPlayerView.getPlayer() != null) {
        view.mPlayerView.getPlayer().setControls(controls);
      }
    } catch (Exception e) {
      Log.w(TAG, "Error setting controls: " + e.getMessage());
    }
  }

  /**
   * Recreates the player with a new configuration, handling cleanup and PiP state.
   * This method ensures proper cleanup and state restoration during configuration changes.
   *
   * @param view The RNJWPlayerView instance
   * @param config The new configuration to apply
   */
  @ReactProp(name = "recreatePlayerWithConfig")
  public void recreatePlayerWithConfig(RNJWPlayerView view, ReadableMap config) {
    if (view == null || view.mPlayerView == null) {
      return;
    }
    // Add null check for getPlayer() to prevent crashes
    try {
      if (view.mPlayerView.getPlayer() != null) {
        view.mPlayerView.getPlayer().stop();
      }
      view.setConfig(config);
    } catch (Exception e) {
      Log.w(TAG, "Error recreating player: " + e.getMessage());
    }
  }

  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder()
            .put(
                    "topPlayerError",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlayerError")))
            .put("topSetupPlayerError",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onSetupPlayerError")))
            .put("topPlayerAdError",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlayerAdError")))
            .put("topPlayerAdWarning",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlayerAdWarning")))
            .put("topAdEvent",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onAdEvent")))
            .put("topAdTime",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onAdTime")))
            .put("topTime",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onTime")))
            .put("topBuffer",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onBuffer")))
            .put("topFullScreen",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onFullScreen")))
            .put("topFullScreenExitRequested",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onFullScreenExitRequested")))
            .put("topFullScreenRequested",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onFullScreenRequested")))
            .put("topFullScreenExit",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onFullScreenExit")))
            .put("topPause",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPause")))
            .put("topPlay",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlay")))
            .put("topComplete",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onComplete")))
            .put("topPlaylistComplete",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlaylistComplete")))
            .put("topPlaylistItem",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlaylistItem")))
            .put("topSeek",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onSeek")))
            .put("topSeeked",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onSeeked")))
            .put("topRateChanged",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onRateChanged")))
            .put("topControlBarVisible",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onControlBarVisible")))
            .put("topOnPlayerReady",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlayerReady")))
            .put("topBeforePlay",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onBeforePlay")))
            .put("topBeforeComplete",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onBeforeComplete")))
            .put("topAdPlay",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onAdPlay")))
            .put("topAdPause",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onAdPause")))
            .put("topAudioTracks",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onAudioTracks")))
            .put("topCaptionsChanged",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onCaptionsChanged")))
            .put("topCaptionsList",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onCaptionsList")))
            .put("topCasting",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onCasting")))
            .put("topFirstFrame",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onLoaded")))
            .put("topBeforeNextPlaylistItem",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onBeforeNextPlaylistItem")))
            .build();
  }

  @Override
  public void onDropViewInstance(@Nonnull RNJWPlayerView view) {
    view.destroyPlayer();
    super.onDropViewInstance(view);
    view = null;
  }
}
