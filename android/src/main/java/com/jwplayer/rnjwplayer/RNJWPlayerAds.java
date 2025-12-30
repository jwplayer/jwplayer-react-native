package com.jwplayer.rnjwplayer;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.jwplayer.pub.api.configuration.ads.AdRules;
import com.jwplayer.pub.api.configuration.ads.AdvertisingConfig;
import com.jwplayer.pub.api.configuration.ads.VastAdvertisingConfig;
import com.jwplayer.pub.api.media.ads.AdBreak;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RNJWPlayerAds {

    // Get advertising config based on the ad client type
    public static AdvertisingConfig getAdvertisingConfig(ReadableMap ads) {
        if (ads == null) {
            return null;
        }

        // Check both "client" (JWPlayer JSON format) and "adClient" (RN wrapper format)
        String adClientType = null;
        if (ads.hasKey("adClient")) {
            adClientType = ads.getString("adClient");
        } else if (ads.hasKey("client")) {
            adClientType = ads.getString("client");
        }
        
        // Validate client type exists and is not null
        if (adClientType == null) {
            throw new IllegalArgumentException("Missing required 'adClient' or 'client' field in advertising config");
        }
        
        // Normalize to lowercase for case-insensitive matching
        adClientType = adClientType.toLowerCase();

        switch (adClientType) {
            case "ima":
            case "googima":  // Support legacy "googima" format
            case "ima_dai":
                // Delegate to ImaHelper (implementation selected by Gradle)
                // Note: Only parse adSchedule for regular IMA, not for DAI (ads are embedded in stream)
                List<AdBreak> adSchedule = ("ima".equals(adClientType) || "googima".equals(adClientType)) 
                    ? getAdSchedule(ads) 
                    : new ArrayList<>();
                try {
                    return ImaHelper.configureImaOrDai(ads, adSchedule);
                } catch (RuntimeException e) {
                    // IMA not enabled - log error and return null (graceful degradation)
                    android.util.Log.e("RNJWPlayerAds", "Failed to configure IMA ads: " + e.getMessage());
                    return null;
                }
            default: // Defaulting to VAST
                return configureVastAdvertising(ads);
        }
    }

    // Configure VAST Advertising
    private static VastAdvertisingConfig configureVastAdvertising(ReadableMap ads) {
        VastAdvertisingConfig.Builder builder = new VastAdvertisingConfig.Builder();

        List<AdBreak> adScheduleList = getAdSchedule(ads);
        builder.schedule(adScheduleList);

        if (ads.hasKey("skipText")) {
            builder.skipText(ads.getString("skipText"));
        }
        if (ads.hasKey("skipMessage")) {
            builder.skipMessage(ads.getString("skipMessage"));
        }
        // ... Add other VAST specific settings from ads ReadableMap

        // Example: Handling VPAID controls
        if (ads.hasKey("vpaidControls")) {
            builder.vpaidControls(ads.getBoolean("vpaidControls"));
        }

        if (ads.hasKey("adRules")) {
            AdRules adRules = getAdRules(Objects.requireNonNull(ads.getMap("adRules")));
            builder.adRules(adRules);
        }

        return builder.build();
    }

    private static List<AdBreak> getAdSchedule(ReadableMap ads) {
        List<AdBreak> adScheduleList = new ArrayList<>();
        
        // Check if adSchedule exists
        if (!ads.hasKey("adSchedule")) {
            return adScheduleList; // Return empty list
        }
        
        ReadableArray adSchedule = ads.getArray("adSchedule");
        if (adSchedule == null) {
            return adScheduleList; // Return empty list if null
        }
        
        for (int i = 0; i < adSchedule.size(); i++) {
            ReadableMap adBreakProp = adSchedule.getMap(i);
            // Skip null entries in the adSchedule array
            if (adBreakProp == null) {
                continue;
            }
            String offset = adBreakProp.hasKey("offset") ? adBreakProp.getString("offset") : "pre";
            if (adBreakProp.hasKey("tag")) {
                AdBreak adBreak = new AdBreak.Builder()
                        .offset(offset)
                        .tag(adBreakProp.getString("tag"))
                        .build();
                adScheduleList.add(adBreak);
            }
        }
        return adScheduleList;
    }

    public static AdRules getAdRules(ReadableMap adRulesMap) {
        AdRules.Builder builder = new AdRules.Builder();

        if (adRulesMap.hasKey("startOn")) {
            Integer startOn = adRulesMap.getInt("startOn");
            builder.startOn(startOn);
        }
        if (adRulesMap.hasKey("frequency")) {
            Integer frequency = adRulesMap.getInt("frequency");
            builder.frequency(frequency);
        }
        if (adRulesMap.hasKey("timeBetweenAds")) {
            Integer timeBetweenAds = adRulesMap.getInt("timeBetweenAds");
            builder.timeBetweenAds(timeBetweenAds);
        }
        if (adRulesMap.hasKey("startOnSeek")) {
            String startOnSeek = adRulesMap.getString("startOnSeek");
            // Mapping the string to the corresponding constant in AdRules
            String mappedStartOnSeek = mapStartOnSeek(startOnSeek);
            builder.startOnSeek(mappedStartOnSeek);
        }

        return builder.build();
    }

    private static String mapStartOnSeek(String startOnSeek) {
        if ("pre".equals(startOnSeek)) {
            return AdRules.RULES_START_ON_SEEK_PRE;
        }
        // Default to "none" if not "pre"
        return AdRules.RULES_START_ON_SEEK_NONE;
    }

}
