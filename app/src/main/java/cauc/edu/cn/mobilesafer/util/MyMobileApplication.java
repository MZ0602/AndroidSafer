package cauc.edu.cn.mobilesafer.util;

import android.app.Application;
import android.content.Context;

import org.xutils.x;

import cauc.edu.cn.mobilesafer.BuildConfig;

/**
* FileName: MyMobileApplication <br>
* Description: 便于非Acitivity的类获取全局Context的工具类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/18 15:07
*/

public class MyMobileApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }

    /**
     * 静态方法，便于任何类随时获取全局Context
     * @return
     */
    public static  Context getContext(){
        return context;
    }
}
