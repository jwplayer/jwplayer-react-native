package com.jwplayer.rnjwplayer;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.jwplayer.pub.api.configuration.ads.AdvertisingConfig;
import com.jwplayer.pub.api.configuration.ads.CnxAdvertisingConfig;
import com.jwplayer.pub.api.configuration.ads.CnxAuctionConfig;
import com.jwplayer.pub.api.configuration.ads.CnxTargetingConfig;
import com.jwplayer.pub.api.media.ads.AdBreak;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * CNX (Connatix) advertising configuration helper.
 * Converts a React Native ReadableMap into a CnxAdvertisingConfig.
 */
public class CnxHelper {

    public static AdvertisingConfig configureCnx(ReadableMap ads) {
        // Required fields: adUnitId and customerId (accept companyId as alias)
        String adUnitId = getStringField(ads, "adUnitId");
        if (adUnitId == null) {
            throw new IllegalArgumentException("CNX advertising config requires 'adUnitId'");
        }

        String customerId = getStringField(ads, "customerId");
        if (customerId == null) {
            customerId = getStringField(ads, "companyId");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CNX advertising config requires 'customerId' or 'companyId'");
        }

        CnxAdvertisingConfig.Builder builder = new CnxAdvertisingConfig.Builder()
                .adUnitId(adUnitId)
                .customerId(customerId);

        // General settings
        if (ads.hasKey("debug")) {
            builder.debug(ads.getBoolean("debug"));
        }
        if (ads.hasKey("viewabilityPolicy")) {
            builder.viewabilityPolicy(ads.getString("viewabilityPolicy"));
        }
        if (ads.hasKey("locale")) {
            builder.locale(ads.getString("locale"));
        }
        if (ads.hasKey("ppid")) {
            builder.ppid(ads.getString("ppid"));
        }
        if (ads.hasKey("requestTimeout")) {
            builder.requestTimeout(ads.getInt("requestTimeout"));
        }
        if (ads.hasKey("plcmt")) {
            builder.plcmt(ads.getInt("plcmt"));
        }
        if (ads.hasKey("maxAdDuration")) {
            builder.maxAdDuration(ads.getDouble("maxAdDuration"));
        }
        if (ads.hasKey("enableVerticalAds")) {
            builder.enableVerticalAds(ads.getBoolean("enableVerticalAds"));
        }
        if (ads.hasKey("playbackMethod")) {
            builder.playbackmethod(ads.getInt("playbackMethod"));
        }
        if (ads.hasKey("autoplayAdsMuted")) {
            builder.autoplayAdsMuted(ads.getBoolean("autoplayAdsMuted"));
        }
        if (ads.hasKey("outstream")) {
            builder.outstream(ads.getBoolean("outstream"));
        }
        if (ads.hasKey("enablePPS")) {
            builder.enablePPS(ads.getBoolean("enablePPS"));
        }
        if (ads.hasKey("strategyOutcomeId")) {
            builder.strategyOutcomeId(ads.getString("strategyOutcomeId"));
        }
        if (ads.hasKey("cnxEnv")) {
            builder.cnxEnv(ads.getString("cnxEnv"));
        }
        if (ads.hasKey("useExternalAdService")) {
            builder.useExternalAdService(ads.getBoolean("useExternalAdService"));
        }
        if (ads.hasKey("enableAdDurationGuards")) {
            builder.enableAdDurationGuards(ads.getBoolean("enableAdDurationGuards"));
        }

        // Skip settings
        if (ads.hasKey("skipEnabled")) {
            builder.skip(ads.getBoolean("skipEnabled"));
        }
        if (ads.hasKey("skipMinDuration")) {
            builder.skipmin(ads.getInt("skipMinDuration"));
        }
        if (ads.hasKey("cnxSkipOffset")) {
            builder.cnxSkipOffset(ads.getInt("cnxSkipOffset"));
        }

        // VAST-inherited settings (from AdvertisingWithVastCustomizations.Builder)
        if (ads.hasKey("skipoffset")) {
            builder.skipOffset(ads.getInt("skipoffset"));
        } else if (ads.hasKey("skipOffset")) {
            builder.skipOffset(ads.getInt("skipOffset"));
        }
        if (ads.hasKey("skiptext") || ads.hasKey("skipText")) {
            builder.skipText(getStringField(ads, "skiptext", "skipText"));
        }
        if (ads.hasKey("skipmessage") || ads.hasKey("skipMessage")) {
            builder.skipMessage(getStringField(ads, "skipmessage", "skipMessage"));
        }
        if (ads.hasKey("admessage") || ads.hasKey("adMessage")) {
            builder.adMessage(getStringField(ads, "admessage", "adMessage"));
        }
        if (ads.hasKey("vpaidcontrols") || ads.hasKey("vpaidControls")) {
            boolean val = ads.hasKey("vpaidcontrols") ? ads.getBoolean("vpaidcontrols") : ads.getBoolean("vpaidControls");
            builder.vpaidControls(val);
        }

        // Extra params
        if (ads.hasKey("extraParams") && ads.getMap("extraParams") != null) {
            builder.extraParams(toObjectMap(ads.getMap("extraParams")));
        }

        // App metadata
        if (ads.hasKey("appPageURL")) {
            builder.appPageURL(ads.getString("appPageURL"));
        }
        if (ads.hasKey("domainURL")) {
            builder.domainURL(ads.getString("domainURL"));
        }
        if (ads.hasKey("storeURL")) {
            builder.storeURL(ads.getString("storeURL"));
        }
        if (ads.hasKey("hasPrivacyPolicy")) {
            builder.hasPrivacyPolicy(ads.getBoolean("hasPrivacyPolicy"));
        }
        if (ads.hasKey("isPaid")) {
            builder.isPaid(ads.getBoolean("isPaid"));
        }
        if (ads.hasKey("useContentOnlyDomain")) {
            builder.useContentOnlyDomain(ads.getBoolean("useContentOnlyDomain"));
        }
        if (ads.hasKey("appCategories") && ads.getArray("appCategories") != null) {
            builder.appCategories(toStringList(ads.getArray("appCategories")));
        }

        // Targeting
        CnxTargetingConfig targeting = buildTargeting(ads);
        if (targeting != null) {
            builder.targeting(targeting);
        }

        // Auction
        if (ads.hasKey("auction") && ads.getMap("auction") != null) {
            CnxAuctionConfig auction = buildAuction(ads.getMap("auction"));
            if (auction != null) {
                builder.auction(auction);
            }
        }

        // Dynamic ads (pass as raw JSONObject)
        if (ads.hasKey("dynamicAds") && ads.getMap("dynamicAds") != null) {
            try {
                JSONObject dynamicAds = toJSONObject(ads.getMap("dynamicAds"));
                builder.dynamicAds(dynamicAds);
            } catch (JSONException e) {
                android.util.Log.w("CnxHelper", "Failed to parse dynamicAds: " + e.getMessage());
            }
        }

        // Ad schedule
        List<AdBreak> schedule = RNJWPlayerAds.getAdSchedule(ads);
        if (!schedule.isEmpty()) {
            builder.schedule(schedule);
        }

        // Ad rules
        if (ads.hasKey("adRules") && ads.getMap("adRules") != null) {
            builder.adRules(RNJWPlayerAds.getAdRules(ads.getMap("adRules")));
        }

        return builder.build();
    }

