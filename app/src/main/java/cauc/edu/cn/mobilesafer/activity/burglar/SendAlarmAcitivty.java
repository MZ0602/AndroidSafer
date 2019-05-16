package cauc.edu.cn.mobilesafer.activity.burglar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import cauc.edu.cn.mobilesafer.util.ConstantValues;
import cauc.edu.cn.mobilesafer.util.MyMobileApplication;
import cauc.edu.cn.mobilesafer.util.SharePreferenceUtil;
import cauc.edu.cn.mobilesafer.util.ToastUtil;

/**
 * FileName: SendAlarmAcitivty <br>
 * Description: 用于开机发现SIM卡发生变化时发送报警短信的活动 <br>
 * Author: 沈滨伟-13042299081 <br>
 * Date: 2019/4/18 15:19
 */
public class SendAlarmAcitivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SEND_SMS权限属于危险权限，需要在运行时动态申请
        if (ContextCompat.checkSelfPermission(MyMobileApplication.getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //如果用户尚未授权此权限，则用requestPermissions函数向用户提交授权申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            //用户已授权过且防盗功能已开启,直接发送报警短信
            if(true == SharePreferenceUtil.getBooleanFromSharePreference(this, ConstantValues.OPEN_SECURITY, false)){
                sendAlarm();
            }
        }
    }

    /**
     * 当检测到SIM卡发生变化，向安全号码发送报警短信
     */
    private void sendAlarm() {
        // 首先开机后获取手机SIM卡的序列号
        TelephonyManager tm = (TelephonyManager) MyMobileApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String simSimSerialnum = tm.getSimSerialNumber();
        String phoneNumber=tm.getLine1Number();
        // 然后获取已存储的原来的SIM卡序列号
        String savedSimSerialNum = SharePreferenceUtil.getStringFromSharePreference(MyMobileApplication.getContext(), ConstantValues.SIM_NUMBER, "");
        if (!simSimSerialnum.equals(savedSimSerialNum)) {
            //获取用户保存在本地的安全联系人号码
            String Safe_Contact_phone=SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(), ConstantValues.CONTACT_PHONE, "");
            // 不相同则向已绑定的手机安全号码发送报警短信
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Safe_Contact_phone, null, "【防盗报警短信】您号码为13042299081的关联手机已被盗！小偷已经替换了SIM卡（即此短信的发信号码）！", null, null);
        }
    }

    /**
     * 向用户提交动态授权申请后，自动对用户做出的授权决定做出应答
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //用户同意授权(发送短信)且防盗功能已开启,直接发送报警短信
                    if(true == SharePreferenceUtil.getBooleanFromSharePreference(this, ConstantValues.OPEN_SECURITY, false)){
                        sendAlarm();
                    }
                }else{
                    ToastUtil.show(this,"你拒绝了授权！");
                }
                break;
            default:
        }
    }
}
