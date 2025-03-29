package com.rnjwplayer

import android.content.Intent
import android.content.res.Configuration
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.zoontek.rnbootsplash.RNBootSplash

class MainActivity : ReactActivity() {
    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    override fun getMainComponentName(): String = "RNJWPlayer"

    /**
     * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
     * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
     */
    override fun createReactActivityDelegate(): ReactActivityDelegate =
        MainActivityDelegate(this, mainComponentName, fabricEnabled)

    class MainActivityDelegate(
        activity: ReactActivity,
        mainComponentName: String,
        fabricEnabled: Boolean
    ) :
        DefaultReactActivityDelegate(activity, mainComponentName, fabricEnabled) {
        override fun loadApp(appKey: String) {
            RNBootSplash.init(plainActivity, R.style.BootTheme)
            super.loadApp(appKey)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val intent = Intent("onConfigurationChanged")
        intent.putExtra("newConfig", newConfig)
        this.sendBroadcast(intent)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        val intent = Intent("onPictureInPictureModeChanged")
        intent.putExtra("isInPictureInPictureMode", isInPictureInPictureMode)
        intent.putExtra("newConfig", newConfig)
        this.sendBroadcast(intent)
    }
}
