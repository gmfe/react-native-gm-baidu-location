package com.example.rctbaidulocation;

import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;


import java.util.HashMap;
import java.util.Map;

public class BaiduLocationModule extends ReactContextBaseJavaModule {
    protected static final String TAG = BaiduLocationModule.class.getSimpleName();
    protected static final String WillStartLocatingUser = "WillStartLocatingUser";
    protected static final String DidStopLocatingUser = "DidStopLocatingUser";

    protected static final String DidUpdateUserHeading = "DidUpdateUserHeading";
    protected static final String DidUpdateBMKUserLocation = "DidUpdateBMKUserLocation";
    protected static final String DidFailToLocateUserWithError = "DidFailToLocateUserWithError";

    private ReactApplicationContext mReactContext;
    private LocationClientOption option = null;
    private LocationClient mLocationClient = null;

    @Override
    public String getName() {
        return "BaiduLocation";
    }

    public BaiduLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DidStopLocatingUser, DidStopLocatingUser);
        constants.put(DidUpdateBMKUserLocation, DidUpdateBMKUserLocation);
        constants.put(DidFailToLocateUserWithError, DidFailToLocateUserWithError);
        return constants;
    }

    @ReactMethod
    public void init() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(mReactContext.getBaseContext());     //声明LocationClient类
            mLocationClient.setLocOption(getDefaultLocationClientOption());
        }
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                    sendSuccessEvent(bdLocation);
                } else {
                    sendFailureEvent(bdLocation);
                }
            }
        });
    }

    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    private LocationClientOption getDefaultLocationClientOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
        option.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        return option;
    }

    @ReactMethod
    public void setLocationOption(ReadableMap optionMap) {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType(optionMap.getString("coorType"));
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(optionMap.getInt("scanSpan"));
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(optionMap.getBoolean("openGps"));
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setIgnoreKillProcess(optionMap.getBoolean("ignoreKillProcess"));
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.setIsNeedAddress(optionMap.getBoolean("isNeedAddress"));
        //可选，设置是否需要地址信息，默认不需要

        option.setIsNeedLocationDescribe(optionMap.getBoolean("isNeedLocationDescribe"));
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    @ReactMethod
    public void startLocation() {
        mLocationClient.start();
    }

    public void restartLocation() {
        mLocationClient.restart();
    }

    @ReactMethod
    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            sendDidStopEvent();
        }
    }

    protected void sendSuccessEvent(BDLocation location) {
        WritableMap map = Arguments.createMap();
        map.putDouble("latitude", location.getLatitude());
        map.putDouble("longitude", location.getLongitude());
        map.putString("address", location.getAddrStr());
        map.putString("province", location.getProvince());
        map.putString("city", location.getCity());
        map.putString("district", location.getDistrict());
        map.putString("streetName", location.getStreet());
        map.putString("streetNumber", location.getStreetNumber());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            map.putString("describe", "gps定位成功");
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            map.putString("describe", "网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            map.putString("describe", "离线定位成功");
        }
        map.putString("locationDescribe", location.getLocationDescribe());
        sendEvent(DidUpdateBMKUserLocation, map);
    }

    protected void sendFailureEvent(BDLocation location) {
        WritableMap map = Arguments.createMap();
        map.putInt("code", BDLocation.TypeServerError);
        if (location.getLocType() == BDLocation.TypeServerError) {
            map.putString("message", "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            map.putString("message", "网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            map.putString("message", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        } else {
            map.putString("message", "定位失败");
        }
        map.putString("locationDescribe", location.getLocationDescribe());
        sendEvent(DidFailToLocateUserWithError, map);
    }

    protected void sendDidStopEvent() {
        WritableMap map = Arguments.createMap();
        map.putString("message", "停止定位");
        sendEvent(DidStopLocatingUser, map);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        //此处需要添加hasActiveCatalystInstance，否则可能造成崩溃
        //问题解决参考: https://github.com/walmartreact/react-native-orientation-listener/issues/8
        if (mReactContext.hasActiveCatalystInstance()) {
            Log.i(TAG, "hasActiveCatalystInstance");
            mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } else {
            Log.i(TAG, "not hasActiveCatalystInstance");
        }
    }
}
