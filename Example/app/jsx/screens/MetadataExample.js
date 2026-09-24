import React, {useState} from 'react';
import {
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  View,
} from 'react-native';
import Player from '../components/Player';
import {globalStyles} from '../../ui/styles/global.style';

// Public test streams that each carry a different kind of timed metadata.
// Every stream is wired to both `onMeta` and `onMetadataCueParsed`; the log
// below shows what arrives, in which phase, and with which `metadataType`.
const STREAMS = [
  {
    key: 'id3',
    label: 'ID3 (VOD)',
    hint:
      'Ad-stitched VOD carrying ID3 timed metadata (TXXX / PRIV frames) every few seconds. Expect `id3` on onMeta on both platforms and on onMetadataCueParsed on Android.',
    playlist: [
      {
        title: 'ID3 timed metadata',
        file: 'https://playertest-cdn.longtailvideo.com/adaptive/fox-yospace-id3/index.m3u8',
      },
    ],
  },
  {
    key: 'daterange',
    label: 'DATERANGE + PDT',
    hint:
      'VOD with EXT-X-PROGRAM-DATE-TIME and EXT-X-DATERANGE tags, plus an ID3 TXXX frame every 5s. Expect `program-date-time` and `date-range` when parsed and again as playback reaches each cue, and `id3` throughout. On iOS the date ranges arrive once the SDK knows the content start date, and this asset\'s variants disagree on it, so they can come late with large `start` values.',
    playlist: [
      {
        title: 'Date range metadata',
        file: 'https://playertest.longtailvideo.com/adaptive/bipbop_16x9/bipbop_16x9_variant_with_daterange.m3u8',
      },
    ],
  },
  {
    key: 'external',
    label: 'External',
    hint:
      'Cue points supplied in the config via `externalMetadata`. Only `identifier` is needed: the wrapper derives the integer `id` the Android SDK reads. Expect `external` at 5s, 15s and 30s.',
    playlist: [
      {
        title: 'External metadata cue points',
        file: 'https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4',
        externalMetadata: [
          {identifier: '1', startTime: 5, endTime: 10},
          {identifier: '2', startTime: 15, endTime: 20},
          {identifier: '3', startTime: 30, endTime: 35},
        ],
      },
    ],
  },
  ...(Platform.OS === 'android'
    ? [
        {
          key: 'scte',
          label: 'SCTE-35',
          hint:
            'VOD with SCTE-35 markers carried in EXT-X-DATERANGE attributes (SCTE35-OUT / SCTE35-IN). Look inside `metadata.attributes` of the `date-range` events. Android only: AVFoundation rejects this test manifest (it reuses a DATERANGE ID with different START-DATEs), so iOS stalls on it.',
          playlist: [
            {
              title: 'SCTE-35 metadata',
              file: 'https://playertest.longtailvideo.com/adaptive/bipbop_16x9/bipbop_16x9_variant_with_scte_tags.m3u8',
            },
          ],
        },
        {
          key: 'emsg',
          label: 'DASH emsg',
          hint:
            'DASH-IF live simulator with SCTE-35 emsg boxes. The iOS SDK does not expose emsg, so this stream is only listed on Android.',
          playlist: [
            {
              title: 'DASH event messages',
              file: 'https://livesim2.dashif.org/livesim2/scte35_2/testpic_2s/Manifest.mpd',
            },
          ],
        },
      ]
    : []),
];

// `access-log` (iOS) and `media` (Android, on every format change) are chatty;
// they are always counted but hidden from the log unless toggled on. The two
// buckets are trimmed separately so the chatty ones can never evict the cue
// events this screen exists to show.
const NOISY_TYPES = ['access-log', 'media'];
const MAX_LOG = 60;
const MAX_NOISY_LOG = 20;
const EMPTY_LOG = {events: [], counts: {}};

const isNoisy = ev => NOISY_TYPES.includes(ev.type);

const trimLog = events => {
  let kept = 0;
  let keptNoisy = 0;
  return events.filter(ev =>
    isNoisy(ev) ? keptNoisy++ < MAX_NOISY_LOG : kept++ < MAX_LOG,
  );
};

