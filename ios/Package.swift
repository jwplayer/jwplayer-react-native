// swift-tools-version: 6.0
// Swift Package Manager manifest for React Native's SPM autolinking (React Native 0.88+).
// CocoaPods apps use RNJWPlayer.podspec, which stays on JWPlayerKit 4.28.2 (the final CocoaPods release).
//
// The ReactNative and React-GeneratedCode paths follow React Native's autolinking layout:
// the app references this package (the wrapper's ios/ directory) through
// ios/build/generated/autolinking/libs/RNJWPlayer. It lives in ios/, not the repo root, so the
// link doesn't point back over Example/ (which links this package) and loop.
//
// SwiftPM can't compile Swift and Objective-C in one target, so the Objective-C view manager
// export (RNJWPlayerViewManager.m) lives in its own target on top of the Swift sources.
//
// Google IMA and Google Cast are always included here; the podspec's $RNJWPlayerUseGoogleIMA /
// $RNJWPlayerUseGoogleCast switches have no SwiftPM equivalent in React Native's autolinking.

import PackageDescription

let reactDependencies: [Target.Dependency] = [
    .product(name: "ReactHeaders", package: "ReactNative"),
    .product(name: "ReactNativeHeaders", package: "ReactNative"),
    .product(name: "ReactNativeDependenciesHeaders", package: "ReactNative"),
    .product(name: "ReactAppHeaders", package: "React-GeneratedCode"),
]

let package = Package(
    name: "RNJWPlayer",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "RNJWPlayer", targets: ["RNJWPlayer"]),
    ],
    dependencies: [
        .package(name: "ReactNative", path: "../../../../xcframeworks"),
        .package(name: "React-GeneratedCode", path: "../../../ios"),
        .package(url: "https://github.com/jwplayer/JWPlayerKit-package.git", exact: "4.28.2"),
        .package(url: "https://github.com/googleads/swift-package-manager-google-interactive-media-ads-ios.git", .upToNextMinor(from: "3.32.0")),
    ],
    targets: [
        // Google Cast, dynamic build. Our iOS docs require the dynamic build: the static one needs its
        // resource bundles copied into the app by hand, and without them discovery finds no devices.
        // This points at the same zip Google's google-cast-ios-sdk package uses for GoogleCastDynamic,
        // without that package's static GTMSessionFetcher dependency. The dynamic framework already
        // contains GTMSessionFetcher, so linking it again loads the GTM classes twice.
        // When updating, copy the url and checksum from google-cast-ios-sdk's Package.swift.
        .binaryTarget(
            name: "GoogleCast",
            url: "https://dl.google.com/dl/chromecast/sdk/ios/GoogleCastSDK-ios-4.8.6_dynamic.zip",
            checksum: "55f6c21291a1315c68063f07e7d76225564bff70f2fd38caad135c71d66eb310"
        ),
        .target(
            name: "RNJWPlayerSwift",
            dependencies: reactDependencies + [
                .product(name: "JWPlayerKit", package: "JWPlayerKit-package"),
                .product(name: "GoogleInteractiveMediaAds", package: "swift-package-manager-google-interactive-media-ads-ios"),
                "GoogleCast",
            ],
            path: "RNJWPlayer",
            exclude: ["RNJWPlayer-Bridging-Header.h"],
            swiftSettings: [
                .define("USE_GOOGLE_IMA"),
                .define("USE_GOOGLE_CAST"),
                .swiftLanguageMode(.v5),
            ]
        ),
        .target(
            name: "RNJWPlayer",
            dependencies: reactDependencies + ["RNJWPlayerSwift"],
            path: "RNJWPlayerObjC",
            publicHeadersPath: ".",
            cSettings: [.define("USE_GOOGLE_CAST", to: "1")]
        ),
    ]
)
