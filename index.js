import React, {
    NativeModules,
    DeviceEventEmitter, //android
    NativeAppEventEmitter, //ios
    Platform,
    AppState,
} from 'react-native';

const BaiduLocationModule = NativeModules.BaiduLocation;

var didStopLocatingUserSubscript,
    didUpdateBMKUserLocationSubscript,
    didFailToLocateUserWithErrorSubscript;

var Index = {
    init(){
        BaiduLocationModule.init();
    },
    setLocationOption(option){
        BaiduLocationModule.setLocationOption(Object.assign({
            coorType: 'bd09ll',
            scanSpan: 5000,
            openGps: true,

            ignoreKillProcess: false,
            isNeedAddress: true,
            isNeedLocationDescribe: true
        }, option))
    },
    startLocation(){
        BaiduLocationModule.startLocation();
    },
    restartLocation() {
        BaiduLocationModule.restartLocation();
    },
    stopLocation(){
        BaiduLocationModule.stopLocation();
    },
    enableLocInForeground(bol) {
        BaiduLocationModule.enableLocInForeground(bol);
    },

    didStopLocatingUser(handler: Function){
        didStopLocatingUserSubscript = this.addEventListener(BaiduLocationModule.DidStopLocatingUser, handler);
    },
    didUpdateBMKUserLocation(handler: Function){
        didUpdateBMKUserLocationSubscript = this.addEventListener(BaiduLocationModule.DidUpdateBMKUserLocation, message => {
            handler(message);
        });
    },
    didFailToLocateUserWithError(handler: Function){
        didFailToLocateUserWithErrorSubscript = this.addEventListener(BaiduLocationModule.DidFailToLocateUserWithError, handler);
    },
    addEventListener(eventName: string, handler: Function) {
        if(Platform.OS === 'android') {
            return DeviceEventEmitter.addListener(eventName, (event) => {
                handler(event);
            });
        }
        else {
            return NativeAppEventEmitter.addListener(
                eventName, (userInfo) => {
                    handler(userInfo);
                });
        }
    },
};

module.exports = Index;
