import React, {Component} from 'react';

/* navigation */
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

/* screens */
import Home from './screens/Home';
import SingleExample from './screens/SingleExample';

const Stack = createNativeStackNavigator();

export default class App extends Component {
  render() {
    return (
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Home">
          <Stack.Screen name="Home" component={Home} />
          <Stack.Screen name="Single" component={SingleExample} />
        </Stack.Navigator>
      </NavigationContainer>
    );
  }
}
