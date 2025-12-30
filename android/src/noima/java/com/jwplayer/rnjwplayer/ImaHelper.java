package com.jwplayer.rnjwplayer;

import com.facebook.react.bridge.ReadableMap;
import com.jwplayer.pub.api.configuration.ads.AdvertisingConfig;
import com.jwplayer.pub.api.media.ads.AdBreak;

import java.util.List;

/**
 * Stub implementation when IMA is disabled.
 * Provides clear error messages for users attempting to use IMA without enabling it.
 */
public class ImaHelper {
    
    public static AdvertisingConfig configureImaOrDai(ReadableMap ads, List<AdBreak> adSchedule) {
        // Note: adSchedule parameter is unused in stub - we always throw before using it
        // Passing a valid adSchedule would cause a runtime exception if Google IMA is not enabled
        throw new RuntimeException(
            "Google IMA is not enabled. " +
            "To use IMA ads, add 'RNJWPlayerUseGoogleIMA = true' to your app/build.gradle ext {} block."
        );
    }
}

