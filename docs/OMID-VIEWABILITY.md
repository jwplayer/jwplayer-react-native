# OMID viewability

The JW Player SDKs measure ad viewability through the IAB Open Measurement SDK (OMID). A verification vendor (IAS, DoubleVerify, Moat, a Xandr / Microsoft Monetize partner, …) listed in a VAST `<AdVerifications>` block loads its script into the ad session and samples how much of the ad is on screen.

## Turning it on for JW VAST ads

OMID is **off by default** for the JW VAST client. Enable it in the `advertising` block:

```js
advertising: {
  client: 'vast',
  omidSupport: 'enabled',          // or 'auto'
  // allowedOmidVendors: ['integralads.com-omid'], // optional allow-list; empty or omitted allows all
  schedule: [{ offset: 'pre', tag: 'https://example.com/vast.xml' }],
}
```

| `omidSupport` | `allowedOmidVendors` | Result |
|---|---|---|
| omitted | omitted | OMID off |
| omitted | set | OMID on, limited to those vendors |
| `'auto'` / `'enabled'` | optional | OMID on |
| `'disabled'` | any | OMID off |

This works on the default config path on iOS and Android. On iOS it also works with `forceLegacyConfig: true` (use `adClient: 'vast'` and `adSchedule` there).

Google IMA (`client: 'googima'`) and IMA DAI run their own OMID session and ignore these keys.

## Friendly obstructions (iOS)

OMID treats any view drawn over the ad as something that hides it. The player's own controls are declared "friendly" by the SDK, but views your app renders on top of the player — custom controls, badges, gradients, a "Live" label — are not, so they lower the measured viewability, in the worst case to 0%.

Declare those views with `registerFriendlyObstructions`:

```jsx
const playerRef = useRef(null);
const controlsRef = useRef(null);

useEffect(() => {
  // Capture the player: React clears playerRef before this effect's cleanup runs.
  const player = playerRef.current;
  player?.registerFriendlyObstructions([
    { ref: controlsRef, purpose: 'mediaControls', reason: 'Custom player controls' },
  ]).then(({ failed }) => {
    // failed: [{ index, reason: 'noRef' | 'notFound' | 'containsPlayer' | 'visible' | 'duplicate' | 'noPlayer' }]
  });
  return () => player?.deregisterFriendlyObstructions([controlsRef]);
}, []);

return (
  <View>
    <JWPlayer ref={playerRef} config={config} style={{ flex: 1 }} />
    <View ref={controlsRef} collapsable={false} style={styles.controlsOverlay}>
      {/* … */}
    </View>
  </View>
);
```

| Method | Notes |
|---|---|
| `registerFriendlyObstructions(obstructions)` | `obstructions`: `{ ref, purpose, reason? }[]`. Registering a ref again replaces its purpose and reason. If the same view is listed twice, the later entry is used and the earlier one fails with `duplicate`. Resolves `{ registered, failed }`; rejects only on an unexpected native error. |
| `deregisterFriendlyObstructions(refs)` | Pass the same refs you registered. It works from an unmount cleanup, after React has cleared them. |
| `deregisterAllFriendlyObstructions()` | Removes every view your app registered. The player's own controls stay registered. |

**`purpose`** is one of:

- `'mediaControls'`: playback controls.
- `'closeAd'`: a close or skip button.
- `'notVisible'`: only for a view that is really hidden while the ad plays. A visible view declared `notVisible` fails with `visible`, because OMID would treat the whole view as covering the ad.
- `'other'`: anything else.

**`reason`** is sent to the vendor. OMID accepts at most 50 letters, digits and spaces, so any other characters are stripped.

### Things to know

- **Give the view `collapsable={false}`.** On the New Architecture a `<View>` that only affects layout is flattened and has no native view. It then fails with `notFound`.
- **Register the overlay itself, not a container that holds the player.** A view that contains the player would hide real obstructions from the vendor, so it fails with `containsPlayer`.
- **Timing.** You can register before the player finishes setting up, and before or during an ad. Registered views also survive `recreatePlayerWithConfig` and switching configs.
- **Deregister on unmount.** Views that unmount without being deregistered are dropped automatically at the next ad request or ad break, but deregistering yourself is more predictable.
- **Re-created views.** If a registered ref ends up on a new native view (a `key` change, or the New Architecture re-creating the view), the new view is registered automatically the next time the old one is found stale (at registration, player creation, ad request or ad break).
- **Android.** Android has the equivalent native API, but it isn't bridged yet. These methods do nothing on Android and resolve `{ registered: 0, failed: [] }`.

## Troubleshooting 0% viewability

- **Vendor reports "not measurable" (no OMID session).** Check `omidSupport` / `allowedOmidVendors`, confirm the VAST response has `<AdVerifications>`, and check that an allow-list isn't filtering out the vendor.
- **Measurable but 0% viewable.** Something undeclared covers the ad. Register your overlays. Also check the iOS SDK version: JWPlayerKit 4.26.2 through 4.28.0 don't declare the player's own full-frame controls, which reads as fully obstructed (fixed in iOS SDK SDK-12221).
