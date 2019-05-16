package cauc.edu.cn.mobilesafer.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import cauc.edu.cn.mobilesafer.util.ConstantValues;
import cauc.edu.cn.mobilesafer.util.MyMobileApplication;
import cauc.edu.cn.mobilesafer.util.SharePreferenceUtil;
/**
* FileName: LocationService <br>
* Description: 用于防盗模块向安全号码发送位置信息的服务 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/23 19:19
*/
public class LocationService extends Service {
    public LocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 最优的获取位置信息的方式
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        // 权限检测
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //设置当手机所处位置变动超过100米，自动向安全手机再发送一次地理位置变更信息短信
        locationManager.requestLocationUpdates(bestProvider, 0, 100, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 获取当前的经纬度信息
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // 读取手机安全联系人号码
                String selectedSecurityNum = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(),
                        ConstantValues.CONTACT_PHONE, "");
                // 发送当前的经纬度信息到绑定的安全号码上（需要手机打开GPS定位）
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(selectedSecurityNum, null, "您被盗的手机目前所在的纬度为：" + latitude + ",经度为：" + longitude, null, null);
                //停止位置短信发送服务
                stopService(new Intent(MyMobileApplication.getContext(),LocationService.class));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
