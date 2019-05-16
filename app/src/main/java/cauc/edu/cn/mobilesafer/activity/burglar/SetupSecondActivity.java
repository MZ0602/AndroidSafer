package cauc.edu.cn.mobilesafer.activity.burglar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import cauc.edu.cn.mobilesafer.util.ui.SettingsItem;
import cauc.edu.cn.mobilesafer.util.ConstantValues;
import cauc.edu.cn.mobilesafer.util.SharePreferenceUtil;
import cauc.edu.cn.mobilesafer.util.ToastUtil;
import cauc.edu.cn.mobilesafer.R;

/**
* FileName: SetupSecondActivity <br>
* Description: 防盗功能的SIM卡序列号绑定活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/17 23:27
*/
public class SetupSecondActivity extends BaseSetupActivity {

    // 是否绑定手机SIM卡的设置选项
    private SettingsItem mSettingsItemBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_second);
        // 初始化布局文件中View控件
        initView();
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     */
    @Override
    public void showNextPage() {
        String simSerialNumber = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(),
                ConstantValues.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(simSerialNumber)) {
            // 若从SharedPreferences中取出的SIM卡序列号不为空，则可以跳转到下一步
            Intent intent = new Intent(getApplicationContext(), SetupThirdActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 否者提示用户异常
            ToastUtil.show(getApplicationContext(), "必须先绑定SIM卡序列号");
        }
        // 开启平移动画效果
        overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     */
    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), SetupFirstActivity.class);
        startActivity(intent);
        finish();
        // 开启平移动画效果
        overridePendingTransition(R.anim.pre_in_anim, R.anim.next_out_anim);
    }

    /**
     * 初始化布局文件中View控件
     */
    private void initView() {
        mSettingsItemBind = (SettingsItem) findViewById(R.id.settings_item_bind);
        // 检查是否已存储了SIM卡序列号
        String simNumber = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(),
                ConstantValues.SIM_NUMBER, "");
        // 判断序列号是否为null
        if (TextUtils.isEmpty(simNumber)) {
            // 若为null mSettingsItemBind状态设置为false
            mSettingsItemBind.setCheck(false);
        } else {
            // 否则mSettingsItemBind状态设置为true
            mSettingsItemBind.setCheck(true);
        }
        mSettingsItemBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取mSettingsItemBind的原有状态
                boolean isChecked = mSettingsItemBind.IsCheck();
                // 点击后状态值取反
                mSettingsItemBind.setCheck(!isChecked);
                if (!isChecked) {
                    //READ_PHONE_STATE权限属于危险权限，需要在运行时动态申请
                    if (ContextCompat.checkSelfPermission(SetupSecondActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        //如果用户尚未授权此权限，则用requestPermissions函数向用户提交授权申请
                        ActivityCompat.requestPermissions(SetupSecondActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    } else {
                        //用户已授权过，直接获取SIM卡序列号，并存储到SharedPreferences中
                        saveSIM();
                    }
                } else {
                    // 若是未绑定状态，则从SharedPreferences中移除相应的Key值所对应的结点
                    SharePreferenceUtil.removeStringFromSharePreference(getApplicationContext(), ConstantValues.SIM_NUMBER);
                }
            }
        });
    }

    /**
     * 获取SIM卡序列号，并存储到SharedPreferences中
     */
    private void saveSIM() {
        // 如果是已绑定状态，则将SIM卡序列号存储到SharedPreferences中
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        // 获取SIM卡序列号，并存储到SharedPreferences中
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String simSerialNumber = telephonyManager.getSimSerialNumber();
        SharePreferenceUtil.putStringToSharePreference(getApplicationContext(),
                ConstantValues.SIM_NUMBER, simSerialNumber);
    }

    /**
     * 向用户提交动态授权申请后，自动对用户做出的授权决定做出应答
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //若用户同意将READ_PHONE_STATE权限授权给用户，则读取本机SIM卡序列号并进行存储
                    saveSIM();
                }else{
                    ToastUtil.show(this,"你拒绝了授权！");
                }
                break;
            default:
        }
    }
}
