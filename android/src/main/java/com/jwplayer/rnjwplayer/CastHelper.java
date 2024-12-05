package com.jwplayer.rnjwplayer;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.cast.framework.CastContext;

/**
 * Dangerous class, only to be used if the app implementing the player has cast enabled
 */
public class CastHelper {

    private static final String TAG = "CastHelper";

    // Only use this if the app has cast enabled
    public static void killCastSession(Context context) {
        try {
            CastContext castContext = CastContext.getSharedInstance(context);
            castContext.getSessionManager().endCurrentSession(true);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