    private static CnxTargetingConfig buildTargeting(ReadableMap ads) {
        boolean hasTargeting = ads.hasKey("targetingPlacementId")
                || ads.hasKey("customMacros")
                || ads.hasKey("queryJSParams");
        if (!hasTargeting) {
            return null;
        }

        CnxTargetingConfig.Builder builder = new CnxTargetingConfig.Builder();
        if (ads.hasKey("targetingPlacementId")) {
            builder.placementId(ads.getString("targetingPlacementId"));
        }
        if (ads.hasKey("customMacros") && ads.getMap("customMacros") != null) {
            builder.customMacros(toObjectMap(ads.getMap("customMacros")));
        }
        if (ads.hasKey("queryJSParams") && ads.getMap("queryJSParams") != null) {
            builder.queryJSParams(toObjectMap(ads.getMap("queryJSParams")));
        }
        return builder.build();
    }

    private static CnxAuctionConfig buildAuction(@NonNull ReadableMap auctionMap) {
        CnxAuctionConfig.Builder builder = new CnxAuctionConfig.Builder();
        if (auctionMap.hasKey("pre") && auctionMap.getMap("pre") != null) {
            builder.pre(buildAuctionBreak(auctionMap.getMap("pre")));
        }
        if (auctionMap.hasKey("mid") && auctionMap.getMap("mid") != null) {
            builder.mid(buildAuctionBreak(auctionMap.getMap("mid")));
        }
        if (auctionMap.hasKey("post") && auctionMap.getMap("post") != null) {
            builder.post(buildAuctionBreak(auctionMap.getMap("post")));
        }
        return builder.build();
    }

    private static CnxAuctionConfig.AuctionBreakConfig buildAuctionBreak(@NonNull ReadableMap breakMap) {
        CnxAuctionConfig.AuctionBreakConfig.Builder builder = new CnxAuctionConfig.AuctionBreakConfig.Builder();
        if (breakMap.hasKey("auctionType")) {
            builder.auctionType(breakMap.getString("auctionType"));
        }
        if (breakMap.hasKey("timeoutMs")) {
            builder.timeoutMs(breakMap.getInt("timeoutMs"));
        }
        return builder.build();
    }

    // --- Utility methods ---

    private static String getStringField(ReadableMap map, String... keys) {
        for (String key : keys) {
            if (map.hasKey(key)) {
                return map.getString(key);
            }
        }
        return null;
    }

    private static Map<String, Object> toObjectMap(ReadableMap readableMap) {
        if (readableMap == null) return null;
        return new HashMap<>(readableMap.toHashMap());
    }

    private static List<String> toStringList(ReadableArray array) {
        if (array == null) return null;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }

    private static JSONObject toJSONObject(ReadableMap readableMap) throws JSONException {
        JSONObject json = new JSONObject();
        HashMap<String, Object> hashMap = readableMap.toHashMap();
        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            json.put(entry.getKey(), JSONObject.wrap(entry.getValue()));
        }
        return json;
    }

    // Make getAdSchedule accessible (delegates to RNJWPlayerAds)
    static List<AdBreak> getAdSchedule(ReadableMap ads) {
        return RNJWPlayerAds.getAdSchedule(ads);
    }
}
