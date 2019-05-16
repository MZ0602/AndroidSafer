package cauc.edu.cn.mobilesafer.activity.burglar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cauc.edu.cn.mobilesafer.receiver.DeviceAdmin;
import cauc.edu.cn.mobilesafer.service.LocationService;
import cauc.edu.cn.mobilesafer.util.ConstantValues;
import cauc.edu.cn.mobilesafer.util.MyMobileApplication;
import cauc.edu.cn.mobilesafer.util.SharePreferenceUtil;
import cauc.edu.cn.mobilesafer.util.ToastUtil;
import cauc.edu.cn.mobilesafer.R;

/**
* FileName: SetupOverActivity <br>
* Description: 手机防盗功能主引导界面活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/17 15:39
*/
public class SetupOverActivity extends AppCompatActivity {

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setupOver = SharePreferenceUtil.getBooleanFromSharePreference(getApplicationContext(),
                ConstantValues.SETUP_OVER, false);
        boolean highpermision = SharePreferenceUtil.getBooleanFromSharePreference(getApplicationContext(),
                ConstantValues.HAS_HIGH_PERMISSION, false);
        //查看防盗功能用户是否已经设置过了
        if (setupOver) {
            setContentView(R.layout.activity_setup_over);
            // 初始化布局文件中的View
            initView();
            if(!highpermision){
                //获取手机超级管理员权限，用于获得远程锁屏、清除数据的能力
                devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                componentName = new ComponentName(this, DeviceAdmin.class);
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活超级管理员中");
                startActivity(intent);
                SharePreferenceUtil.putBooleanToSharePreference(MyMobileApplication.getContext(),ConstantValues.HAS_HIGH_PERMISSION,true);
            }
        } else {
            //用于尚未设置过，进入初始设置页面
            Intent intent = new Intent(getApplicationContext(), SetupFirstActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        TextView selectedSecurityText = (TextView) findViewById(R.id.selected_security_num);
        // 读取并设置手机安全号码
        String selectedSecurityNum = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(),
                ConstantValues.CONTACT_PHONE, "");
        selectedSecurityText.setText(selectedSecurityNum);
        TextView selectedText = (TextView) findViewById(R.id.isOpenSafe);
        ImageView selectedPic = (ImageView ) findViewById(R.id.open_security_lock);
        //查看用户手机防盗功能是否开启，并显示
        boolean setupOver = SharePreferenceUtil.getBooleanFromSharePreference(getApplicationContext(),
                ConstantValues.OPEN_SECURITY, false);
        if(setupOver){
            selectedText.setText("防盗功能已开启");
            selectedPic.setImageResource(R.drawable.unlock);
        }else{
            selectedPic.setImageResource(R.drawable.lock);
        }
        selectedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(SetupOverActivity.this,"请点击“重新设置”按键进行设置！");
            }
        });
        selectedPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(SetupOverActivity.this,"请点击“重新设置”按键进行设置！");
            }
        });
        //对“重新设置”功能键的点击事件进行设置
        Button resetSetup=(Button) findViewById(R.id.reset_setup);
        resetSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击重新进入设置向导
                Intent intent = new Intent(getApplicationContext(), SetupFirstActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //对“报警测试”功能键的点击事件进行设置
        Button testAlarm=(Button) findViewById(R.id.test_alarm);
        testAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SEND_SMS权限属于危险权限，需要在运行时动态申请
                if (ContextCompat.checkSelfPermission(MyMobileApplication.getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    //如果用户尚未授权此权限，则用requestPermissions函数向用户提交授权申请
                    ActivityCompat.requestPermissions(SetupOverActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                } else {
                    //用户已授权过,直接发送报警短信
                    sendAlarm();
                }
            }
        });
    }

    /**
     * 向安全号码发送报警测试短信
     */
    private void sendAlarm() {
        //获取用户保存在本地的安全联系人号码
        String Safe_Contact_phone=SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(), ConstantValues.CONTACT_PHONE, "");
        // 不相同则向已绑定的手机安全号码发送报警短信
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Safe_Contact_phone, null, "【防盗报警短信】您手机已被盗！这是一条报警测试短信！", null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //弹出“已成功发送测试短信”的提示框
        builder.setMessage("已向您的安全联系人号码"+Safe_Contact_phone+"发送了报警测试信息！")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * 向用户提交动态授权申请后，自动对用户做出的授权决定做出应答
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //若用户同意将SEND_SMS权限授权给用户
                    sendAlarm();
                }else{
                    ToastUtil.show(this,"你拒绝了授权！");
                }
                break;
            default:
        }
    }
}
