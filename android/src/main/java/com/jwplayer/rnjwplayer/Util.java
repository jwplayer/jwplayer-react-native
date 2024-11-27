package com.jwplayer.rnjwplayer;

import static androidx.media3.common.util.Util.toByteArray;

import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.jwplayer.pub.api.JsonHelper;
import com.jwplayer.pub.api.media.ads.AdBreak;
import com.jwplayer.pub.api.media.captions.Caption;
import com.jwplayer.pub.api.media.captions.CaptionType;
import com.jwplayer.pub.api.media.playlists.MediaSource;
import com.jwplayer.pub.api.media.playlists.PlaylistItem;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Util {

    public static byte[] executePost(String url, byte[] data, Map<String, String> requestProperties)
            throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(data != null);
            urlConnection.setDoInput(true);
            if (requestProperties != null) {
                for (Map.Entry<String, String> requestProperty : requestProperties.entrySet()) {
                    urlConnection.setRequestProperty(requestProperty.getKey(),
                            requestProperty.getValue());
                }
            }
            // Write the request body, if there is one.
            if (data != null) {
                OutputStream out = urlConnection.getOutputStream();
                try {
                    out.write(data);
                } finally {
                    out.close();
                }
            }
            // Read and return the response body.
            InputStream inputStream = urlConnection.getInputStream();
            try {
                return toByteArray(inputStream);
            } finally {
                inputStream.close();
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static boolean isValidURL(String url) {
        return URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches();
    }

    public static List<PlaylistItem> createPlaylist(ReadableArray playlistItems) {
        List<PlaylistItem> playlist = new ArrayList<>();
        if (playlistItems == null || playlistItems.size() <= 0)
            return playlist;

        int j = 0;
        while (playlistItems.size() > j) {
            ReadableMap playlistItem = playlistItems.getMap(j);

            JSONObject obj;
            PlaylistItem item = null;
            // Try since legacy config may or may not conform to this standard
            try {
                obj = MapUtil.toJSONObject(playlistItem);
                item = JsonHelper.parsePlaylistItemJson(obj);
            } catch (Exception ex) {
                Log.e("createPlaylist", ex.toString());
            }
            if (item != null) {
                playlist.add(item);
            } else {
                // Try to use the legacy format
                PlaylistItem newPlayListItem = getPlaylistItem((playlistItem));
                playlist.add(newPlayListItem);
            }
            j++;
        }
        return playlist;
    }

    public static PlaylistItem getPlaylistItem(ReadableMap playlistItem) {
        PlaylistItem.Builder itemBuilder = new PlaylistItem.Builder();

        if (playlistItem.hasKey("file")) {
            String file = playlistItem.getString("file");
            itemBuilder.file(file);
        }

        if (playlistItem.hasKey("sources")) {
            ArrayList<MediaSource> sources = new ArrayList<>();
            ReadableArray sourcesArray = playlistItem.getArray("sources");
            if (sourcesArray != null) {
                for (int i = 0; i < sourcesArray.size(); i++) {
                    ReadableMap sourceProp = sourcesArray.getMap(i);
                    if (sourceProp.hasKey("file")) {
                        String file = sourceProp.getString("file");
                        String label = sourceProp.getString("label");
                        boolean isDefault = sourceProp.hasKey("default") ? sourceProp.getBoolean("default") : false;
                        MediaSource source = new MediaSource.Builder().file(file).label(label).isDefault(isDefault).build();
                        sources.add(source);
                    }
                }
            }

            itemBuilder.sources(sources);
        }

        if (playlistItem.hasKey("title")) {
            String title = playlistItem.getString("title");
            itemBuilder.title(title);
        }

        if (playlistItem.hasKey("description")) {
            String desc = playlistItem.getString("description");
            itemBuilder.description(desc);
        }

        if (playlistItem.hasKey("image")) {
            String image = playlistItem.getString("image");
            itemBuilder.image(image);
        }

        if (playlistItem.hasKey("mediaId")) {
            String mediaId = playlistItem.getString("mediaId");
            itemBuilder.mediaId(mediaId);
        }

        if (playlistItem.hasKey("startTime")) {
            double startTime = playlistItem.getDouble("startTime");
            itemBuilder.startTime(startTime);
        }

        if (playlistItem.hasKey("duration")) {
            int duration = playlistItem.getInt("duration");
            itemBuilder.duration(duration);
        }

        if (playlistItem.hasKey("tracks")) {
            ArrayList<Caption> tracks = new ArrayList<>();
            ReadableArray track = playlistItem.getArray("tracks");
            if (track != null) {
                for (int i = 0; i < track.size(); i++) {
                    ReadableMap trackProp = track.getMap(i);
                    if (trackProp.hasKey("file")) {
                        String file = trackProp.getString("file");
                        String label = trackProp.getString("label");
                        CaptionType kind = CaptionType.CAPTIONS;
                        // Being safe to old implementations though this should be required. 
                        if (trackProp.hasKey("kind")) {
                            kind = getCaptionType(trackProp.getString("kind").toUpperCase(Locale.US));
                        }
                        boolean isDefault = trackProp.hasKey("default") ? trackProp.getBoolean("default") : false;
                        Caption caption = new Caption.Builder().file(file).label(label).kind(kind).isDefault(isDefault).build();
                        tracks.add(caption);
                    }
                }
            }

            itemBuilder.tracks(tracks);
        }

        if (playlistItem.hasKey("authUrl")) {
            itemBuilder.mediaDrmCallback(new WidevineCallback(playlistItem.getString("authUrl")));
        }

        if (playlistItem.hasKey("adSchedule")) {
            ArrayList<AdBreak> adSchedule = new ArrayList<>();
            ReadableArray ad = playlistItem.getArray("adSchedule");

            for (int i = 0; i < ad.size(); i++) {
                ReadableMap adBreakProp = ad.getMap(i);
                String offset = adBreakProp.hasKey("offset") ? adBreakProp.getString("offset") : "pre";
                if (adBreakProp.hasKey("tag")) {
                    AdBreak adBreak = new AdBreak.Builder().offset(offset).tag(adBreakProp.getString("tag")).build();
                    adSchedule.add(adBreak);
                }
            }

            itemBuilder.adSchedule(adSchedule);
        }

        String recommendations;
        if (playlistItem.hasKey("recommendations")) {
            recommendations = playlistItem.getString("recommendations");
            itemBuilder.recommendations(recommendations);
        }

        return itemBuilder.build();
    }

    /**
     * Internal helper for parsing a caption type from a known string
     *
     * @param type one of "CAPTIONS", "CHAPTERS", "THUMBNAILS"
     * @return the correct Enum CaptionType
     */
    public static CaptionType getCaptionType(String type) {
        for (CaptionType captionType : CaptionType.values()) {
            if (captionType.name().equals(type)) {
                return CaptionType.valueOf(type);
            }
        }
        // This should never happen. If a null is returned, expect a crash. Check `type` is specified type
        return null;
    }

    public enum AdEventType {
        JWAdEventTypeAdBreakEnd(0),
        JWAdEventTypeAdBreakStart(1),
        JWAdEventTypeClicked(2),
        JWAdEventTypeComplete(3),
        JWAdEventTypeImpression(4),
        JWAdEventTypeMeta(5),
        JWAdEventTypePause(6),
        JWAdEventTypePlay(7),
        JWAdEventTypeRequest(8),
        JWAdEventTypeSchedule(9),
        JWAdEventTypeSkipped(10),
        JWAdEventTypeStarted(11),
        JWAdEventTypeCompanion(12);

        private final int value;

        AdEventType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Method to get the event type value
    public static int getEventTypeValue(AdEventType eventType) {
        return eventType.getValue();
    }
}