export default () => {
  const [streamKey, setStreamKey] = useState(STREAMS[0].key);
  const [log, setLog] = useState(EMPTY_LOG);
  const [showNoisy, setShowNoisy] = useState(false);

  const stream = STREAMS.find(s => s.key === streamKey) || STREAMS[0];

  const record = (phase, e) => {
    // Android also includes `message`; strip it so the log shows just the payload.
    const {message, ...payload} = e.nativeEvent || {};
    const type = payload.metadataType || 'unknown';
    const countKey = `${phase} ${type}`;
    const entry = {
      id: `${Date.now()}-${Math.random()}`,
      phase,
      type,
      time: new Date().toLocaleTimeString(),
      payload,
    };

    // One state update per event: the counts and the trimmed log move together.
    setLog(prev => ({
      events: trimLog([entry, ...prev.events]),
      counts: {...prev.counts, [countKey]: (prev.counts[countKey] || 0) + 1},
    }));
  };

  const onMeta = e => record('onMeta', e);
  const onMetadataCueParsed = e => record('onMetadataCueParsed', e);

  const selectStream = key => {
    setStreamKey(key);
    setLog(EMPTY_LOG);
  };

  const {events, counts} = log;
  const visibleEvents = showNoisy ? events : events.filter(ev => !isNoisy(ev));

  return (
    <View style={globalStyles.container}>
      <View style={globalStyles.subContainer}>
        <View style={globalStyles.playerContainer}>
          <Player
            // Remount the player when switching streams so each stream starts clean.
            tag={stream.key}
            style={{flex: 1}}
            config={{autostart: true, playlist: stream.playlist}}
            onMeta={onMeta}
            onMetadataCueParsed={onMetadataCueParsed}
          />
        </View>
      </View>

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.picker}>
        {STREAMS.map(s => (
          <Pressable
            key={s.key}
            onPress={() => selectStream(s.key)}
            style={[styles.chip, s.key === streamKey && styles.chipSelected]}>
            <Text
              style={[
                styles.chipText,
                s.key === streamKey && styles.chipTextSelected,
              ]}>
              {s.label}
            </Text>
          </Pressable>
        ))}
      </ScrollView>

      <Text style={styles.hint}>{stream.hint}</Text>

      <View style={styles.toolbar}>
        <Text style={styles.counts}>
          {Object.keys(counts).length === 0
            ? 'No metadata events yet.'
            : Object.entries(counts)
                .sort(([a], [b]) => a.localeCompare(b))
                .map(([k, v]) => `${k}: ${v}`)
                .join('   ')}
        </Text>
        <View style={styles.toggle}>
          <Text style={styles.toggleLabel}>Show media / access-log</Text>
          <Switch value={showNoisy} onValueChange={setShowNoisy} />
        </View>
        <Pressable style={styles.clear} onPress={() => setLog(EMPTY_LOG)}>
          <Text style={styles.clearText}>Clear</Text>
        </Pressable>
      </View>

      <ScrollView style={styles.log} contentContainerStyle={styles.logContent}>
        {visibleEvents.map(ev => (
          <View key={ev.id} style={styles.entry}>
            <Text style={styles.entryHeader}>
              <Text
                style={
                  ev.phase === 'onMeta'
                    ? styles.phaseMeta
                    : styles.phaseParsed
                }>
                {ev.phase}
              </Text>
              {'  '}
              <Text style={styles.entryType}>{ev.type}</Text>
              {'  '}
              <Text style={styles.entryTime}>{ev.time}</Text>
            </Text>
            <Text style={styles.entryBody}>
              {JSON.stringify(ev.payload, null, 2)}
            </Text>
          </View>
        ))}
      </ScrollView>
    </View>
  );
};

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

const styles = StyleSheet.create({
  picker: {
    paddingHorizontal: 12,
    paddingVertical: 10,
    gap: 8,
  },
  chip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
    backgroundColor: '#e6e6e6',
  },
  chipSelected: {
    backgroundColor: '#333',
  },
  chipText: {
    fontSize: 13,
    color: '#333',
  },
  chipTextSelected: {
    color: 'white',
    fontWeight: 'bold',
  },
  hint: {
    fontSize: 12,
    color: '#555',
    paddingHorizontal: 16,
    paddingBottom: 8,
  },
  toolbar: {
    paddingHorizontal: 16,
    paddingBottom: 6,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#ccc',
  },
  counts: {
    fontSize: 12,
    color: '#2a7',
    fontFamily: mono,
    marginBottom: 6,
  },
  toggle: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  toggleLabel: {
    fontSize: 12,
    color: '#333',
  },
  clear: {
    alignSelf: 'flex-end',
    paddingVertical: 4,
  },
  clearText: {
    fontSize: 12,
    color: '#c33',
  },
  log: {
    flex: 1,
  },
  logContent: {
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  entry: {
    marginBottom: 10,
  },
  entryHeader: {
    fontSize: 12,
    fontFamily: mono,
  },
  phaseMeta: {
    color: '#0a58ca',
    fontWeight: 'bold',
  },
  phaseParsed: {
    color: '#8a4b08',
    fontWeight: 'bold',
  },
  entryType: {
    color: '#333',
    fontWeight: 'bold',
  },
  entryTime: {
    color: '#999',
  },
  entryBody: {
    fontSize: 11,
    color: '#444',
    fontFamily: mono,
  },
});
