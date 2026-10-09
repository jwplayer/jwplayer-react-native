import UIKit
import React
import React_RCTAppDelegate
import ReactAppDependencyProvider
import RNBootSplash
import GoogleCast

// iOS 27 requires the UIScene lifecycle, so the window and the React Native root
// view are created in SceneDelegate. AppDelegate only owns the React Native factory
// and app-wide setup such as Google Cast.
@main
class AppDelegate: UIResponder, UIApplicationDelegate {
  // Set by SceneDelegate. React Native still reads the window from the app
  // delegate in places (RedBox, for example), so keep it reachable here.
  var window: UIWindow?
  var reactNativeDelegate: ReactNativeDelegate?
  var reactNativeFactory: RCTReactNativeFactory?

  func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
    let delegate = ReactNativeDelegate()
    delegate.dependencyProvider = RCTAppDependencyProvider()
    reactNativeDelegate = delegate
    reactNativeFactory = RCTReactNativeFactory(delegate: delegate)
/*:
    kGCKDefaultMediaReceiverApplicationID is a constant from the GoogleCast SDK, representing the default Chromecast receiver.
     - https://developers.google.com/android/reference/com/google/android/gms/cast/CastMediaControlIntent#public-static-final-string-default_media_receiver_application_id
    If using a custom Chromecast receiver, replace kGCKDefaultMediaReceiverApplicationID with your custom receiver's App ID string.
     - https://github.com/jwplayer/jwplayer-react-native?tab=readme-ov-file#casting
*/
    let discoveryCriteria = GCKDiscoveryCriteria(applicationID: kGCKDefaultMediaReceiverApplicationID)
    let options = GCKCastOptions(discoveryCriteria: discoveryCriteria)
    GCKCastContext.setSharedInstanceWith(options)

    return true
  }

  func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
    let configuration = UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    configuration.delegateClass = SceneDelegate.self
    return configuration
  }
}

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
  var window: UIWindow?

  func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
    guard let windowScene = scene as? UIWindowScene,
          let factory = (UIApplication.shared.delegate as? AppDelegate)?.reactNativeFactory else { return }

    // You can add your custom initial props in the dictionary below.
    // They will be passed down to the ViewController used by React Native.
    let rootView = factory.rootViewFactory.view(withModuleName: "RNJWPlayer", initialProperties: [:])

    let rootViewController = UIViewController()
    rootViewController.view = rootView

    let window = UIWindow(windowScene: windowScene)
    window.rootViewController = rootViewController
    self.window = window
    (UIApplication.shared.delegate as? AppDelegate)?.window = window
    window.makeKeyAndVisible()
  }
}

class ReactNativeDelegate: RCTDefaultReactNativeFactoryDelegate {
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

  override func customize(_ rootView: RCTRootView) {
    super.customize(rootView)
    RNBootSplash.initWithStoryboard("LaunchScreen", rootView: rootView)
  }
}
