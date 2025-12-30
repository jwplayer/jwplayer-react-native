package com.jwplayer.rnjwplayer;

import com.facebook.react.bridge.ReadableMap;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.jwplayer.pub.api.configuration.ads.AdvertisingConfig;
import com.jwplayer.pub.api.configuration.ads.dai.ImaDaiAdvertisingConfig;
import com.jwplayer.pub.api.configuration.ads.ima.ImaAdvertisingConfig;
import com.jwplayer.pub.api.media.ads.AdBreak;
import com.jwplayer.pub.api.media.ads.dai.ImaDaiSettings;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * IMA-specific advertising configuration helper.
 * This class is only compiled when RNJWPlayerUseGoogleIMA = true.
 */
public class ImaHelper {
    
    public static AdvertisingConfig configureImaOrDai(ReadableMap ads, List<AdBreak> adSchedule) {
        cd /Users/jmilham/source/sdk/react/jwplayer-react-native/Example && yarn remove @jwplayer/jwplayer-react-native && yarn add file:../ 2>&1 | tail -20        // Check both "client" (JWPlayer JSON format) and "adClient" (RN wrapper format)
        String adClientType = null;
        if (ads.hasKey("adClient")) {
            adClientType = ads.getString("adClient");
        } else if (ads.hasKey("client")) {
            adClientType = ads.getString("client");
        }
        
        if (adClientType != null) {
            adClientType = adClientType.toLowerCase();
        }
        
        if ("ima".equals(adClientType) || "googima".equals(adClientType)) {
            return configureImaAdvertising(ads, adSchedule);
        } else if ("ima_dai".equals(adClientType)) {
            return configureImaDaiAdvertising(ads);
        }
        
        // Should never reach here due to validation in RNJWPlayerAds
        throw new RuntimeException("Unknown IMA ad client type: " + adClientType);
    }
    
    private static ImaAdvertisingConfig configureImaAdvertising(ReadableMap ads, List<AdBreak> adSchedule) {
        ImaAdvertisingConfig.Builder builder = new ImaAdvertisingConfig.Builder();
        builder.schedule(adSchedule);
        
        if (ads.hasKey("imaSettings")) {
            builder.imaSdkSettings(getImaSettings(Objects.requireNonNull(ads.getMap("imaSettings"))));
        }
        
        return builder.build();
    }
    
    private static ImaDaiAdvertisingConfig configureImaDaiAdvertising(ReadableMap ads) {
        ImaDaiAdvertisingConfig.Builder builder = new ImaDaiAdvertisingConfig.Builder();
        
        if (ads.hasKey("imaSettings")) {
            builder.imaSdkSettings(getImaSettings(Objects.requireNonNull(ads.getMap("imaSettings"))));
        }
        
        if (ads.hasKey("imaDaiSettings")) {
            builder.imaDaiSettings(getImaDaiSettings(Objects.requireNonNull(ads.getMap("imaDaiSettings"))));
        }
        
        return builder.build();
    }
    
    private static ImaDaiSettings getImaDaiSettings(ReadableMap imaDaiSettingsMap) {
        String videoId = imaDaiSettingsMap.hasKey("videoId") ? imaDaiSettingsMap.getString("videoId") : null;
        String cmsId = imaDaiSettingsMap.hasKey("cmsId") ? imaDaiSettingsMap.getString("cmsId") : null;
        String assetKey = imaDaiSettingsMap.hasKey("assetKey") ? imaDaiSettingsMap.getString("assetKey") : null;
        String apiKey = imaDaiSettingsMap.hasKey("apiKey") ? imaDaiSettingsMap.getString("apiKey") : null;

        // Validate we have either assetKey OR (videoId + cmsId)
        if (assetKey == null && (videoId == null || cmsId == null)) {
            throw new IllegalArgumentException(
                "ImaDaiSettings requires either 'assetKey' OR both 'videoId' and 'cmsId'. " +
                "Provided: assetKey=" + assetKey + ", videoId=" + videoId + ", cmsId=" + cmsId
            );
        }

        Map<String, String> adTagParameters = null;
        if (imaDaiSettingsMap.hasKey("adTagParameters") && imaDaiSettingsMap.getMap("adTagParameters") != null) {
            adTagParameters = new HashMap<>();
            ReadableMap adTagParamsMap = imaDaiSettingsMap.getMap("adTagParameters");
            for (Map.Entry<String, Object> entry : adTagParamsMap.toHashMap().entrySet()) {
                if (entry.getValue() instanceof String) {
                    adTagParameters.put(entry.getKey(), (String) entry.getValue());
                }
            }
        }

        ImaDaiSettings.StreamType streamType = ImaDaiSettings.StreamType.HLS;
        if (imaDaiSettingsMap.hasKey("streamType")) {
            String streamTypeStr = imaDaiSettingsMap.getString("streamType");
            if ("DASH".equalsIgnoreCase(streamTypeStr)) {
                streamType = ImaDaiSettings.StreamType.DASH;
            }
        }
        
        ImaDaiSettings imaDaiSettings = (assetKey != null) ?
                new ImaDaiSettings(assetKey, streamType, apiKey) :
                new ImaDaiSettings(videoId, cmsId, streamType, apiKey);

        if (adTagParameters != null) {
            imaDaiSettings.setAdTagParameters(adTagParameters);
        }

        return imaDaiSettings;
    }
    
    public static ImaSdkSettings getImaSettings(ReadableMap imaSettingsMap) {
        ImaSdkSettings settings = ImaSdkFactory.getInstance().createImaSdkSettings();

        if (imaSettingsMap.hasKey("maxRedirects")) {
            settings.setMaxRedirects(imaSettingsMap.getInt("maxRedirects"));
        }
        if (imaSettingsMap.hasKey("language")) {
            settings.setLanguage(imaSettingsMap.getString("language"));
        }
        if (imaSettingsMap.hasKey("ppid")) {
            settings.setPpid(imaSettingsMap.getString("ppid"));
        }
        if (imaSettingsMap.hasKey("playerType")) {
            settings.setPlayerType(imaSettingsMap.getString("playerType"));
        }
        if (imaSettingsMap.hasKey("playerVersion")) {
            settings.setPlayerVersion(imaSettingsMap.getString("playerVersion"));
        }
        if (imaSettingsMap.hasKey("sessionId")) {
            settings.setSessionId(imaSettingsMap.getString("sessionId"));
        }
        if (imaSettingsMap.hasKey("debugMode")) {
            settings.setDebugMode(imaSettingsMap.getBoolean("debugMode"));
        }

        return settings;
    }
}

