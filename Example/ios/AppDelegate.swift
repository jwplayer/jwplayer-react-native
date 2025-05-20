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

    let discoveryCriteria = GCKDiscoveryCriteria(applicationID: "CC1AD845")
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

// Add GCKLoggerDelegate conformance outside the AppDelegate class
extension AppDelegate: GCKLoggerDelegate {
  func logMessage(_ message: String,
                  at level: GCKLoggerLevel,
                  fromFunction function: String,
                  location: String) {
    // You can filter by level here if needed
    // e.g., if level.rawValue >= GCKLoggerLevel.debug.rawValue (check SDK for exact enum usage)
    print("[CAST] [\(level.nameForOutput())] \(location): \(function) - \(message)")
  }
}

// Helper extension for GCKLoggerLevel to get a string name (optional, but nice for logging)
extension GCKLoggerLevel {
    func nameForOutput() -> String {
        switch self {
        case .verbose: return "VERBOSE"
        case .debug: return "DEBUG"
        case .info: return "INFO"
        case .warning: return "WARNING"
        case .error: return "ERROR"
        case .none: return "NONE"
        @unknown default:
            return "UNKNOWN_LOG_LEVEL"
        }
    }
}
