package cauc.edu.cn.mobilesafer.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
/**
* FileName: DeviceAdmin <br>
* Description: 防盗模块用于获得手机超级管理员权限的广播接收器 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/29 10:52
*/
public class DeviceAdmin extends DeviceAdminReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
