import React, { useRef, useState } from 'react';
import Player from '../components/Player';
import PlayerContainer from '../components/PlayerContainer';
import { Alert, Modal, StyleSheet, Text, Pressable, View, StatusBar, Dimensions } from 'react-native';
export const { height } = Dimensions.get('window');


export default () => {
    const playerRef = useRef([]);

    const [modalVisible, setModalVisible] = useState(false);

    let jwConfig = {
        "title": "Single Inline Linear Preroll",
        "fullScreenOnLandscape": true,
        "landscapeOnFullScreen": true,
        "playlist": [
            {
                "file": "https://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4",
                "tracks": [
                    {
                        file: "https://cdn.jwplayer.com/strips/xzxKu91o-120.vtt",
                        label: 'track1',
                        default: true,
                        kind: "thumbnails",
                    }
                ]
            }
        ]
    }

    return (
        <View style={styles.centeredView}>
            <Modal
                animationType="fade"
                transparent={true}
                visible={modalVisible}
                onRequestClose={() => {
                    setModalVisible(!modalVisible);
                }}>
                <View style={styles.modalView}>
                    <Player
                        ref={playerRef}
                        style={{ flex: 1 }}
                        config={{
                            autostart: true,
                            playerInModal: true,
                            styling: {
                                colors: {},
                            },
                            ...jwConfig
                        }}
                    />
                </View>
                <Pressable
                    style={[styles.button, styles.buttonClose]}
                    onPress={() => setModalVisible(false)}>
                    <Text style={styles.textStyle}>Hide Modal</Text>
                </Pressable>
            </Modal>
            <Pressable
                style={[styles.button, styles.buttonOpen]}
                onPress={() => setModalVisible(true)}>
                <Text style={styles.textStyle}>Show Modal</Text>
            </Pressable>
            
        </View>
    );
};
const styles = StyleSheet.create({
    centeredView: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 22,
    },
    modalView: {
        marginTop:height / 3,
        height: 300
    },
    button: {
        borderRadius: 20,
        padding: 10,
        elevation: 2,
    },
    buttonOpen: {
        backgroundColor: '#F194FF',
    },
    buttonClose: {
        backgroundColor: '#2196F3',
    },
    textStyle: {
        color: 'white',
        fontWeight: 'bold',
        textAlign: 'center',
    },
});