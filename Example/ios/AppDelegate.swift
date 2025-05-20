import UIKit
import React
import React_RCTAppDelegate
import ReactAppDependencyProvider
import RNBootSplash
import GoogleCast

@main
class AppDelegate: RCTAppDelegate {
  override func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
    self.moduleName = "RNJWPlayer"
    self.dependencyProvider = RCTAppDependencyProvider()
/*:
    kGCKDefaultMediaReceiverApplicationID is a constant from the GoogleCast SDK, representing the default Chromecast receiver.
     - https://developers.google.com/android/reference/com/google/android/gms/cast/CastMediaControlIntent#public-static-final-string-default_media_receiver_application_id
    If using a custom Chromecast receiver, replace kGCKDefaultMediaReceiverApplicationID with your custom receiver's App ID string.
     - https://github.com/jwplayer/jwplayer-react-native?tab=readme-ov-file#casting
*/
    let discoveryCriteria = GCKDiscoveryCriteria(applicationID: kGCKDefaultMediaReceiverApplicationID)
    let options = GCKCastOptions(discoveryCriteria: discoveryCriteria)
    GCKCastContext.setSharedInstanceWith(options)
    // You can add your custom initial props in the dictionary below.
    // They will be passed down to the ViewController used by React Native.
    self.initialProps = [:]

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  override func sourceURL(for bridge: RCTBridge) -> URL? {
    self.bundleURL()
  }

  override func bundleURL() -> URL? {
#if DEBUG
    RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index")
#else
    Bundle.main.url(forResource: "main", withExtension: "jsbundle")
#endif
  }

  override func customize(_ rootView: RCTRootView!) {
    super.customize(rootView)
    RNBootSplash.initWithStoryboard("LaunchScreen", rootView: rootView)
  }
}
