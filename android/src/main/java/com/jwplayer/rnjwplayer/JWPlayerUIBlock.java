package com.jwplayer.rnjwplayer;

import androidx.annotation.OptIn;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.fabric.FabricUIManager;
import com.facebook.react.fabric.interop.UIBlockViewResolver;
import com.facebook.react.uimanager.common.UIManagerType;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.common.annotations.UnstableReactNativeAPI;

import java.util.function.Function;

public class JWPlayerUIBlock implements UIBlockInterface {
    private int tag;
    private Promise promise;
    private ReactApplicationContext context;
    private Function<RNJWPlayerView, Void> playerOperation;

    public JWPlayerUIBlock(int tag, Promise promise, ReactApplicationContext context, Function<RNJWPlayerView, Void> playerOperation) {
        this.tag = tag;
        this.promise = promise;
        this.context = context;
        this.playerOperation = playerOperation;
    }

    @Override
    public void execute(NativeViewHierarchyManager nvhm) {
        executeImpl(nvhm, null);
    }

    @Override
    @OptIn(markerClass = UnstableReactNativeAPI.class)
    public void execute(UIBlockViewResolver uiBlockViewResolver) {
        executeImpl(null, uiBlockViewResolver);
    }

    @OptIn(markerClass = UnstableReactNativeAPI.class)
    private void executeImpl(NativeViewHierarchyManager nvhm, UIBlockViewResolver uiBlockViewResolver) {
        RNJWPlayerView playerView = uiBlockViewResolver != null 
            ? (RNJWPlayerView) uiBlockViewResolver.resolveView(tag) 
            : (RNJWPlayerView) nvhm.resolveView(tag);
            
        if (playerView == null) {
            promise.reject("RNJWPlayerView not found");
            return;
        }
        if (playerView.mPlayerView == null) {
            promise.reject("RNJWPlayerView.mPlayerView is not valid");
            return;
        }

        playerOperation.apply(playerView);
    }

    public void addToUIManager() {
        if (DefaultNewArchitectureEntryPoint.getFabricEnabled()) {
            UIManager uiManager = UIManagerHelper.getUIManager(context, UIManagerType.FABRIC);
            assert uiManager != null;
            ((FabricUIManager) uiManager).addUIBlock(this);
        } else {
            UIManagerModule uiManager = context.getNativeModule(UIManagerModule.class);
            assert uiManager != null;
            uiManager.addUIBlock(this);
        }
    }
}

@OptIn(markerClass = UnstableReactNativeAPI.class)
interface UIBlockInterface extends UIBlock, com.facebook.react.fabric.interop.UIBlock  {}
