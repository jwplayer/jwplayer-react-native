package com.jwplayer.rnjwplayer;

import android.util.Base64;

import androidx.media3.common.C;
import androidx.media3.common.ParserException;
import androidx.media3.extractor.metadata.emsg.EventMessage;
import androidx.media3.extractor.metadata.id3.ApicFrame;
import androidx.media3.extractor.metadata.id3.BinaryFrame;
import androidx.media3.extractor.metadata.id3.CommentFrame;
import androidx.media3.extractor.metadata.id3.GeobFrame;
import androidx.media3.extractor.metadata.id3.Id3Frame;
import androidx.media3.extractor.metadata.id3.InternalFrame;
import androidx.media3.extractor.metadata.id3.PrivFrame;
import androidx.media3.extractor.metadata.id3.TextInformationFrame;
import androidx.media3.extractor.metadata.id3.UrlLinkFrame;

import com.facebook.react.bridge.WritableMap;
import com.jwplayer.pub.api.events.DateRangeEvent;
import com.jwplayer.pub.api.events.InPlaylistTimedMetadataEvent;
import com.jwplayer.pub.api.events.MetaEvent;
import com.jwplayer.pub.api.events.MetadataCueParsedEvent;
import com.jwplayer.pub.api.events.ProgramDateTimeEvent;
import com.jwplayer.pub.api.media.meta.DateRangeMetadataCue;
import com.jwplayer.pub.api.media.meta.EMSGMetadataCue;
import com.jwplayer.pub.api.media.meta.ID3MetadataCue;
import com.jwplayer.pub.api.media.meta.InPlaylistTimedMetadataCue;
import com.jwplayer.pub.api.media.meta.Metadata;
import com.jwplayer.pub.api.media.meta.MetadataCue;
import com.jwplayer.pub.api.media.meta.ProgramDateTimeMetadataCue;
import com.jwplayer.pub.api.media.playlists.ExternalMetadata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Serializes the JW Player Android SDK metadata events into JSON-safe maps for the
 * {@code onMeta} / {@code onMetadataCueParsed} React Native events.
 *
 * <p>The payload shape mirrors the JW Player web player's {@code meta} / {@code metadataCueParsed}
 * events: {@code { metadataType, metadataTime?, metadata?, programDateTime?, ... }}.
 *
 * <p>Binary payloads (ID3 PRIV / GEOB / APIC data, emsg message data) are base64 encoded and
 * dates are ISO 8601 strings so everything survives the bridge.
 */
final class RNJWPlayerMetadata {

    static final String TYPE_ID3 = "id3";
    static final String TYPE_EMSG = "emsg";
    static final String TYPE_DATE_RANGE = "date-range";
    static final String TYPE_PROGRAM_DATE_TIME = "program-date-time";
    static final String TYPE_EXTERNAL = "external";
    static final String TYPE_MEDIA = "media";
    static final String TYPE_UNKNOWN = "unknown";

    private RNJWPlayerMetadata() {
    }

    // ---------------------------------------------------------------------------------------------
    // Playback-time events (onMeta)
    // ---------------------------------------------------------------------------------------------

    /**
     * {@link MetaEvent} carries either ID3 frames (timed metadata reached during playback) or
     * track-format statistics (on first frame / format change), never both.
     */
    static WritableMap fromMetaEvent(MetaEvent event) {
        Metadata metadata = event != null ? event.getMetadata() : null;
        if (metadata == null) {
            return null;
        }
        List<Id3Frame> frames = metadata.getId3Metadata();
        if (frames != null && !frames.isEmpty()) {
            // The SDK does not report a cue time for playback-time ID3 frames.
            return MapUtil.toWritableMap(id3(frames, Double.NaN));
        }
        return MapUtil.toWritableMap(media(metadata));
    }

