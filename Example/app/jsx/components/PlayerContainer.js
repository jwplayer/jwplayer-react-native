import React from 'react';
import { StyleSheet, Text, Pressable, View, Dimensions } from 'react-native';
export const { height } = Dimensions.get('window');

/* styles */
import { globalStyles } from '../../ui/styles/global.style';

const styles = StyleSheet.create({
  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 22,
  },
  modalView: {
    marginTop: height / 3,
    height: 300
  },
  button: {
    borderRadius: 20,
  },
  buttonOpen: {
    backgroundColor: '#808080',
  },
  textStyle: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 18,
    margin: 30,
    textAlign: 'center',
  },
});

export default ({ children, text, onPress = null }) => {
  return (
    <View style={globalStyles.container}>
      <View style={globalStyles.subContainer}>
        <View style={globalStyles.playerContainer}>{children}</View>
      </View>
      <Text style={globalStyles.text}>{text}</Text>
      {onPress && <Pressable
        style={[styles.button, styles.buttonOpen]}
        onPress={onPress}>
        <Text style={styles.textStyle}>Button for onPress</Text>
      </Pressable>
      }
    </View>
  );
};
