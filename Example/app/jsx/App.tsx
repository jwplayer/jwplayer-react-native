import React, {Component} from 'react';
import RNBootSplash from 'react-native-bootsplash';
import {SafeAreaProvider} from 'react-native-safe-area-context';

/* navigation */
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

/* screens */
import Home from './screens/Home';
import TypeScriptExample from './screens/TypeScriptExample';
import ListExample from './screens/ListExample';
import SingleExample from './screens/SingleExample';
import OnBeforeNextPlaylistItemExample from './screens/OnBeforeNextPlaylistItemExample';
import DRMExample from './screens/DRMExample';
import LocalFileExample from './screens/LocalFileExample';
import SourcesExample from './screens/SourcesExample';
import YoutubeExample from './screens/YoutubeExample';
import PlayerInModal from './screens/PlayerInModal';
import GlobalPlayerExample from './screens/GlobalPlayerExample';

const Stack = createNativeStackNavigator();

export default class App extends Component {
  render() {
    return (
      <SafeAreaProvider>
        <NavigationContainer onReady={() => RNBootSplash.hide({fade: true})}>
          <Stack.Navigator
            initialRouteName="Home"
            screenOptions={{
              contentStyle: {
                // React Navigation will automatically handle safe areas
              },
            }}
          >
            <Stack.Screen name="Home" component={Home} />
            <Stack.Screen name="Single" component={SingleExample} />
            <Stack.Screen name="On Before Next Playlist Item" component={OnBeforeNextPlaylistItemExample} />
            <Stack.Screen name="Modal" component={PlayerInModal} />
            <Stack.Screen name="List" component={ListExample} />
            <Stack.Screen name="DRM" component={DRMExample} />
            <Stack.Screen name="Local" component={LocalFileExample} />
            <Stack.Screen name="Sources" component={SourcesExample} />
            <Stack.Screen name="Youtube" component={YoutubeExample} />
            <Stack.Screen name="Global Player" component={GlobalPlayerExample} />
          </Stack.Navigator>
        </NavigationContainer>
      </SafeAreaProvider>
    );
  }
}
