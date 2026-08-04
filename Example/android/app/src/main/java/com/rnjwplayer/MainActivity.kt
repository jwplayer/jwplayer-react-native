package com.rnjwplayer

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.core.view.WindowCompat
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.swmansion.rnscreens.fragment.restoration.RNScreensFragmentFactory

class MainActivity : ReactActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // The splash screen is initialized here, before super.onCreate, rather than from a
        // ReactActivityDelegate. The fragment factory is required when using react-native-screens.
        supportFragmentManager.fragmentFactory = RNScreensFragmentFactory()
        // react-native-bootsplash's post-splash view is deliberately not initialised.
        // On API 31+ it draws a large black ring where the logo should be: its compat
        // drawable does not resolve ?bootSplashLogo from this theme (verified with
        // three different logo assets, and bootSplashBackground has no effect on it
        // either), so it falls back to the library's own default. The platform splash
        // screen configured in values-v31/BootTheme already shows the branded logo.

        super.onCreate(savedInstanceState)

        // With the splash view gone, nothing covers the window between the platform
        // splash exiting and React's first render, and the bare window is black.
        // Painting it with the splash background makes that gap white instead.
        window.setBackgroundDrawableResource(R.color.bootsplash_background)
        window.decorView.setBackgroundResource(R.color.bootsplash_background)

        // Enable edge-to-edge display
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

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
        DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

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
