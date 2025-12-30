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

        String adClientType = ads.getString("adClient");
        switch (adClientType) {
            case "ima":
            case "ima_dai":
                // Delegate to ImaHelper (implementation selected by Gradle)
                List<AdBreak> adSchedule = getAdSchedule(ads);
                return ImaHelper.configureImaOrDai(ads, adSchedule);
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
        ReadableArray adSchedule = ads.getArray("adSchedule");
        for (int i = 0; i < adSchedule.size(); i++) {
            ReadableMap adBreakProp = adSchedule.getMap(i);
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
