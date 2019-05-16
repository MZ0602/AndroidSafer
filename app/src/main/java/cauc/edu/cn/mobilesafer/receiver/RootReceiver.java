package cauc.edu.cn.mobilesafer.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cauc.edu.cn.mobilesafer.activity.burglar.SendAlarmAcitivty;
import cauc.edu.cn.mobilesafer.util.MyMobileApplication;

/**
* FileName: RootReceiver <br>
* Description: 用于接收开机广播的接收器 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/18 16:23
*/
public class RootReceiver extends BroadcastReceiver {
    private static final String tag = "RootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag, "重启手机成功");
        Intent hhhintent = new Intent(MyMobileApplication.getContext(), SendAlarmAcitivty.class);
        MyMobileApplication.getContext().startActivity(hhhintent);
    }
}
