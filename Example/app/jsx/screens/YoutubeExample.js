import React, {useState} from 'react';
import {
  StyleSheet,
  View,
  Text,
  FlatList,
  TouchableOpacity,
  Image,
  Platform,
} from 'react-native';
import {useSafeAreaInsets} from 'react-native-safe-area-context';
import Player from '../components/AnimatedPlayer';

export default () => {
  const [isVisible, setIsVisible] = useState(false);
  const [playerItem, setPlayerItem] = useState(null);
  const [playerKey, setPlayerKey] = useState(0);
  const insets = useSafeAreaInsets();
  const data = [
    {
      title: 'JWPlayer-1',
      subtitle: 'Subtitle 1',
      file: 'http://content.bitsontherun.com/videos/bkaovAYt-injeKYZS.mp4',
      image: 'https://d3el35u4qe4frz.cloudfront.net/bkaovAYt-480.jpg',
      duration: 33,
      startTime: 10,
      author: 'Some Guy',
      series: 'Youtube sample 1',
    },
    {
      title: 'JWPlayer-2',
      subtitle: 'Subtitle 2',
      file: 'http://content.bitsontherun.com/videos/bkaovAYt-kNspJqnJ.mp4',
      image: 'https://d3el35u4qe4frz.cloudfront.net/bkaovAYt-480.jpg',
      duration: 33,
      author: 'Some other Guy',
      series: 'Youtube sample 2',
    },
    {
      title: 'JWPlayer-3',
      subtitle: 'Subtitle 3',
      file: 'https://playertest.longtailvideo.com/adaptive/oceans/oceans.m3u8',
      image: 'https://d3el35u4qe4frz.cloudfront.net/bkaovAYt-480.jpg',
      duration: 2000,
      author: 'A third Guy',
      series: 'Youtube sample 3',
    },
  ];

  return (
    <View style={{flex: 1, backgroundColor: 'white'}}>
      <FlatList
        contentContainerStyle={[
          styles.flatList,
          Platform.OS === 'android' && {paddingBottom: insets.bottom},
        ]}
        keyExtractor={({title}, index) => `${title}-${index}`}
        data={data}
        renderItem={({item, index}) => (
          <TouchableOpacity
            onPress={() => {
              // Don't reload if it's the same item
              if (playerItem?.file === item.file) {
                setIsVisible(true);
                return;
              }

              if (Platform.OS === 'android') {
                // On Android, force component recreation by setting to null first
                setPlayerItem(null);
                setIsVisible(false);
                // Then set new item in next frame
                setTimeout(() => {
                  setPlayerItem(item);
                  setPlayerKey(prev => prev + 1);
                  setIsVisible(true);
                }, 0);
              } else {
                // On iOS, update directly and increment key to trigger recreation
                setPlayerItem(item);
                setPlayerKey(prev => prev + 1);
                setIsVisible(true);
              }
            }}
            style={styles.itemContainer}>
            <Image style={styles.image} source={{uri: item?.image}} />
            <View style={styles.textContainer}>
              <Text style={styles.text}>{item?.title}</Text>
              <Text style={styles.subtext}>{item?.subtitle}</Text>
            </View>
          </TouchableOpacity>
        )}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
      />
      {isVisible && playerItem && (
        <Player 
          key={`player-${playerKey}`}
          {...playerItem} 
          setIsVisible={setIsVisible} 
          bottomInset={insets.bottom} 
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  itemContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  image: {
    height: 50,
    width: 50,
    resizeMode: 'contain',
    // backgroundColor: 'lightgray',
  },
  textContainer: {
    margin: 20,
  },
  text: {
    fontSize: 18,
  },
  subtext: {
    fontSize: 15,
  },
  flatList: {

  },
  separator: {
    height: 1,
    backgroundColor: 'lightgray',
  },
});