    /** {@code EXT-X-DATERANGE} / {@code EXT-X-PROGRAM-DATE-TIME} reached during playback. */
    static WritableMap fromInPlaylistTimedMetadata(InPlaylistTimedMetadataEvent event) {
        if (event == null) {
            return null;
        }
        if (event instanceof DateRangeEvent) {
            DateRangeEvent dateRange = (DateRangeEvent) event;
            return MapUtil.toWritableMap(dateRange(dateRange.getRawTag(), dateRange.getStart(), dateRange.getEnd(),
                    dateRange.getDuration(), dateRange.getId(), dateRange.getStartDate(), dateRange.getAttributes()));
        }
        if (event instanceof ProgramDateTimeEvent) {
            ProgramDateTimeEvent pdt = (ProgramDateTimeEvent) event;
            return MapUtil.toWritableMap(programDateTime(pdt.getRawTag(), pdt.getStart(), pdt.getEnd(), pdt.getProgramDateTime()));
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", event.getRawTag());
        body.put("start", event.getStart());
        body.put("end", event.getEnd());
        return MapUtil.toWritableMap(event(TYPE_UNKNOWN, event.getStart(), body));
    }

    /** DASH {@code emsg} boxes reached during playback: one event per message, like the web player. */
    static List<WritableMap> fromEventMessages(List<EventMessage> messages) {
        List<WritableMap> events = new ArrayList<>();
        if (messages == null) {
            return events;
        }
        for (EventMessage message : messages) {
            if (message != null) {
                events.add(MapUtil.toWritableMap(emsg(message)));
            }
        }
        return events;
    }

    /** A configured {@code externalMetadata} cue point reached during playback. */
    static WritableMap fromExternalMetadata(ExternalMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("identifier", String.valueOf(metadata.getId()));
        body.put("id", metadata.getId());
        body.put("start", metadata.getStartTime());
        body.put("end", metadata.getEndTime());
        return MapUtil.toWritableMap(event(TYPE_EXTERNAL, metadata.getStartTime(), body));
    }

    // ---------------------------------------------------------------------------------------------
    // Parse-time events (onMetadataCueParsed)
    // ---------------------------------------------------------------------------------------------

    /** One {@link MetadataCueParsedEvent} can expand to several events (one per emsg message). */
    static List<WritableMap> fromMetadataCueParsed(MetadataCueParsedEvent event) {
        List<WritableMap> events = new ArrayList<>();
        MetadataCue cue = event != null ? event.getMetadataCue() : null;
        if (cue == null || cue.getMetadataCueType() == null) {
            return events;
        }

        switch (cue.getMetadataCueType()) {
            case ID3: {
                ID3MetadataCue id3Cue = (ID3MetadataCue) cue;
                events.add(MapUtil.toWritableMap(id3(id3Cue.getId3Frames(), id3Cue.getStart())));
                break;
            }
            case EMSG: {
                EMSGMetadataCue emsgCue = (EMSGMetadataCue) cue;
                if (emsgCue.getEMSGs() != null) {
                    for (EventMessage message : emsgCue.getEMSGs()) {
                        if (message != null) {
                            events.add(MapUtil.toWritableMap(emsg(message)));
                        }
                    }
                }
                break;
            }
            case DATE_RANGE: {
                DateRangeMetadataCue dateRange = (DateRangeMetadataCue) cue;
                events.add(MapUtil.toWritableMap(dateRange(dateRange.getRawTag(), dateRange.getStart(), dateRange.getEnd(),
                        dateRange.getDuration(), dateRange.getId(), dateRange.getStartDate(), dateRange.getAttributes())));
                break;
            }
            case PROGRAM_DATE_TIME: {
                ProgramDateTimeMetadataCue pdt = (ProgramDateTimeMetadataCue) cue;
                events.add(MapUtil.toWritableMap(programDateTime(pdt.getRawTag(), pdt.getStart(), pdt.getEnd(), pdt.getProgramDateTime())));
                break;
            }
            default: {
                Map<String, Object> body = new LinkedHashMap<>();
                if (cue instanceof InPlaylistTimedMetadataCue) {
                    body.put("content", ((InPlaylistTimedMetadataCue) cue).getRawTag());
                }
                events.add(MapUtil.toWritableMap(event(TYPE_UNKNOWN, cue.getStart(), body)));
                break;
            }
        }
        return events;
    }

    // ---------------------------------------------------------------------------------------------
    // Payload builders
    // ---------------------------------------------------------------------------------------------

    private static Map<String, Object> id3(List<Id3Frame> frames, double time) {
        Map<String, Object> flattened = new LinkedHashMap<>();
        if (frames != null) {
            for (Id3Frame frame : frames) {
                putFrame(flattened, frame);
            }
        }
        return event(TYPE_ID3, time, flattened);
    }

    /**
     * Flattens one ID3 frame into the {@code metadata} map keyed by frame id. Frames that carry a
     * description / owner (TXXX, WXXX, PRIV, GEOB, ...) nest their value under it, matching the
     * web player's ID3 parser.
     */
    private static void putFrame(Map<String, Object> target, Id3Frame frame) {
        if (frame == null || frame.id == null) {
            return;
        }
        String id = frame.id;
        if (frame instanceof TextInformationFrame) {
            TextInformationFrame text = (TextInformationFrame) frame;
            // ID3v2.4 allows several null-separated values per text frame. The frame entry keeps
            // them all; the friendly alias stays a string (the first value, as ExoPlayer's own
            // MediaMetadata mapping does).
            Object value = text.values.size() == 1 ? text.values.get(0) : new ArrayList<Object>(text.values);
            putNested(target, id, text.description, value);
            putAlias(target, id, text.values.isEmpty() ? null : text.values.get(0));
        } else if (frame instanceof UrlLinkFrame) {
            UrlLinkFrame url = (UrlLinkFrame) frame;
            putNested(target, id, url.description, url.url);
            putAlias(target, id, url.url);
        } else if (frame instanceof PrivFrame) {
            PrivFrame priv = (PrivFrame) frame;
            putNested(target, id, priv.owner, base64(priv.privateData));
        } else if (frame instanceof GeobFrame) {
            GeobFrame geob = (GeobFrame) frame;
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("mimeType", geob.mimeType);
            value.put("filename", geob.filename);
            value.put("description", geob.description);
            value.put("data", base64(geob.data));
            putNested(target, id, geob.description, value);
        } else if (frame instanceof ApicFrame) {
            ApicFrame apic = (ApicFrame) frame;
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("mimeType", apic.mimeType);
            value.put("description", apic.description);
            value.put("pictureType", apic.pictureType);
            value.put("pictureData", base64(apic.pictureData));
            putNested(target, id, apic.description, value);
        } else if (frame instanceof CommentFrame) {
            CommentFrame comment = (CommentFrame) frame;
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("language", comment.language);
            value.put("description", comment.description);
            value.put("text", comment.text);
            putNested(target, id, comment.description, value);
        } else if (frame instanceof InternalFrame) {
            InternalFrame internal = (InternalFrame) frame;
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("domain", internal.domain);
            value.put("description", internal.description);
            value.put("text", internal.text);
            putNested(target, id, internal.description, value);
        } else if (frame instanceof BinaryFrame) {
            target.put(id, base64(((BinaryFrame) frame).data));
        } else {
            // ChapterFrame, ChapterTocFrame, MlltFrame, ...: fall back to the frame's own description.
            target.put(id, frame.toString());
        }
    }

    /**
     * One ID3 group can carry the same frame id several times, with and without a description
     * (e.g. {@code TXXX} with an empty description next to {@code TXXX(segment-id)}). Nothing is
     * overwritten: once a frame id nests, a description-less value lives under the empty-string key.
     */
    @SuppressWarnings("unchecked")
    private static void putNested(Map<String, Object> target, String key, String description, Object value) {
        String nestedKey = description == null ? "" : description;
        Object existing = target.get(key);
        if (nestedKey.isEmpty() && !(existing instanceof Map)) {
            target.put(key, value);
            return;
        }
        Map<String, Object> nested;
        if (existing instanceof Map) {
            nested = (Map<String, Object>) existing;
        } else {
            nested = new LinkedHashMap<>();
            if (existing != null) {
                nested.put("", existing);
            }
            target.put(key, nested);
        }
        nested.put(nestedKey, value);
    }

    /** Friendly aliases the web player also exposes (title, artist, album, url). */
    private static void putAlias(Map<String, Object> target, String id, Object value) {
        if (value == null) {
            return;
        }
        String alias;
        switch (id) {
            case "TIT2":
            case "TT2":
                alias = "title";
                break;
            case "TPE1":
            case "TP1":
                alias = "artist";
                break;
            case "TALB":
            case "TAL":
                alias = "album";
                break;
            case "WXXX":
                alias = "url";
                break;
            default:
                return;
        }
        if (!target.containsKey(alias)) {
            target.put(alias, value);
        }
    }

    /** Track-format statistics. Unset fields ({@link Metadata#NO_VALUE} / empty) are omitted. */
    private static Map<String, Object> media(Metadata metadata) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("metadataType", TYPE_MEDIA);
        if (metadata.getHeight() != Metadata.NO_VALUE) {
            payload.put("height", metadata.getHeight());
        }
        if (metadata.getWidth() != Metadata.NO_VALUE) {
            payload.put("width", metadata.getWidth());
        }
        if (metadata.getFramerate() != Metadata.NO_VALUE && metadata.getFramerate() > 0) {
            payload.put("frameRate", metadata.getFramerate());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        // A bitrate of 0 means "not reported" for the selected format; treat it like NO_VALUE.
        putIfPositive(body, "videoBitrate", metadata.getVideoBitrate());
        putIfSet(body, "videoId", metadata.getVideoId());
        putIfSet(body, "videoMimeType", metadata.getVideoMimeType());
        putIfSet(body, "droppedFrames", metadata.getDroppedFrames());
        putIfPositive(body, "audioBitrate", metadata.getAudioBitrate());
        putIfSet(body, "audioChannels", metadata.getAudioChannels());
        putIfSet(body, "audioSamplingRate", metadata.getAudioSamplingRate());
        putIfSet(body, "audioId", metadata.getAudioId());
        putIfSet(body, "audioMimeType", metadata.getAudioMimeType());
        putIfSet(body, "language", metadata.getLanguage());
        payload.put("metadata", body);
        return payload;
    }

    private static Map<String, Object> dateRange(String rawTag, double start, double end, double duration,
                                                 String id, Date startDate, Map<String, String> attributes) {
        List<Object> attributeList = new ArrayList<>();
        String endDate = null;
        if (attributes != null) {
            // The SDK collects the tag's attributes into a HashMap, so manifest order is gone and
            // a repeated name has already collapsed to one value. Sort by name for a stable order.
            List<String> names = new ArrayList<>();
            for (String name : attributes.keySet()) {
                if (name != null) {
                    names.add(name);
                }
            }
            Collections.sort(names);
            for (String name : names) {
                String value = attributes.get(name);
                Map<String, Object> attribute = new LinkedHashMap<>();
                attribute.put("name", name);
                attribute.put("value", value);
                attributeList.add(attribute);
                if ("END-DATE".equals(name)) {
                    endDate = value;
                }
            }
        }
        if (duration < 0) {
            // The SDK only reports PLANNED-DURATION. Fall back like the web player does:
            // the DURATION attribute, then the cue's own span.
            String declared = attributes != null ? attributes.get("DURATION") : null;
            if (declared != null) {
                try {
                    duration = Double.parseDouble(declared);
                } catch (NumberFormatException ignored) {
                    duration = -1;
                }
            }
            if (duration < 0) {
                duration = Math.max(0, end - start);
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("tag", "EXT-X-DATERANGE");
        body.put("start", start);
        body.put("end", end);
        body.put("duration", duration);
        body.put("attributes", attributeList);
        if (startDate != null) {
            body.put("startDate", iso8601(startDate));
        }
        if (endDate != null) {
            body.put("endDate", isoDateAttribute(endDate));
        }
        if (id != null && !id.isEmpty()) {
            body.put("id", id);
        }
        if (rawTag != null) {
            body.put("content", rawTag);
        }
        return event(TYPE_DATE_RANGE, start, body);
    }

    private static Map<String, Object> programDateTime(String rawTag, double start, double end, Date programDateTime) {
        String iso = programDateTime != null ? iso8601(programDateTime) : null;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("programDateTime", iso);
        body.put("start", start);
        body.put("end", end);
        if (rawTag != null) {
            body.put("content", rawTag);
        }
        Map<String, Object> payload = event(TYPE_PROGRAM_DATE_TIME, start, body);
        // Hoisted to the top level as well, matching the web player.
        payload.put("programDateTime", iso);
        return payload;
    }

    private static Map<String, Object> emsg(EventMessage message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", message.id);
        body.put("schemeIdUri", message.schemeIdUri);
        body.put("value", message.value);
        body.put("duration", message.durationMs == C.TIME_UNSET ? null : message.durationMs / 1000.0);
        body.put("messageData", base64(message.messageData));
        // The SDK does not expose the presentation time of emsg boxes.
        return event(TYPE_EMSG, Double.NaN, body);
    }

    // ---------------------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------------------

    private static Map<String, Object> event(String type, double time, Map<String, Object> body) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("metadataType", type);
        if (!Double.isNaN(time) && !Double.isInfinite(time) && time >= 0) {
            payload.put("metadataTime", time);
        }
        payload.put("metadata", body);
        return payload;
    }

    private static void putIfSet(Map<String, Object> target, String key, int value) {
        if (value != Metadata.NO_VALUE) {
            target.put(key, value);
        }
    }

    private static void putIfPositive(Map<String, Object> target, String key, int value) {
        if (value > 0) {
            target.put(key, value);
        }
    }

    private static void putIfSet(Map<String, Object> target, String key, String value) {
        if (value != null && !value.isEmpty()) {
            target.put(key, value);
        }
    }

    private static String base64(byte[] data) {
        return data == null ? null : Base64.encodeToString(data, Base64.NO_WRAP);
    }

    private static String iso8601(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    /**
     * Re-emits a manifest date attribute (e.g. {@code END-DATE}) through the same UTC formatter as
     * {@code startDate}, so both fields share one format whatever offset the manifest used. Falls
     * back to the raw string when it does not parse. Fully qualified to avoid this package's Util.
     */
    private static String isoDateAttribute(String value) {
        try {
            return iso8601(new Date(androidx.media3.common.util.Util.parseXsDateTime(value)));
        } catch (ParserException | RuntimeException e) {
            return value;
        }
    }
}
