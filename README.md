## react-native-gm-baidu-location
为 react-native 桥接的[百度定位 SDK](http://lbsyun.baidu.com/location/)，由 [react-native-baidu-location](https://github.com/1123746696/react-native-baidu-location) 改进而来，原来的库运行会报错，主要工作是增加了 API，以及升级 SDK 版本，时间问题保留了原库的一些代码逻辑。
- [x] Android SDK v7.5
- [x] IOS SDK v3.0

### 安装

```
npm install react-native-gm-baidu-location
react-native link react-native-gm-baidu-location
```

### 集成

#### Android

1. 在 `AndroidManifest.xml` 文件中，正确填写 AK

   ```xml
   <application>
   	<meta-data
           android:name="com.baidu.lbsapi.API_KEY"
           android:value="开发者申请的AK" >
   	</meta-data>
   </application>
   ```

2. 在 `AndroidManifest.xml` 中声明定位 service

   ```xml
   <application>
   	<service
   		android:name="com.baidu.location.f"
   		android:enabled="true"
   		android:process=":remote">
       </service>
   </application>
   ```

3. 在 `AndroidManifest.xml` 添加所需权限

   注意：针对Android 6.0 以上设备，定位等敏感权限还需要动态获取，见 [PermissionsAndroid](https://facebook.github.io/react-native/docs/permissionsandroid.html)

   ```xml
   <manifest>
       <!-- 这个权限用于进行网络定位-->
   	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   	<!-- 这个权限用于访问GPS定位-->
   	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   	<!-- 用于访问wifi网络信息，wifi信息会用于进行网络定位-->
   	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
   	<!-- 获取运营商信息，用于支持提供运营商信息相关的接口-->
   		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   	<!-- 这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
   	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
   	<!-- 用于读取手机当前的状态-->
   	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
   	<!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据-->
   	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   	<!-- 访问网络，网络定位需要上网-->
   	<uses-permission android:name="android.permission.INTERNET" />
   	<!-- SD卡读取权限，用户写入离线定位数据-->
   	<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
   </manifest>
   ```

详情参考[百度定位 Android Studio 集成指南](http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio)

### API

|               API               | Param  |                         Description                          | iOS  | Android |
| :-----------------------------: | :----: | :----------------------------------------------------------: | :--: | :-----: |
|             `init`              |   -    |                         初始定位实例                         |  ❌   |    ✅    |
|      ` setLocationOption`       |  `{}`  | 设置定位参数，详见：[setLocationOption](#`setLocationOption(option)`) |  ❌   |    ✅    |
|        ` startLocation`         |   -    |                           开始定位                           |  ✅   |    ✅    |
|       ` restartLocation`        |   -    |                         重新获取定位                         |  ❌   |    ✅    |
|         ` stopLocation`         |   -    |                           停止定位                           |  ✅   |    ✅    |
|    ` enableLocInForeground`     |   -    | 保持后台定位，详见：[Android 后台定位限制](http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/android8-notice) |  ❌   |    ✅    |
|   ` didUpdateBMKUserLocation`   | `func` |                         定位刷新回调                         |  ✅   |    ✅    |
|     ` didStopLocatingUser`      | `func` |                          停止定回调                          |  ✅   |    ✅    |
| ` didFailToLocateUserWithError` | `func` |                         定位失败回调                         |  ✅   |    ✅    |

### API Detail

##### `setLocationOption(option)`

- `option`

  | field                  | type | description                                                  |
  | :--------------------- | :--- | :----------------------------------------------------------- |
  | coorType               | Str  | 可选，设置返回经纬度坐标类型，默认gcj02<br /> gcj02：国测局坐标；<br /> bd09ll：百度经纬度坐标；<br /> bd09：百度墨卡托坐标；<br /> 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标 |
  | scanSpan               | Num  | 可 选，设置发起定位请求的间隔，int类型，单位ms               |
  | openGps                | Bool | 可选，设置是否使用gps，默认false                             |
  | ignoreKillProcess      | Bool | 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true) |
  | isNeedAddress          | Bool | 可选，设置是否需要地址信息，默认不需要                       |
  | isNeedLocationDescribe | Bool | 可选，默认false，设置是否需要位置语义化结果                  |

### Usage

```js
import BaiduLocation from 'react-native-baidu-location';

BaiduLocatioin.init();

BaiduLocation.setLocationOption({
        scanSpan: 5000
});

BaiduLocation.startLocation();

BaiduLocation.restartLocation();

BaiduLocation.stopLocation();

BaiduLocation.enableLocInForeground();

BaiduLocation.didUpdateBMKUserLocation(param => {
    console.log(param);
});

BaiduLocation.didStopLocatingUser(param => {
    console.log(param);
});

BaiduLocation.didFailToLocateUserWithError(param => {
    console.log(param);
});
```

