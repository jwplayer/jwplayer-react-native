import React, {useCallback, useEffect, useRef, useState} from 'react';
import {Platform, Pressable, ScrollView, StyleSheet, Text, View} from 'react-native';
import Player from '../components/Player';
import {globalStyles} from '../../ui/styles/global.style';

// VAST response with an OMID <AdVerifications> block (vendor smartvendortest.com-omid).
const OMID_VAST_TAG = 'https://s3.amazonaws.com/george.success.jwplayer.com/omid_tag.xml';
const FILE = 'https://playertest.longtailvideo.com/adaptive/bbbfull/bbbfull.m3u8';

const modernConfig = {
  playlist: [{file: FILE, title: 'OMID preroll (default config)'}],
  advertising: {
    client: 'vast',
    omidSupport: 'enabled',
    schedule: [{offset: 'pre', tag: OMID_VAST_TAG}],
  },
};

const legacyConfig = {
  forceLegacyConfig: true,
  playlist: [{file: FILE, title: 'OMID preroll (legacy config)'}],
  advertising: {
    adClient: 'vast',
    omidSupport: 'enabled',
    adSchedule: [{offset: 'pre', tag: OMID_VAST_TAG}],
  },
};

// Registers itself on mount and deregisters in its unmount cleanup, the pattern from
// docs/OMID-VIEWABILITY.md. By the time the cleanup runs React has cleared `ref`, so this
// checks that deregistering still works.
const SelfRegisteringBadge = ({playerRef, append}) => {
  const ref = useRef(null);
  useEffect(() => {
    const player = playerRef.current;
    player
      ?.registerFriendlyObstructions([{ref, purpose: 'other', reason: 'Self registering badge'}])
      .then(result => append(`self badge register → ${JSON.stringify(result)}`));
    return () => {
      player?.deregisterFriendlyObstructions([ref]);
      append('self badge cleanup → deregister');
    };
  }, [playerRef, append]);
  return (
    <View ref={ref} collapsable={false} style={styles.selfBadge} pointerEvents="none">
      <Text style={styles.overlayText}>Self</Text>
    </View>
  );
};

