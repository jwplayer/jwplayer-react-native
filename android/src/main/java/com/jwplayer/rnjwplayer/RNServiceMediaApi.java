package com.jwplayer.rnjwplayer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.jwplayer.pub.api.JWPlayer;
import com.jwplayer.pub.api.background.NotificationButton;
import com.jwplayer.pub.api.background.ServiceMediaApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Background-audio transport for the bridge: the SDK defaults, plus custom notification buttons
 * configured from JS ({@code notificationButtons} config / {@code setNotificationButtons}).
 * Taps are reported to JS through {@link Listener}.
 */
class RNServiceMediaApi extends ServiceMediaApi {

    private static final String TAG = "RNServiceMediaApi";

    interface Listener {
        void onNotificationButtonClick(@NonNull String id);
    }

    /** One button as configured from JS; the icon is resolved when the buttons are read. */
    static final class ButtonSpec {
        final String id;
        final String icon;
        final String title;
        final boolean enabled;
        final boolean inCompactView;
        final int placement;

        ButtonSpec(String id, String icon, String title, boolean enabled, boolean inCompactView,
                   int placement) {
            this.id = id;
            this.icon = icon;
            this.title = title;
            this.enabled = enabled;
            this.inCompactView = inCompactView;
            this.placement = placement;
        }
    }

    @Nullable
    private final Listener mListener;
    private volatile List<ButtonSpec> mButtons;

    RNServiceMediaApi(@NonNull JWPlayer player, @NonNull List<ButtonSpec> buttons,
                      @Nullable Listener listener) {
        super(player);
        mButtons = buttons;
        mListener = listener;
    }

    /** Replaces the custom buttons and asks the SDK to rebuild the notification. */
    void setButtons(@NonNull List<ButtonSpec> buttons) {
        mButtons = buttons;
        invalidateNotificationButtons();
    }

    @NonNull
    @Override
    public List<NotificationButton> getNotificationButtons(@NonNull Context context) {
        List<NotificationButton> result = new ArrayList<>(super.getNotificationButtons(context));
        for (ButtonSpec spec : mButtons) {
            int iconResId = resolveDrawable(context, spec.icon);
            if (iconResId == 0) {
                Log.w(TAG, "Notification button '" + spec.id + "' skipped: no drawable named '"
                        + spec.icon + "' in the app's resources");
                continue;
            }
            result.add(new NotificationButton.Builder(spec.id, iconResId, spec.title)
                    .enabled(spec.enabled)
                    .inCompactView(spec.inCompactView)
                    .placement(spec.placement)
                    .build());
        }
        return result;
    }

    @Override
    public void onNotificationButtonClick(@NonNull String buttonId) {
        if (mListener != null) {
            mListener.onNotificationButtonClick(buttonId);
        }
    }

    private static int resolveDrawable(Context context, @Nullable String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        if (id == 0) {
            id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
        }
        return id;
    }

    /**
     * Parses the JS button list. Never throws: entries that aren't objects, or lack a string
     * id, title or icon, are dropped with a warning; optional fields of the wrong type fall back
     * to their defaults; duplicate ids keep the first entry.
     */
    @NonNull
    static List<ButtonSpec> parse(@Nullable ReadableArray array) {
        if (array == null || array.size() == 0) {
            return Collections.emptyList();
        }
        List<ButtonSpec> specs = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            try {
                if (array.getType(i) != ReadableType.Map) {
                    Log.w(TAG, "Notification button at index " + i + " skipped: not an object");
                    continue;
                }
                ReadableMap m = array.getMap(i);
                if (m == null) {
                    continue;
                }
                String id = optString(m, "id");
                String icon = optString(m, "icon");
                String title = optString(m, "title");
                if (id == null || id.isEmpty() || icon == null || title == null) {
                    Log.w(TAG, "Notification button at index " + i
                            + " skipped: id, icon and title are required strings");
                    continue;
                }
                if (seen.contains(id)) {
                    Log.w(TAG, "Notification button '" + id + "' skipped: duplicate id");
                    continue;
                }
                seen.add(id);
                boolean enabled = optBoolean(m, "enabled", true);
                boolean inCompactView = optBoolean(m, "inCompactView", false);
                int placement = NotificationButton.PLACEMENT_OVERFLOW;
                String p = optString(m, "placement");
                if ("back".equals(p)) {
                    placement = NotificationButton.PLACEMENT_BACK;
                } else if ("forward".equals(p)) {
                    placement = NotificationButton.PLACEMENT_FORWARD;
                }
                specs.add(new ButtonSpec(id, icon, title, enabled, inCompactView, placement));
            } catch (RuntimeException e) {
                Log.w(TAG, "Notification button at index " + i + " skipped: " + e.getMessage());
            }
        }
        return specs;
    }

    /** The string at {@code key}, or null when it is missing, null or not a string. */
    @Nullable
    private static String optString(@NonNull ReadableMap m, @NonNull String key) {
        if (!m.hasKey(key) || m.getType(key) != ReadableType.String) {
            if (m.hasKey(key) && m.getType(key) != ReadableType.Null) {
                Log.w(TAG, "Notification button field '" + key + "' ignored: expected a string");
            }
            return null;
        }
        return m.getString(key);
    }

    /** The boolean at {@code key}, or {@code fallback} when it is missing, null or not a boolean. */
    private static boolean optBoolean(@NonNull ReadableMap m, @NonNull String key, boolean fallback) {
        if (!m.hasKey(key) || m.getType(key) != ReadableType.Boolean) {
            if (m.hasKey(key) && m.getType(key) != ReadableType.Null) {
                Log.w(TAG, "Notification button field '" + key + "' ignored: expected a boolean");
            }
            return fallback;
        }
        return m.getBoolean(key);
    }
}
