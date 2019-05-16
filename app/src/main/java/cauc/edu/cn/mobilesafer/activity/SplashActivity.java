package cauc.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import cauc.edu.cn.mobilesafer.R;
/**
* FileName: SplashActivity <br>
* Description: 启动程序时的初始化活动(拷贝数据库、申请全部动态权限) <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/18 23:09
*/
public class SplashActivity extends AppCompatActivity {
    private static final String tag = "SplashActivity";
    // 应用版本需要升级时发送消息的标志
    private static final int UPDATE_MSG = 100;
    // 不需要升级应用版本直接进入应用主界面是发送消息的标志
    private static final int HOME_MSG = 101;
    // SplashActivity界面的根布局
    private RelativeLayout mRelativeLayout;
    // 文本框：用于显示版本名称
    private TextView mTextView;
    private PackageManager mPackageManager;
    // 本地安装的应用版本的版本号
    private int mLocalVersionCode;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HOME_MSG:
                    // 进入应用程序的主界面
                    enterHomeActivity();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 初始化视图View
        initView();
        // 初始化数据Data
        initData();
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        mRelativeLayout.startAnimation(animation);
        // 初始化数据库
        initDB();
    }

    /**
     * 初始化数据Data
     */
    private void initData() {
        // 获取当前应用版本的版本名称
        mTextView.setText("版本名称：" + getVersionName());
        // 获取本地的当前版本的版本号
        mLocalVersionCode = getVersionCode();
        //延迟三秒钟进入主界面
        mHandler.sendEmptyMessageDelayed(HOME_MSG, 3000);
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        // 初始化来电归属地数据库,并将数据库拷贝到应用Files下
        copyDB("address.db");
        // 初始化常用号码数据库,并将数据库拷贝到应用Files下
        copyDB("commonnum.db");
        // 初始化病毒数据库,并将数据库拷贝到应用Files下
        copyDB("antivirus.db");
    }

    /**
     * 初始化来电归属地数据库,并将数据库拷贝到应用Files下
     *
     * @param databaseName 数据库的名字
     */
    private void copyDB(String databaseName) {
        // 获取应用所在的文件路径
        File filePath = getFilesDir();
        // 创建一个新的文件
        File file = new File(filePath, databaseName);
        // 文件若已存在，则退出数据库的拷贝过程
        if (file.exists() && file.length() > 0) {
            return;
        } else {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            try {
                // 打开assets文件目录下的数据库文件
                inputStream = getAssets().open(databaseName);
                fos = new FileOutputStream(file);
                byte[] byteBuffer = new byte[1024];
                int temp = -1;
                while ((temp = inputStream.read(byteBuffer)) != -1) {
                    // 读取到的数据流写入到指定的文件中
                    Log.i(tag, "temp = " + temp);
                    fos.write(byteBuffer, 0, temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null && fos != null) {
                    try {
                        inputStream.close();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 进入应用程序的主界面
     */
    private void enterHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHomeActivity();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取当前版本的版本名称
     *
     * @return 返回值为空，则获取失败，否则返回当前版本的版本名称
     */
    private String getVersionName() {
        mPackageManager = getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前版本的版本号
     *
     * @return 返回值为0，则获取失败，否则返回当前版本的版本号
     */
    private int getVersionCode() {
        mPackageManager = getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 初始化视图View
     */
    private void initView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        mTextView = (TextView) findViewById(R.id.text_version_name);
    }
}