export default () => {
  const playerRef = useRef(null);
  const overlayRef = useRef(null);
  const containerRef = useRef(null);
  const flattenedRef = useRef(null);
  const [legacy, setLegacy] = useState(false);
  const [overlayMounted, setOverlayMounted] = useState(true);
  // Changing the key gives the overlay a new native view behind the same ref.
  const [overlayKey, setOverlayKey] = useState(0);
  const [selfBadgeMounted, setSelfBadgeMounted] = useState(false);
  const [log, setLog] = useState([]);

  const append = useCallback(line => {
    setLog(prev => [`${new Date().toLocaleTimeString()}  ${line}`, ...prev].slice(0, 30));
  }, []);

  const registerEntries = useCallback(
    async (entries, label) => {
      if (!playerRef.current) return;
      const result = await playerRef.current.registerFriendlyObstructions(entries);
      append(`register(${label}) → ${JSON.stringify(result)}`);
    },
    [append],
  );

  const register = useCallback(
    (purpose, ref = overlayRef, label = 'overlay') =>
      registerEntries([{ref, purpose, reason: 'App overlay badge'}], `${label}, ${purpose}`),
    [registerEntries],
  );

  // Registered before the player finishes setting up, so this also covers the path
  // where obstructions are stored and applied once the native player exists.
  useEffect(() => {
    if (overlayMounted) register('other');
  }, [legacy, overlayMounted, register]);

  const config = legacy ? legacyConfig : modernConfig;

  return (
    <View style={globalStyles.container}>
      <View style={globalStyles.subContainer}>
        <View ref={containerRef} collapsable={false} style={globalStyles.playerContainer}>
          <Player
            key={legacy ? 'legacy' : 'modern'}
            ref={playerRef}
            style={{flex: 1}}
            config={{autostart: true, ...config}}
            onAdEvent={e => append(`adEvent type=${e.nativeEvent.type}`)}
          />
          {overlayMounted ? (
            // collapsable={false}: a layout-only View is flattened on the New Architecture
            // and would have no native view to register.
            <View key={overlayKey} ref={overlayRef} collapsable={false} style={styles.overlay} pointerEvents="none">
              <Text style={styles.overlayText}>App overlay</Text>
            </View>
          ) : null}
          {selfBadgeMounted ? <SelfRegisteringBadge playerRef={playerRef} append={append} /> : null}
          {/* Layout-only on purpose: flattened on the New Architecture, so it has no native view. */}
          <View ref={flattenedRef} style={styles.flattened} />
        </View>
      </View>

      {Platform.OS !== 'ios' ? (
        <Text style={styles.note}>Friendly obstruction registration is iOS only.</Text>
      ) : null}

      <View style={styles.row}>
        <Button label={legacy ? 'Use default config' : 'Use legacy config'} onPress={() => setLegacy(v => !v)} />
        <Button label="Register (mediaControls)" onPress={() => register('mediaControls')} />
      </View>
      <View style={styles.row}>
        <Button label="Deregister overlay" onPress={() => { playerRef.current?.deregisterFriendlyObstructions([overlayRef]); append('deregister(overlay)'); }} />
        <Button label="Deregister all" onPress={() => { playerRef.current?.deregisterAllFriendlyObstructions(); append('deregisterAll()'); }} />
      </View>
      <View style={styles.row}>
        <Button
          label={overlayMounted ? 'Unmount overlay (no deregister)' : 'Mount overlay'}
          onPress={() => setOverlayMounted(v => !v)}
        />
      </View>

      <View style={styles.row}>
        <Button label="Flattened view (expect notFound)" onPress={() => register('other', flattenedRef, 'flattened')} />
        <Button label="Player container (expect containsPlayer)" onPress={() => register('other', containerRef, 'container')} />
      </View>

      <View style={styles.row}>
        <Button label="notVisible on visible badge (expect visible)" onPress={() => register('notVisible')} />
        <Button
          label="Same view twice (expect duplicate)"
          onPress={() =>
            registerEntries(
              [
                {ref: overlayRef, purpose: 'other', reason: 'First entry'},
                {ref: overlayRef, purpose: 'mediaControls', reason: 'Second entry'},
              ],
              'overlay twice',
            )
          }
        />
      </View>
      <View style={styles.row}>
        <Button
          label="Re-create overlay view (same ref)"
          onPress={() => {
            setOverlayKey(k => k + 1);
            append('overlay view re-created (not re-registered by the screen)');
          }}
        />
        <Button
          label={selfBadgeMounted ? 'Unmount self badge (cleanup deregisters)' : 'Mount self badge'}
          onPress={() => setSelfBadgeMounted(v => !v)}
        />
      </View>

      <ScrollView style={styles.log}>
        {log.map((line, i) => (
          <Text key={i} style={styles.logLine}>{line}</Text>
        ))}
      </ScrollView>
    </View>
  );
};

const Button = ({label, onPress}) => (
  <Pressable style={styles.button} onPress={onPress}>
    <Text style={styles.buttonText}>{label}</Text>
  </Pressable>
);

const styles = StyleSheet.create({
  overlay: {
    position: 'absolute',
    top: 8,
    right: 8,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
    backgroundColor: 'rgba(200, 30, 30, 0.85)',
  },
  selfBadge: {
    position: 'absolute',
    top: 8,
    left: 8,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
    backgroundColor: 'rgba(30, 120, 200, 0.85)',
  },
  flattened: {position: 'absolute', left: 0, bottom: 0, width: 40, height: 40},
  overlayText: {color: 'white', fontWeight: '600', fontSize: 12},
  note: {color: 'white', marginHorizontal: 16, marginTop: 8},
  row: {flexDirection: 'row', marginHorizontal: 12, marginTop: 8},
  button: {
    flex: 1,
    marginHorizontal: 4,
    paddingVertical: 10,
    borderRadius: 6,
    backgroundColor: '#1f6feb',
    alignItems: 'center',
  },
  buttonText: {color: 'white', fontSize: 12, fontWeight: '600', textAlign: 'center'},
  log: {flex: 1, margin: 12},
  logLine: {color: '#ccc', fontSize: 11, fontFamily: Platform.select({ios: 'Menlo', android: 'monospace'})},
});
