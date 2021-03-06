package com.example.rctbaidulocation;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.react.bridge.Arguments;
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
    private BDLocationListener listener = new BDLocationListener();
    private LocationClient mLocationClient = null;
    private NotificationUtils mNotificationUtils;
    private Notification notification;

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
        }
        mLocationClient.registerLocationListener(listener);

        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationUtils = new NotificationUtils(mReactContext);
            Notification.Builder builder2 = mNotificationUtils.getAndroidChannelNotification
                    ("适配android 8限制后台定位功能", "正在后台定位");
            notification = builder2.build();
        } else {
            //获取一个Notification构造器
            Notification.Builder builder = new Notification.Builder(mReactContext.getBaseContext());
            Intent nfIntent = new Intent();

            builder.setContentTitle("后台定位功能") // 设置下拉列表里的标题
                    .setSmallIcon(R.drawable.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("正在后台定位") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            notification = builder.build(); // 获取构建好的Notification
        }
    }

    private class BDLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                sendSuccessEvent(bdLocation);
            } else {
                sendFailureEvent(bdLocation);
            }
        }
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
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }

    public void restartLocation() {
        if (mLocationClient != null) {
            mLocationClient.restart();
        }
    }

    @ReactMethod
    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            sendDidStopEvent();
        }
    }

    @ReactMethod
    public void enableLocInForeground(boolean b) {
        if (b) {
            mLocationClient.enableLocInForeground(1, notification);
        } else {
            mLocationClient.disableLocInForeground(b);
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
