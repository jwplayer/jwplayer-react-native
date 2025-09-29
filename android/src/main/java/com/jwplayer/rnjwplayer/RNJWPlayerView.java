package com.jwplayer.rnjwplayer;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.jwplayer.pub.api.JWPlayer;
import com.jwplayer.pub.api.JsonHelper;
import com.jwplayer.pub.api.UiGroup;
import com.jwplayer.pub.api.background.MediaService;
import com.jwplayer.pub.api.background.MediaServiceController;
import com.jwplayer.pub.api.configuration.PlayerConfig;
import com.jwplayer.pub.api.configuration.UiConfig;
import com.jwplayer.pub.api.configuration.ads.AdvertisingConfig;
import com.jwplayer.pub.api.events.AdBreakEndEvent;
import com.jwplayer.pub.api.events.AdBreakIgnoredEvent;
import com.jwplayer.pub.api.events.AdBreakStartEvent;
import com.jwplayer.pub.api.events.AdClickEvent;
import com.jwplayer.pub.api.events.AdCompanionsEvent;
import com.jwplayer.pub.api.events.AdCompleteEvent;
import com.jwplayer.pub.api.events.AdErrorEvent;
import com.jwplayer.pub.api.events.AdImpressionEvent;
import com.jwplayer.pub.api.events.AdLoadedEvent;
import com.jwplayer.pub.api.events.AdLoadedXmlEvent;
import com.jwplayer.pub.api.events.AdMetaEvent;
import com.jwplayer.pub.api.events.AdPauseEvent;
import com.jwplayer.pub.api.events.AdPlayEvent;
import com.jwplayer.pub.api.events.AdRequestEvent;
import com.jwplayer.pub.api.events.AdScheduleEvent;
import com.jwplayer.pub.api.events.AdSkippedEvent;
import com.jwplayer.pub.api.events.AdStartedEvent;
import com.jwplayer.pub.api.events.AdTimeEvent;
import com.jwplayer.pub.api.events.AdViewableImpressionEvent;
import com.jwplayer.pub.api.events.AdWarningEvent;
import com.jwplayer.pub.api.events.AudioTrackChangedEvent;
import com.jwplayer.pub.api.events.AudioTracksEvent;
import com.jwplayer.pub.api.events.BeforeCompleteEvent;
import com.jwplayer.pub.api.events.BeforePlayEvent;
import com.jwplayer.pub.api.events.BufferEvent;
import com.jwplayer.pub.api.events.CaptionsChangedEvent;
import com.jwplayer.pub.api.events.CaptionsListEvent;
import com.jwplayer.pub.api.events.CastEvent;
import com.jwplayer.pub.api.events.CompleteEvent;
import com.jwplayer.pub.api.events.ControlBarVisibilityEvent;
import com.jwplayer.pub.api.events.ControlsEvent;
import com.jwplayer.pub.api.events.DisplayClickEvent;
import com.jwplayer.pub.api.events.ErrorEvent;
import com.jwplayer.pub.api.events.EventType;
import com.jwplayer.pub.api.events.FirstFrameEvent;
import com.jwplayer.pub.api.events.FullscreenEvent;
import com.jwplayer.pub.api.events.IdleEvent;
import com.jwplayer.pub.api.events.MetaEvent;
import com.jwplayer.pub.api.events.PauseEvent;
import com.jwplayer.pub.api.events.PipCloseEvent;
import com.jwplayer.pub.api.events.PipOpenEvent;
import com.jwplayer.pub.api.events.PlayEvent;
import com.jwplayer.pub.api.events.PlaybackRateChangedEvent;
import com.jwplayer.pub.api.events.PlaylistCompleteEvent;
import com.jwplayer.pub.api.events.PlaylistEvent;
import com.jwplayer.pub.api.events.PlaylistItemEvent;
import com.jwplayer.pub.api.events.ReadyEvent;
import com.jwplayer.pub.api.events.SeekEvent;
import com.jwplayer.pub.api.events.SeekedEvent;
import com.jwplayer.pub.api.events.SetupErrorEvent;
import com.jwplayer.pub.api.events.TimeEvent;
import com.jwplayer.pub.api.events.listeners.AdvertisingEvents;
import com.jwplayer.pub.api.events.listeners.CastingEvents;
import com.jwplayer.pub.api.events.listeners.PipPluginEvents;
import com.jwplayer.pub.api.events.listeners.VideoPlayerEvents;
import com.jwplayer.pub.api.fullscreen.ExtensibleFullscreenHandler;
import com.jwplayer.pub.api.fullscreen.FullscreenDialog;
import com.jwplayer.pub.api.fullscreen.FullscreenHandler;
import com.jwplayer.pub.api.fullscreen.delegates.DeviceOrientationDelegate;
import com.jwplayer.pub.api.fullscreen.delegates.DialogLayoutDelegate;
import com.jwplayer.pub.api.fullscreen.delegates.SystemUiDelegate;
import com.jwplayer.pub.api.license.LicenseUtil;
import com.jwplayer.pub.api.media.captions.Caption;
import com.jwplayer.pub.api.media.playlists.PlaylistItem;
import com.jwplayer.ui.views.CueMarkerSeekbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RNJWPlayerView extends RelativeLayout implements
        VideoPlayerEvents.OnFullscreenListener,
        VideoPlayerEvents.OnReadyListener,
        VideoPlayerEvents.OnPlayListener,
        VideoPlayerEvents.OnPauseListener,
        VideoPlayerEvents.OnCompleteListener,
        VideoPlayerEvents.OnIdleListener,
        VideoPlayerEvents.OnErrorListener,
        VideoPlayerEvents.OnSetupErrorListener,
        VideoPlayerEvents.OnBufferListener,
        VideoPlayerEvents.OnTimeListener,
        VideoPlayerEvents.OnPlaylistListener,
        VideoPlayerEvents.OnPlaylistItemListener,
        VideoPlayerEvents.OnPlaylistCompleteListener,
        VideoPlayerEvents.OnAudioTracksListener,
        VideoPlayerEvents.OnAudioTrackChangedListener,
        VideoPlayerEvents.OnControlsListener,
        VideoPlayerEvents.OnControlBarVisibilityListener,
        VideoPlayerEvents.OnDisplayClickListener,
        VideoPlayerEvents.OnFirstFrameListener,
        VideoPlayerEvents.OnSeekListener,
        VideoPlayerEvents.OnSeekedListener,
        VideoPlayerEvents.OnPlaybackRateChangedListener,
        VideoPlayerEvents.OnCaptionsListListener,
        VideoPlayerEvents.OnCaptionsChangedListener,
        VideoPlayerEvents.OnMetaListener,
        VideoPlayerEvents.PlaylistItemCallbackListener,

        CastingEvents.OnCastListener,

        PipPluginEvents.OnPipCloseListener,
        PipPluginEvents.OnPipOpenListener,

        AdvertisingEvents.OnBeforePlayListener,
        AdvertisingEvents.OnBeforeCompleteListener,
        AdvertisingEvents.OnAdPauseListener,
        AdvertisingEvents.OnAdPlayListener,
        AdvertisingEvents.OnAdRequestListener,
        AdvertisingEvents.OnAdScheduleListener,
        AdvertisingEvents.OnAdStartedListener,
        AdvertisingEvents.OnAdBreakStartListener,
        AdvertisingEvents.OnAdBreakEndListener,
        AdvertisingEvents.OnAdClickListener,
        AdvertisingEvents.OnAdCompleteListener,
        AdvertisingEvents.OnAdCompanionsListener,
        AdvertisingEvents.OnAdErrorListener,
        AdvertisingEvents.OnAdImpressionListener,
        AdvertisingEvents.OnAdMetaListener,
        AdvertisingEvents.OnAdSkippedListener,
        AdvertisingEvents.OnAdTimeListener,
        AdvertisingEvents.OnAdViewableImpressionListener,
        AdvertisingEvents.OnAdBreakIgnoredListener,
        AdvertisingEvents.OnAdWarningListener,
        AdvertisingEvents.OnAdLoadedListener,
        AdvertisingEvents.OnAdLoadedXmlListener,

        AudioManager.OnAudioFocusChangeListener,

        LifecycleEventListener, LifecycleOwner {
    public RNJWPlayer mPlayerView = null;
    public JWPlayer mPlayer = null;

    private ViewGroup mRootView;

    // Props
    ReadableMap mConfig = null;
    ReadableArray mPlaylistProp = null;
    ReadableMap mColors = null;

    Boolean backgroundAudioEnabled = false;

    Boolean landscapeOnFullScreen = false;
    Boolean fullScreenOnLandscape = false;
    Boolean portraitOnExitFullScreen = false;
    Boolean exitFullScreenOnPortrait = false;
    Boolean playerInModal = false;

    Number currentPlayingIndex;

    private static final String TAG = "RNJWPlayerView";

    static ReactActivity mActivity;

    Window mWindow;

    public static AudioManager audioManager;

    final Object focusLock = new Object();

    AudioFocusRequest focusRequest;

    boolean hasAudioFocus = false;
    boolean playbackDelayed = false;
    boolean playbackNowAuthorized = false;
    boolean userPaused = false;
    boolean wasInterrupted = false;

    private static int sessionDepth = 0;
    boolean isInBackground = false;

    private final ReactApplicationContext mAppContext;

    private ThemedReactContext mThemedReactContext;

    private MediaServiceController mMediaServiceController;
    private PipHandlerReceiver mReceiver = null;

    // Add completion handler field
    PlaylistItemDecision itemUpdatePromise = null;

    private void doBindService() {
        if (mMediaServiceController != null) {
            if (!isBackgroundAudioServiceRunning()) {
                // This may not be your expected behavior, but is necessary to avoid crashing
                // Do not use multiple player instances with background audio enabled

                // don't rebind me if the service is already active with a player.
                mMediaServiceController.bindService();
            }
        }
    }

    private void doUnbindService() {
        if (mMediaServiceController != null) {
            mMediaServiceController.unbindService();
            mMediaServiceController = null;
        }
    }

    private static boolean contextHasBug(Context context) {
        return context == null ||
                context.getResources() == null ||
                context.getResources().getConfiguration() == null;
    }

    private static Context getNonBuggyContext(ThemedReactContext reactContext,
                                              ReactApplicationContext appContext) {
        Context superContext = reactContext;
        if (!contextHasBug(appContext.getCurrentActivity())) {
            superContext = appContext.getCurrentActivity();
        } else if (contextHasBug(superContext)) {
            // we have the bug! let's try to find a better context to use
            if (!contextHasBug(reactContext.getCurrentActivity())) {
                superContext = reactContext.getCurrentActivity();
            } else if (!contextHasBug(reactContext.getApplicationContext())) {
                superContext = reactContext.getApplicationContext();
            } else {
                // ¯\_(ツ)_/¯
            }
        }
        return superContext;
    }

    private boolean isBackgroundAudioServiceRunning() {
        ActivityManager manager = (ActivityManager) mAppContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MediaService.class.getName().equals(service.service.getClassName())) {
                Log.w(TAG, "MediaService is already running with another player loaded. To avoid crashing, this player, "
                        + mPlayerView.getTag() + "  will not be loaded into the background service.");
                return true;
            }
        }
        return false;
    }

    public RNJWPlayerView(ThemedReactContext reactContext, ReactApplicationContext appContext) {
        super(getNonBuggyContext(reactContext, appContext));
        mAppContext = appContext;

        registry.setCurrentState(Lifecycle.State.CREATED);
        mThemedReactContext = reactContext;

        mActivity = (ReactActivity) getActivity();
        if (mActivity != null) {
            mWindow = mActivity.getWindow();
        }

        if (mActivity != null) {
            mActivity.getLifecycle().addObserver(lifecycleObserver);
        }

        mRootView = mActivity.findViewById(android.R.id.content);

        getReactContext().addLifecycleEventListener(this);
    }

    private LifecycleObserver lifecycleObserver = new LifecycleEventObserver() {
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event.getTargetState() == Lifecycle.State.DESTROYED) {
                return; // no op: handled elsewhere
            }
            registry.setCurrentState(event.getTargetState());
        }
    };

    public ReactApplicationContext getAppContext() {
        return mAppContext;
    }

    public ThemedReactContext getReactContext() {
        return mThemedReactContext;
    }

    public Activity getActivity() {
        if (!contextHasBug(mAppContext.getCurrentActivity())) {
            return mAppContext.getCurrentActivity();
        } else if (contextHasBug(mThemedReactContext)) {
            if (!contextHasBug(mThemedReactContext.getCurrentActivity())) {
                return mThemedReactContext.getCurrentActivity();
            } else if (!contextHasBug(mThemedReactContext.getApplicationContext())) {
                return (Activity) mThemedReactContext.getApplicationContext();
            }
        }

        return mThemedReactContext.getReactApplicationContext().getCurrentActivity();
    }

    // The registry for lifecycle events. Required by player object. Main use case if for garbage collection / teardown
    private final LifecycleRegistry registry = new LifecycleRegistry(this);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    // // closest to `ondestroy` for a view without listening to the activity event
    // // The activity event can be deceptive in React-Native
    // @Override
    // protected void onDetachedFromWindow() {
    //     super.onDetachedFromWindow();
    //     registry.setCurrentState(Lifecycle.State.DESTROYED);
    // }

    public void destroyPlayer() {
        if (mPlayer != null) {
            unRegisterReceiver();

            // If we are casting we need to break the cast session as there is no simple
            // way to reconnect to an existing session if the player that created it is dead
            
            // If this doesn't match your use case, using a single player object and load content
            // into it rather than creating a new player for every piece of content. 
            mPlayer.stop();

            // send signal to JW SDK player is destroyed
            registry.setCurrentState(Lifecycle.State.DESTROYED);

            // Stop listening to activities lifecycle
            mActivity.getLifecycle().removeObserver(lifecycleObserver);
            mPlayer.deregisterActivityForPip();

            // Remove playlist item callback listener
            mPlayer.removePlaylistItemCallbackListener();

            mPlayer.removeListeners(this,
                    // VideoPlayerEvents
                    EventType.READY,
                    EventType.PLAY,
                    EventType.PAUSE,
                    EventType.COMPLETE,
                    EventType.IDLE,
                    EventType.ERROR,
                    EventType.SETUP_ERROR,
                    EventType.BUFFER,
                    EventType.TIME,
                    EventType.PLAYLIST,
                    EventType.PLAYLIST_ITEM,
                    EventType.PLAYLIST_COMPLETE,
                    EventType.FIRST_FRAME,
                    EventType.CONTROLS,
                    EventType.CONTROLBAR_VISIBILITY,
                    EventType.DISPLAY_CLICK,
                    EventType.FULLSCREEN,
                    EventType.SEEK,
                    EventType.SEEKED,
                    EventType.PLAYBACK_RATE_CHANGED,
                    EventType.CAPTIONS_LIST,
                    EventType.CAPTIONS_CHANGED,
                    EventType.META,

                    // Ad events
                    EventType.BEFORE_PLAY,
                    EventType.BEFORE_COMPLETE,
                    EventType.AD_BREAK_START,
                    EventType.AD_BREAK_END,
                    EventType.AD_BREAK_IGNORED,
                    EventType.AD_CLICK,
                    EventType.AD_COMPANIONS,
                    EventType.AD_COMPLETE,
                    EventType.AD_ERROR,
                    EventType.AD_IMPRESSION,
                    EventType.AD_WARNING,
                    EventType.AD_LOADED,
                    EventType.AD_LOADED_XML,
                    EventType.AD_META,
                    EventType.AD_PAUSE,
                    EventType.AD_PLAY,
                    EventType.AD_REQUEST,
                    EventType.AD_SCHEDULE,
                    EventType.AD_SKIPPED,
                    EventType.AD_STARTED,
                    EventType.AD_TIME,
                    EventType.AD_VIEWABLE_IMPRESSION,
                    // Cast event
                    EventType.CAST,
                    // Pip events
                    EventType.PIP_CLOSE,
                    EventType.PIP_OPEN
            );

            mPlayer = null;
            mPlayerView = null;

            getReactContext().removeLifecycleEventListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (audioManager != null && focusRequest != null) {
                    audioManager.abandonAudioFocusRequest(focusRequest);
                }
            } else {
                if (audioManager != null) {
                    audioManager.abandonAudioFocus(this);
                }
            }

            audioManager = null;

            doUnbindService();
        }
    }

    public void setupPlayerView(Boolean backgroundAudioEnabled, Boolean playlistItemCallbackEnabled) {
        if (mPlayer != null) {

            mPlayer.addListeners(this,
                    // VideoPlayerEvents
                    EventType.READY,
                    EventType.PLAY,
                    EventType.PAUSE,
                    EventType.COMPLETE,
                    EventType.IDLE,
                    EventType.ERROR,
                    EventType.SETUP_ERROR,
                    EventType.BUFFER,
                    EventType.TIME,
                    EventType.AUDIO_TRACKS,
                    EventType.AUDIO_TRACK_CHANGED,
                    EventType.PLAYLIST,
                    EventType.PLAYLIST_ITEM,
                    EventType.PLAYLIST_COMPLETE,
                    EventType.FIRST_FRAME,
                    EventType.CONTROLS,
                    EventType.CONTROLBAR_VISIBILITY,
                    EventType.DISPLAY_CLICK,
                    EventType.FULLSCREEN,
                    EventType.SEEK,
                    EventType.SEEKED,
                    EventType.PLAYBACK_RATE_CHANGED,
                    EventType.CAPTIONS_LIST,
                    EventType.CAPTIONS_CHANGED,
                    EventType.META,
                    // Ad events
                    EventType.BEFORE_PLAY,
                    EventType.BEFORE_COMPLETE,
                    EventType.AD_BREAK_START,
                    EventType.AD_BREAK_END,
                    EventType.AD_BREAK_IGNORED,
                    EventType.AD_CLICK,
                    EventType.AD_COMPANIONS,
                    EventType.AD_COMPLETE,
                    EventType.AD_ERROR,
                    EventType.AD_IMPRESSION,
                    EventType.AD_WARNING,
                    EventType.AD_LOADED,
                    EventType.AD_LOADED_XML,
                    EventType.AD_META,
                    EventType.AD_PAUSE,
                    EventType.AD_PLAY,
                    EventType.AD_REQUEST,
                    EventType.AD_SCHEDULE,
                    EventType.AD_SKIPPED,
                    EventType.AD_STARTED,
                    EventType.AD_TIME,
                    EventType.AD_VIEWABLE_IMPRESSION,
                    // Cast event
                    EventType.CAST,
                    // Pip events
                    EventType.PIP_CLOSE,
                    EventType.PIP_OPEN
            );

            if (playerInModal) {
                mPlayer.setFullscreenHandler(createModalFullscreenHandler());
            } else {
                mPlayer.setFullscreenHandler(new fullscreenHandler());
            }
            mPlayer.allowBackgroundAudio(backgroundAudioEnabled);

            if (playlistItemCallbackEnabled) {
                mPlayer.setPlaylistItemCallbackListener(this);
            }
        }
    }

   public void resolveNextPlaylistItem(ReadableMap playlistItem) {
        if (itemUpdatePromise == null) {
            return;
        }

        if (playlistItem == null) {
            itemUpdatePromise.continuePlayback();
            itemUpdatePromise = null;
            return;
        }

        try {
            PlaylistItem updatedPlaylistItem = Util.getPlaylistItem(playlistItem);
            itemUpdatePromise.modify(updatedPlaylistItem);
        } catch (Exception exception) {
            itemUpdatePromise.continuePlayback();
        }

        itemUpdatePromise = null;
    }

    /**
     * Helper to build the a generic `ExtensibleFullscreenHandler` with small tweaks to play nice with Modals
     *
     * @return {@link ExtensibleFullscreenHandler}
     */
    private ExtensibleFullscreenHandler createModalFullscreenHandler() {
        DeviceOrientationDelegate delegate = getDeviceOrientationDelegate();
        FullscreenDialog dialog = new FullscreenDialog(
                mActivity,
                mActivity,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
        );

        return new ExtensibleFullscreenHandler(
                new DialogLayoutDelegate(
                        mPlayerView,
                        dialog
                ),
                delegate,
                new SystemUiDelegate(
                        mActivity,
                        mActivity.getLifecycle(),
                        new Handler(),
                        dialog.getWindow().getDecorView()
                )
        ) {
            @Override
            public void onFullscreenRequested() {
                // if landscape is priorty we have to turn off full-screen portrait before allowing
                // the default call for full-screen
                mPlayer.allowFullscreenPortrait(!landscapeOnFullScreen);
                super.onFullscreenRequested();
                // safely set it back on UI thread after work can be finished
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    if (mPlayer != null) {
                        mPlayer.allowFullscreenPortrait(true);
                    }
                }, 100);
                WritableMap eventEnterFullscreen = Arguments.createMap();
                eventEnterFullscreen.putString("message", "onFullscreenRequested");
                getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                        getId(),
                        "topFullScreenRequested",
                        eventEnterFullscreen);
            }

            @Override
            public void onFullscreenExitRequested() {
                super.onFullscreenExitRequested();

                WritableMap eventExitFullscreen = Arguments.createMap();
                eventExitFullscreen.putString("message", "onFullscreenExitRequested");
                getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                        getId(),
                        "topFullScreenExitRequested",
                        eventExitFullscreen);
            }
        };
    }

    /**
     * Add logic here for your custom orientation implementation
     *
     * @return Default {@link DeviceOrientationDelegate}
     */
    private DeviceOrientationDelegate getDeviceOrientationDelegate() {
        DeviceOrientationDelegate delegate = new DeviceOrientationDelegate(
                mActivity,
                mActivity.getLifecycle(),
                new Handler()
        ) {
            @Override
            public void setFullscreen(boolean fullscreen) {
                super.setFullscreen(fullscreen);
            }

            @Override
            public void onAllowRotationChanged(boolean allowRotation) {
                super.onAllowRotationChanged(allowRotation);
            }

            @Override
            protected void doRotation(boolean fullscreen, boolean allowFullscreenPortrait) {
                super.doRotation(fullscreen, allowFullscreenPortrait);
            }

            @Override
            protected void doRotationListener() {
                super.doRotationListener();
            }

            @Override
            public void onAllowFullscreenPortrait(boolean allowFullscreenPortrait) {
                super.onAllowFullscreenPortrait(allowFullscreenPortrait);
            }
        };
        delegate.onAllowRotationChanged(true);
        return delegate;
    }

    @Override
    public void onBeforeNextPlaylistItem(PlaylistItemDecision playlistItemDecision, PlaylistItem nextItem, int indexOfNextItem) {
        WritableMap event = Arguments.createMap();
        Gson gson = new Gson();
        event.putString("message", "onBeforeNextPlaylistItem");
        event.putInt("index", indexOfNextItem);
        event.putString("playlistItem", gson.toJson(nextItem));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topBeforeNextPlaylistItem", event);

        itemUpdatePromise = playlistItemDecision;
    }

    private class fullscreenHandler implements FullscreenHandler {
        ViewGroup mPlayerViewContainer = (ViewGroup) mPlayerView.getParent();
        private View mDecorView;

        @Override
        public void onFullscreenRequested() {
            mDecorView = mActivity.getWindow().getDecorView();

            // Hide system ui
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hides bottom bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hides top bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // prevents navigation bar from overriding
                    // exit-full-screen button. Swipe from side to access nav bar.
            );

            // Enter landscape mode for fullscreen videos
            if (landscapeOnFullScreen) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            mPlayerViewContainer = (ViewGroup) mPlayerView.getParent();

            // Remove the JWPlayerView from the list item.
            if (mPlayerViewContainer != null) {
                mPlayerViewContainer.removeView(mPlayerView);
            }

            // Initialize a new rendering surface.
            // mPlayerView.initializeSurface();

            // Add the JWPlayerView to the RootView as soon as the UI thread is ready.
            mRootView.post(new Runnable() {
                @Override
                public void run() {
                    mRootView.addView(mPlayerView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                }
            });

            WritableMap eventEnterFullscreen = Arguments.createMap();
            eventEnterFullscreen.putString("message", "onFullscreenRequested");
            getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                    getId(),
                    "topFullScreenRequested",
                    eventEnterFullscreen);
        }

        @Override
        public void onFullscreenExitRequested() {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_VISIBLE // clear the hide system flags
            );

            // Enter portrait mode
            if (portraitOnExitFullScreen) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            // Remove the player view from the root ViewGroup.
            mRootView.removeView(mPlayerView);

            // As soon as the UI thread has finished processing the current message queue it
            // should add the JWPlayerView back to the list item.
            mPlayerViewContainer.post(new Runnable() {
                @Override
                public void run() {
                    // View may not have been removed properly (especially if returning from PiP)
                    mPlayerViewContainer.removeView(mPlayerView);
                    
                    mPlayerViewContainer.addView(mPlayerView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    // returning from full-screen portrait requires a different measure
                    if (mActivity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    ) {
                        mPlayerView.layout(mPlayerView.getLeft(), mPlayerViewContainer.getTop(),
                                mPlayerViewContainer.getMeasuredWidth(), mPlayerViewContainer.getBottom());
                    } else {
                        mPlayerView.layout(mPlayerViewContainer.getLeft(), mPlayerViewContainer.getTop(),
                                mPlayerViewContainer.getRight(), mPlayerViewContainer.getBottom());
                    }
                }
            });

            WritableMap eventExitFullscreen = Arguments.createMap();
            eventExitFullscreen.putString("message", "onFullscreenExitRequested");
            getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                    getId(),
                    "topFullScreenExitRequested",
                    eventExitFullscreen);
        }

        @Override
        public void onAllowRotationChanged(boolean b) {
            Log.e(TAG, "onAllowRotationChanged: " + b);
        }

        @Override
        public void onAllowFullscreenPortraitChanged(boolean allowFullscreenPortrait) {
            Log.e(TAG, "onAllowFullscreenPortraitChanged: " + allowFullscreenPortrait);
        }

        @Override
        public void updateLayoutParams(ViewGroup.LayoutParams layoutParams) {
            // View.setSystemUiVisibility(int).
            // Log.e(TAG, "updateLayoutParams: "+layoutParams );
        }

        @Override
        public void setUseFullscreenLayoutFlags(boolean b) {
            // View.setSystemUiVisibility(int).
            // Log.e(TAG, "setUseFullscreenLayoutFlags: "+b );
        }
    }

    private ArrayList<Integer> rootViewChildrenOriginalVisibility = new ArrayList<Integer>();

    private class PipHandlerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (Objects.equals(intent.getAction(), "onPictureInPictureModeChanged")) {
                if (intent.hasExtra("newConfig") && intent.hasExtra("isInPictureInPictureMode")) {
                    // Tell the JWP SDK we are toggling so it can handle toolbar / internal setup
                    mPlayer.onPictureInPictureModeChanged(intent.getBooleanExtra("isInPictureInPictureMode", false), intent.getParcelableExtra("newConfig"));

                    View decorView = mActivity.getWindow().getDecorView();
                    ViewGroup rootView = decorView.findViewById(android.R.id.content);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);

                    if (intent.getBooleanExtra("isInPictureInPictureMode", false)) {
                        // Going into Picture in Picture
                        ViewGroup parent = (ViewGroup) mPlayerView.getParent();

                        // Remove the player view temporarily
                        if (parent != null) {
                            parent.removeView(mPlayerView);
                        }

                        // Hide all views but player view and keep a handle on them for later
                        for (int i = 0; i < rootView.getChildCount(); i++) {
                            if (rootView.getChildAt(i) != mPlayerView) {
                                rootViewChildrenOriginalVisibility.add(rootView.getChildAt(i).getVisibility());
                                rootView.getChildAt(i).setVisibility(View.GONE);
                            }
                        }
                        // Add player view back (This is safe since the JWP SDK has already calculated the PiP size/aspect off the View)
                        rootView.addView(mPlayerView, layoutParams);
                    } else {
                        // Exiting Picture in Picture

                        // Toggle controls to ensure we don't lose them -- weird UX bug fix where controls got lost
                        mPlayer.setForceControlsVisibility(true);
                        mPlayer.setForceControlsVisibility(false);

                        // If player was in fullscreen when going into PiP, we need to force it back out
                        if (mPlayer.getFullscreen()) {
                            mPlayer.setFullscreen(false, true);
                        }

                        // Strip player view
                        rootView.removeView(mPlayerView);

                        // Add visibility back to  any other controls
                        for (int i = 0; i < rootView.getChildCount(); i++) {
                            rootView.getChildAt(i).setVisibility(rootViewChildrenOriginalVisibility.get(i));
                        }
                        // Clear our list of views
                        rootViewChildrenOriginalVisibility.clear();
                        // Add player view back in main spot
                        addView(mPlayerView, 0, layoutParams);
                    }
                }
            }
        }
    }

    private void registerReceiver() {
        mReceiver = new PipHandlerReceiver();
        IntentFilter intentFilter = new IntentFilter("onPictureInPictureModeChanged");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Must be Exported to get intents
                mActivity.registerReceiver(mReceiver, intentFilter, Context.RECEIVER_EXPORTED);
            } else {
                mActivity.registerReceiver(mReceiver, intentFilter); // Safe API level < 34
            }
        } else {
            mActivity.registerReceiver(mReceiver, intentFilter); // Safe API level < 34
        }
    }

    private void unRegisterReceiver() {
        if (mReceiver != null) {
            mActivity.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

    }

    /**
     * Creates a UiConfig that ensures PLAYER_CONTROLS_CONTAINER is always shown.
     * If controls are not shown, the PLAYER_CONTROLS_CONTAINER UI Group is not displayed.
     * This logic ensures that the PLAYER_CONTROLS_CONTAINER UI Group is displayed regardless if controls are shown or not.
     * There is no way to recover controls if you do not show this UiGroup.
     * But you are able to hide the controls still if it is shown.
     */
    private UiConfig createUiConfigWithControlsContainer(JWPlayer player, UiConfig originalUiConfig) {
        if (!player.getControls()) {
            return new UiConfig.Builder(originalUiConfig).show(UiGroup.PLAYER_CONTROLS_CONTAINER).build();
        } else {
            return originalUiConfig;
        }
    }

    public void setConfig(ReadableMap prop) {
        if (mConfig == null || !mConfig.equals(prop)) {
            if (mConfig != null && isOnlyDiff(prop, "playlist") && mPlayer != null) { // still safe check, even with JW
                // JSON change
                PlayerConfig oldConfig = mPlayer.getConfig();
                boolean wasFullscreen = mPlayer.getFullscreen();
                UiConfig uiConfig = createUiConfigWithControlsContainer(mPlayer, oldConfig.getUiConfig());
                PlayerConfig config = new PlayerConfig.Builder()
                        .autostart(oldConfig.getAutostart())
                        .nextUpOffset(oldConfig.getNextUpOffset())
                        .repeat(oldConfig.getRepeat())
                        .relatedConfig(oldConfig.getRelatedConfig())
                        .displayDescription(oldConfig.getDisplayDescription())
                        .displayTitle(oldConfig.getDisplayTitle())
                        .advertisingConfig(oldConfig.getAdvertisingConfig())
                        .stretching(oldConfig.getStretching())
                        .uiConfig(uiConfig)
                        .playlist(Util.createPlaylist(mPlaylistProp))
                        .allowCrossProtocolRedirects(oldConfig.getAllowCrossProtocolRedirects())
                        .preload(oldConfig.getPreload())
                        .useTextureView(oldConfig.useTextureView())
                        .thumbnailPreview(oldConfig.getThumbnailPreview())
                        .mute(oldConfig.getMute())
                        .build();

                mPlayer.setup(config);
                // if the player was fullscreen, set it to fullscreen again as the player is recreated
                // The fullscreen view is still active but the internals don't know it is
                if (wasFullscreen) {
                    mPlayer.setFullscreen(true, true);
                }
            } else {
                if (prop.hasKey("license")) {
                    new LicenseUtil().setLicenseKey(getReactContext(), prop.getString("license"));
                } else {
                    Log.e(TAG, "JW SDK license not set");
                }

                // The entire config is different (other than the "playlist" key)
                this.setupPlayer(prop);
            }
        } else {
            // No change
        }

        mConfig = prop;
    }

    public boolean isOnlyDiff(ReadableMap prop, String keyName) {
        // Convert ReadableMap to HashMap
        Map<String, Object> mConfigMap = mConfig.toHashMap();
        Map<String, Object> propMap = prop.toHashMap();

        Map<String, Object> differences = new HashMap<>();

        // Find keys in mConfig that aren't in prop or have different values
        for (Map.Entry<String, Object> entry : mConfigMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!propMap.containsKey(key) || !propMap.get(key).equals(value)) {
                differences.put(key, value);
            }
        }

        // Find keys in prop that aren't in mConfig
        for (String key : propMap.keySet()) {
            if (!mConfigMap.containsKey(key)) {
                differences.put(key, propMap.get(key));
            }
        }

        return differences.size() == 1 && differences.containsKey(keyName);
    }

    private boolean playlistNotTheSame(ReadableMap prop) {
        return prop.hasKey("playlist") && mPlaylistProp != prop.getArray("playlist") && !Arrays
                .deepEquals(new ReadableArray[]{mPlaylistProp}, new ReadableArray[]{prop.getArray("playlist")});
    }

    private void setupPlayer(ReadableMap prop) {
        // Legacy
        PlayerConfig.Builder configBuilder = new PlayerConfig.Builder();

        JSONObject obj;
        PlayerConfig jwConfig = null;
        Boolean forceLegacy = prop.hasKey("forceLegacyConfig") ? prop.getBoolean("forceLegacyConfig") : false;
        Boolean playlistItemCallbackEnabled = prop.hasKey("playlistItemCallbackEnabled") ? prop.getBoolean("playlistItemCallbackEnabled") : false;
        Boolean isJwConfig = false;
        if (!forceLegacy) {
            try {
                obj = MapUtil.toJSONObject(prop);
                jwConfig = JsonHelper.parseConfigJson(obj);
                isJwConfig = true;
            } catch (Exception ex) {
                Log.e("RNJWPlayerView", ex.toString());
                isJwConfig = false; // not a valid jw config. Try to setup in legacy
            }
        }

        if (!isJwConfig) {
            // Legacy
            if (playlistNotTheSame(prop)) {
                List<PlaylistItem> playlist = new ArrayList<>();
                mPlaylistProp = prop.getArray("playlist");
                if (mPlaylistProp != null && mPlaylistProp.size() > 0) {

                    int j = 0;
                    while (mPlaylistProp.size() > j) {
                        ReadableMap playlistItem = mPlaylistProp.getMap(j);

                        PlaylistItem newPlayListItem = Util.getPlaylistItem((playlistItem));
                        playlist.add(newPlayListItem);
                        j++;
                    }
                }

                configBuilder.playlist(playlist);
            }

            // Legacy
            if (prop.hasKey("autostart")) {
                boolean autostart = prop.getBoolean("autostart");
                configBuilder.autostart(autostart);
            }

            // Legacy
            if (prop.hasKey("nextUpStyle")) {
                ReadableMap nextUpStyle = prop.getMap("nextUpStyle");
                if (nextUpStyle != null && nextUpStyle.hasKey("offsetSeconds")
                        && nextUpStyle.hasKey("offsetPercentage")) {
                    int offsetSeconds = prop.getInt("offsetSeconds");
                    int offsetPercentage = prop.getInt("offsetPercentage");
                    configBuilder.nextUpOffset(offsetSeconds).nextUpOffsetPercentage(offsetPercentage);
                }
            }

            // Legacy
            if (prop.hasKey("repeat")) {
                boolean repeat = prop.getBoolean("repeat");
                configBuilder.repeat(repeat);
            }

            // Legacy
            if (prop.hasKey("styling")) {
                ReadableMap styling = prop.getMap("styling");
                if (styling != null) {
                    if (styling.hasKey("displayDescription")) {
                        boolean displayDescription = styling.getBoolean("displayDescription");
                        configBuilder.displayDescription(displayDescription);
                    }

                    if (styling.hasKey("displayTitle")) {
                        boolean displayTitle = styling.getBoolean("displayTitle");
                        configBuilder.displayTitle(displayTitle);
                    }

                    if (styling.hasKey("colors")) {
                        mColors = styling.getMap("colors");
                    }
                }
            }

            // Legacy
            if (prop.hasKey("advertising")) {
                ReadableMap ads = prop.getMap("advertising");
                AdvertisingConfig advertisingConfig = RNJWPlayerAds.getAdvertisingConfig(ads);
                if (advertisingConfig != null) {
                    configBuilder.advertisingConfig(advertisingConfig);
                }
            }

            // Legacy
            if (prop.hasKey("stretching")) {
                String stretching = prop.getString("stretching");
                configBuilder.stretching(stretching);
            }

            // Legacy
            // this isn't the ideal way to do controls...
            // Better to just expose the `.setControls` method
            if (prop.hasKey("controls")) {
                boolean controls = prop.getBoolean("controls");
                if (!controls) {
                    UiConfig uiConfig = new UiConfig.Builder().hideAllControls().build();
                    configBuilder.uiConfig(uiConfig);
                }
            }

            // Legacy
            if (prop.hasKey("hideUIGroups")) {
                ReadableArray uiGroupsArray = prop.getArray("hideUIGroups");
                UiConfig.Builder hideConfigBuilder = new UiConfig.Builder().displayAllControls();
                for (int i = 0; i < uiGroupsArray.size(); i++) {
                    if (uiGroupsArray.getType(i) == ReadableType.String) {
                        UiGroup uiGroup = GROUP_TYPES.get(uiGroupsArray.getString(i));
                        if (uiGroup != null) {
                            hideConfigBuilder.hide(uiGroup);
                        }
                    }
                }
                UiConfig hideJwControlbarUiConfig = hideConfigBuilder.build();
                configBuilder.uiConfig(hideJwControlbarUiConfig);
            }
        }

        Context simpleContext = getNonBuggyContext(getReactContext(), getAppContext());

        this.destroyPlayer();

        mPlayerView = new RNJWPlayer(simpleContext);

        mPlayerView.setFocusable(true);
        mPlayerView.setFocusableInTouchMode(true);

        setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPlayerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        addView(mPlayerView);

        // Ensure we have a valid state before applying to the player
        registry.setCurrentState(registry.getCurrentState()); // This is a hack to ensure player and view know the lifecycle state

        mPlayer = mPlayerView.getPlayer(this);

        if (prop.hasKey("controls")) { // Hack for controls hiding not working right away
            mPlayerView.getPlayer().setControls(prop.getBoolean("controls"));
        }

        if (prop.hasKey("fullScreenOnLandscape")) {
            fullScreenOnLandscape = prop.getBoolean("fullScreenOnLandscape");
            mPlayerView.fullScreenOnLandscape = fullScreenOnLandscape;
        }

        if (prop.hasKey("landscapeOnFullScreen")) {
            landscapeOnFullScreen = prop.getBoolean("landscapeOnFullScreen");
        }

        if (prop.hasKey("portraitOnExitFullScreen")) {
            portraitOnExitFullScreen = prop.getBoolean("fullScreenOnLandscape");
        }

        if (prop.hasKey("playerInModal")) {
            playerInModal = prop.getBoolean("playerInModal");
        }

        if (prop.hasKey("exitFullScreenOnPortrait")) {
            exitFullScreenOnPortrait = prop.getBoolean("exitFullScreenOnPortrait");
            mPlayerView.exitFullScreenOnPortrait = exitFullScreenOnPortrait;
        }

        if (isJwConfig) {
            mPlayer.setup(jwConfig);
        } else {
            PlayerConfig playerConfig = configBuilder.build();
            mPlayer.setup(playerConfig);
        }

        if (mActivity != null && prop.hasKey("pipEnabled")) {
            boolean pipEnabled = prop.getBoolean("pipEnabled");
            if (pipEnabled) {
                registerReceiver();
                mPlayer.registerActivityForPip(mActivity, mActivity.getSupportActionBar());
            } else {
                mPlayer.deregisterActivityForPip();
                unRegisterReceiver();
            }
        }

        // Legacy
        // This isn't the ideal way to do this on Android. All drawables/colors/themes shoudld
        // be targed using styling. See `https://docs.jwplayer.com/players/docs/android-styling-guide`
        // for more information on how best to override the JWP styles using XML. If you are unsure of a
        // color/drawable/theme, open an `Ask` issue.
        if (mColors != null) {
            if (mColors.hasKey("backgroundColor")) {
                mPlayerView.setBackgroundColor(Color.parseColor("#" + mColors.getString("backgroundColor")));
            }

            if (mColors.hasKey("buttons")) {

            }

            if (mColors.hasKey("timeslider")) {
                CueMarkerSeekbar seekBar = findViewById(com.longtailvideo.jwplayer.R.id.controlbar_seekbar);
                ReadableMap timeslider = mColors.getMap("timeslider");
                if (timeslider != null) {
                    LayerDrawable progressDrawable = (LayerDrawable) seekBar.getProgressDrawable();

                    if (timeslider.hasKey("progress")) {
                        // seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#" +
                        // timeslider.getString("progress")), PorterDuff.Mode.SRC_IN);
                        Drawable processDrawable = progressDrawable.findDrawableByLayerId(android.R.id.progress);
                        processDrawable.setColorFilter(Color.parseColor("#" + timeslider.getString("progress")),
                                PorterDuff.Mode.SRC_IN);
                    }

                    if (timeslider.hasKey("buffer")) {
                        Drawable secondaryProgressDrawable = progressDrawable
                                .findDrawableByLayerId(android.R.id.secondaryProgress);
                        secondaryProgressDrawable.setColorFilter(Color.parseColor("#" + timeslider.getString("buffer")),
                                PorterDuff.Mode.SRC_IN);
                    }

                    if (timeslider.hasKey("rail")) {
                        Drawable backgroundDrawable = progressDrawable.findDrawableByLayerId(android.R.id.background);
                        backgroundDrawable.setColorFilter(Color.parseColor("#" + timeslider.getString("rail")),
                                PorterDuff.Mode.SRC_IN);
                    }

                    if (timeslider.hasKey("thumb")) {
                        seekBar.getThumb().setColorFilter(Color.parseColor("#" + timeslider.getString("thumb")),
                                PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        }

        // Needed to handle volume control
        audioManager = (AudioManager) simpleContext.getSystemService(Context.AUDIO_SERVICE);

        if (prop.hasKey("backgroundAudioEnabled")) {
            backgroundAudioEnabled = prop.getBoolean("backgroundAudioEnabled");
        }

        setupPlayerView(backgroundAudioEnabled, playlistItemCallbackEnabled);

        if (backgroundAudioEnabled) {
            audioManager = (AudioManager) simpleContext.getSystemService(Context.AUDIO_SERVICE);
            mMediaServiceController = new MediaServiceController.Builder((AppCompatActivity) mActivity, mPlayer)
                    .build();
        }
    }

    // Audio Focus

    public void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (hasAudioFocus) {
                return;
            }

            if (audioManager != null) {
                AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // CONTENT_TYPE_SPEECH
                        .build();
                focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(playbackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        // .setWillPauseWhenDucked(true)
                        .setOnAudioFocusChangeListener(this)
                        .build();

                int res = audioManager.requestAudioFocus(focusRequest);
                synchronized (focusLock) {
                    if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                        playbackNowAuthorized = false;
                    } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        playbackNowAuthorized = true;
                        hasAudioFocus = true;
                    } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                        playbackDelayed = true;
                        playbackNowAuthorized = false;
                    }
                }
                Log.e(TAG, "audioRequest: " + res);
            }
        } else {
            int result = 0;
            if (audioManager != null) {
                if (hasAudioFocus) {
                    return;
                }

                result = audioManager.requestAudioFocus(this,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN);
            }
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                hasAudioFocus = true;
            }
            Log.e(TAG, "audioRequest: " + result);
        }
    }


    public void lowerApiOnAudioFocus(int focusChange) {
        if (mPlayer != null) {
            int initVolume = mPlayer.getVolume();

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (!userPaused) {
                        setVolume(initVolume);

                        boolean autostart = mPlayer.getConfig().getAutostart();
                        if (autostart) {
                            mPlayer.play();
                        }
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mPlayer.pause();
                    wasInterrupted = true;
                    hasAudioFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mPlayer.pause();
                    wasInterrupted = true;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    setVolume(initVolume / 2);
                    break;
            }
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (mPlayer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int initVolume = mPlayer.getVolume();

                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (playbackDelayed || !userPaused) {
                            synchronized (focusLock) {
                                playbackDelayed = false;
                            }

                            setVolume(initVolume);

                            boolean autostart = mPlayer.getConfig().getAutostart();
                            if (autostart) {
                                mPlayer.play();
                            }
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        mPlayer.pause();
                        synchronized (focusLock) {
                            wasInterrupted = true;
                            playbackDelayed = false;
                        }
                        hasAudioFocus = false;
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        mPlayer.pause();
                        synchronized (focusLock) {
                            wasInterrupted = true;
                            playbackDelayed = false;
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        setVolume(initVolume / 2);
                        break;
                }
            } else {
                lowerApiOnAudioFocus(focusChange);
            }
        }
    }

    private void setVolume(int volume) {
        if (!mPlayer.getMute()) {
            mPlayer.setVolume(volume);
        }
    }

    private void updateWakeLock(boolean enable) {
        if (mWindow != null) {
            if (enable && !isInBackground) {
                mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                mWindow.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    // Ad events

    @Override
    public void onAdLoaded(AdLoadedEvent adLoadedEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adLoadedEvent));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdLoadedXml(AdLoadedXmlEvent adLoadedXmlEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adLoadedXmlEvent));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdPause(AdPauseEvent adPauseEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putString("reason", adPauseEvent.getAdPauseReason().toString());
        event.putInt("client", Util.getAdEventClientValue(adPauseEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypePause));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdPlay(AdPlayEvent adPlayEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putString("reason", adPlayEvent.getAdPlayReason().toString());
        event.putInt("client", Util.getAdEventClientValue(adPlayEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypePlay));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdBreakEnd(AdBreakEndEvent adBreakEndEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adBreakEndEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeAdBreakEnd));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdBreakStart(AdBreakStartEvent adBreakStartEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adBreakStartEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeAdBreakStart));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdBreakIgnored(AdBreakIgnoredEvent adBreakIgnoredEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adBreakIgnoredEvent));
        // missing type code
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdClick(AdClickEvent adClickEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adClickEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeClicked));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdCompanions(AdCompanionsEvent adCompanionsEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adCompanionsEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeCompanion));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdComplete(AdCompleteEvent adCompleteEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adCompleteEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeComplete));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlayerAdError");
        event.putInt("code", adErrorEvent.getCode());
        event.putInt("adErrorCode", adErrorEvent.getAdErrorCode());
        event.putString("error", adErrorEvent.getMessage());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topPlayerAdError", event);
    }

    @Override
    public void onAdWarning(AdWarningEvent adWarningEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlayerAdWarning");
        event.putInt("code", adWarningEvent.getCode());
        event.putInt("adErrorCode", adWarningEvent.getAdErrorCode());
        event.putString("warning", adWarningEvent.getMessage());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topPlayerAdWarning", event);
    }

    @Override
    public void onAdImpression(AdImpressionEvent adImpressionEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adImpressionEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeImpression));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdMeta(AdMetaEvent adMetaEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adMetaEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeMeta));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdRequest(AdRequestEvent adRequestEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adRequestEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeRequest));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdSchedule(AdScheduleEvent adScheduleEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adScheduleEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeSchedule));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdSkipped(AdSkippedEvent adSkippedEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adSkippedEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeSkipped));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdStarted(AdStartedEvent adStartedEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdEvent");
        event.putInt("client", Util.getAdEventClientValue(adStartedEvent));
        event.putInt("type", Util.getAdEventTypeValue(Util.AdEventType.JWAdEventTypeStarted));
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdEvent", event);
    }

    @Override
    public void onAdTime(AdTimeEvent adTimeEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAdTime");
        event.putDouble("position", adTimeEvent.getPosition());
        event.putDouble("duration", adTimeEvent.getDuration());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAdTime", event);
    }

    @Override
    public void onAdViewableImpression(AdViewableImpressionEvent adViewableImpressionEvent) {
        // send everything?
    }

    @Override
    public void onBeforeComplete(BeforeCompleteEvent beforeCompleteEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onBeforeComplete");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topBeforeComplete", event);

        updateWakeLock(false);
    }

    @Override
    public void onBeforePlay(BeforePlayEvent beforePlayEvent) {
        // Ideally done in onFirstFrame instead
        // if (backgroundAudioEnabled) {
        //     doBindService();
        // }

        WritableMap event = Arguments.createMap();
        event.putString("message", "onBeforePlay");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topBeforePlay", event);
    }

    // Audio Events

    @Override
    public void onAudioTracks(AudioTracksEvent audioTracksEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onAudioTracks");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topAudioTracks", event);
    }

    @Override
    public void onAudioTrackChanged(AudioTrackChangedEvent audioTrackChangedEvent) {

    }

    // Captions Events

    @Override
    public void onCaptionsChanged(CaptionsChangedEvent captionsChangedEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onCaptionsChanged");
        event.putInt("index", captionsChangedEvent.getCurrentTrack());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topCaptionsChanged", event);
    }

    @Override
    public void onCaptionsList(CaptionsListEvent captionsListEvent) {
        WritableMap event = Arguments.createMap();
        List<Caption> captionTrackList = captionsListEvent.getCaptions();
        WritableArray captionTracks = Arguments.createArray();
        if (captionTrackList != null) {
            for(int i = 0; i < captionTrackList.size(); i++) {
                WritableMap captionTrack = Arguments.createMap();
                Caption track = captionTrackList.get(i);
                captionTrack.putString("file", track.getFile());
                captionTrack.putString("label", track.getLabel());
                captionTrack.putBoolean("default", track.isDefault());
                captionTracks.pushMap(captionTrack);
            }
        }
        event.putString("message", "onCaptionsList");
        event.putInt("index", captionsListEvent.getCurrentCaptionIndex());
        event.putArray("tracks", captionTracks);
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topCaptionsList", event);

    }

    // Player Events

    @Override
    public void onBuffer(BufferEvent bufferEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onBuffer");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topBuffer", event);

        updateWakeLock(true);
    }

    @Override
    public void onComplete(CompleteEvent completeEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onComplete");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topComplete", event);

        updateWakeLock(false);
    }

    @Override
    public void onControlBarVisibilityChanged(ControlBarVisibilityEvent controlBarVisibilityEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onControlBarVisible");
        event.putBoolean("visible", controlBarVisibilityEvent.isVisible());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topControlBarVisible", event);

        updateWakeLock(true);
    }

    @Override
    public void onControls(ControlsEvent controlsEvent) {

    }

    @Override
    public void onDisplayClick(DisplayClickEvent displayClickEvent) {

    }

    @Override
    public void onError(ErrorEvent errorEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onError");
        Exception ex = errorEvent.getException();
        if (ex != null) {
            event.putString("error", ex.toString());
            event.putString("description", errorEvent.getMessage());
            event.putInt("errorCode", errorEvent.getErrorCode());
        }
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topPlayerError", event);

        updateWakeLock(false);
    }

    @Override
    public void onFirstFrame(FirstFrameEvent firstFrameEvent) {
        if (backgroundAudioEnabled) {
            doBindService();
            requestAudioFocus();
        }
        WritableMap onFirstFrame = Arguments.createMap();
        onFirstFrame.putString("message", "onLoaded");
        onFirstFrame.putDouble("loadTime", firstFrameEvent.getLoadTime());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topFirstFrame",
                onFirstFrame);
    }

    @Override
    public void onFullscreen(FullscreenEvent fullscreenEvent) {
        if (fullscreenEvent.getFullscreen()) {
            if (mPlayerView != null) {
                mPlayerView.requestFocus();
            }

            WritableMap eventExitFullscreen = Arguments.createMap();
            eventExitFullscreen.putString("message", "onFullscreen");
            getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                    getId(),
                    "topFullScreen",
                    eventExitFullscreen);
        } else {
            WritableMap eventExitFullscreen = Arguments.createMap();
            eventExitFullscreen.putString("message", "onFullscreenExit");
            getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                    getId(),
                    "topFullScreenExit",
                    eventExitFullscreen);
        }
    }

    @Override
    public void onIdle(IdleEvent idleEvent) {

    }

    @Override
    public void onPause(PauseEvent pauseEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPause");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topPause", event);

        updateWakeLock(false);

        if (!wasInterrupted) {
            userPaused = true;
        }
    }

    @Override
    public void onPlay(PlayEvent playEvent) {
        // Ideally done in onFirstFrame instead
        // if (backgroundAudioEnabled) {
        //     doBindService();
        //     requestAudioFocus();
        // }

        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlay");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topPlay", event);

        updateWakeLock(true);

        userPaused = false;
        wasInterrupted = false;
    }

    @Override
    public void onPlaylistComplete(PlaylistCompleteEvent playlistCompleteEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlaylistComplete");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topPlaylistComplete", event);

        updateWakeLock(false);
    }

    @Override
    public void onPlaylistItem(PlaylistItemEvent playlistItemEvent) {
        // Ideally done in onFirstFrame instead
        // if (backgroundAudioEnabled) {
        //     doBindService();
        // }

        currentPlayingIndex = playlistItemEvent.getIndex();

        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlaylistItem");
        event.putInt("index", playlistItemEvent.getIndex());
        Gson gson = new Gson();
        String json = gson.toJson(playlistItemEvent.getPlaylistItem());
        event.putString("playlistItem", json);
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topPlaylistItem", event);
    }

    @Override
    public void onPlaylist(PlaylistEvent playlistEvent) {

    }

    @Override
    public void onReady(ReadyEvent readyEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlayerReady");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topOnPlayerReady", event);

        updateWakeLock(true);
    }

    @Override
    public void onSeek(SeekEvent seekEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onSeek");
        event.putDouble("position", seekEvent.getPosition());
        event.putDouble("offset", seekEvent.getOffset());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topSeek", event);
    }

    @Override
    public void onSeeked(SeekedEvent seekedEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onSeeked");
        event.putDouble("position", seekedEvent.getPosition());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topSeeked", event);
    }

    @Override
    public void onPlaybackRateChanged(PlaybackRateChangedEvent playbackRateChangedEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onRateChanged");
        event.putDouble("rate", playbackRateChangedEvent.getPlaybackRate());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topRateChanged", event);
    }

    @Override
    public void onSetupError(SetupErrorEvent setupErrorEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onSetupError");
        event.putString("errorMessage", setupErrorEvent.getMessage());
        event.putInt("errorCode", setupErrorEvent.getCode());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topSetupPlayerError", event);

        updateWakeLock(false);
    }

    @Override
    public void onTime(TimeEvent timeEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onTime");
        event.putDouble("position", timeEvent.getPosition());
        event.putDouble("duration", timeEvent.getDuration());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topTime", event);
    }

    @Override
    public void onMeta(MetaEvent metaEvent) {

    }

    // Picture in Picture events

    @Override
    public void onPipClose(PipCloseEvent pipCloseEvent) {

    }

    @Override
    public void onPipOpen(PipOpenEvent pipOpenEvent) {

    }

    // Casting events

    private boolean mIsCastActive = false;

    /**
     * Get if this player-view is currently casting
     *
     * @return true if casting
     */
    public boolean getIsCastActive() {
        return mIsCastActive;
    }

    @Override
    public void onCast(CastEvent castEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onCasting");
        event.putString("device", castEvent.getDeviceName());
        event.putBoolean("active", castEvent.isActive());
        event.putBoolean("available", castEvent.isAvailable());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topCasting", event);
        mIsCastActive = castEvent.isActive();
        // stop/start the background audio service if it's running and we're casting
        if (castEvent.isActive()) {
            doUnbindService();
        } else {
            if (backgroundAudioEnabled) {
                mMediaServiceController = new MediaServiceController.Builder((AppCompatActivity) mActivity, mPlayer)
                        .build();
                doBindService();
            }
        }
    }

    // LifecycleEventListener

    @Override
    public void onHostResume() {
        sessionDepth++;
        if (sessionDepth == 1) {
            isInBackground = false;
        }
    }

    @Override
    public void onHostPause() {
        if (sessionDepth > 0)
            sessionDepth--;
        if (sessionDepth == 0) {
            isInBackground = true;
        }
    }

    @Override
    public void onHostDestroy() {
        this.destroyPlayer();
    }

    // utils
    private final Map<String, Integer> CLIENT_TYPES = MapBuilder.of(
            "vast", 0,
            "ima", 1,
            "ima_dai", 2);

    private final Map<String, UiGroup> GROUP_TYPES = ImmutableMap.<String, UiGroup>builder()
            .put("overlay", UiGroup.OVERLAY)
            .put("control_bar", UiGroup.CONTROLBAR)
            .put("center_controls", UiGroup.CENTER_CONTROLS)
            .put("next_up", UiGroup.NEXT_UP)
            .put("error", UiGroup.ERROR)
            .put("playlist", UiGroup.PLAYLIST)
            .put("controls_container", UiGroup.PLAYER_CONTROLS_CONTAINER)
            .put("settings_menu", UiGroup.SETTINGS_MENU)
            .put("quality_submenu", UiGroup.SETTINGS_QUALITY_SUBMENU)
            .put("captions_submenu", UiGroup.SETTINGS_CAPTIONS_SUBMENU)
            .put("playback_submenu", UiGroup.SETTINGS_PLAYBACK_SUBMENU)
            .put("audiotracks_submenu", UiGroup.SETTINGS_AUDIOTRACKS_SUBMENU)
            .put("casting_menu", UiGroup.CASTING_MENU).build();
}


