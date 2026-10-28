// swift-tools-version: 6.0
// Swift Package Manager manifest for React Native's SPM autolinking (React Native 0.88+).
// CocoaPods apps keep using RNJWPlayer.podspec.
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
        .package(url: "https://github.com/googleads/swift-package-manager-google-interactive-media-ads-ios.git", exact: "3.32.0"),
        .package(url: "https://github.com/googlecast/google-cast-ios-sdk.git", exact: "4.8.6"),
    ],
    targets: [
        .target(
            name: "RNJWPlayerSwift",
            dependencies: reactDependencies + [
                .product(name: "JWPlayerKit", package: "JWPlayerKit-package"),
                .product(name: "GoogleInteractiveMediaAds", package: "swift-package-manager-google-interactive-media-ads-ios"),
                .product(name: "GoogleCastStatic", package: "google-cast-ios-sdk"),
            ],
            path: "RNJWPlayer",
            exclude: ["RNJWPlayerViewManager.m", "RNJWPlayer-Bridging-Header.h"],
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
