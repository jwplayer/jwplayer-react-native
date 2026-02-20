//
//  RNJWPlayerOfflineModule.m
//  RNJWPlayer
//
//  React Native bridge for the offline download module.
//

#if __has_include("React/RCTBridgeModule.h")
#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"
#else
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#endif

@interface RCT_EXTERN_MODULE(RNJWPlayerOfflineModule, RCTEventEmitter)

RCT_EXTERN_METHOD(downloadVideo:(NSDictionary *)config
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(isDownloaded:(NSString *)mediaId
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getDownloads:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(deleteDownload:(NSString *)mediaId
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getOfflinePlaylistItem:(NSString *)mediaId
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(supportedEvents)

@end
