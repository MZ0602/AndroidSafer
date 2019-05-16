package cauc.edu.cn.mobilesafer.activity.burglar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cauc.edu.cn.mobilesafer.util.ConstantValues;
import cauc.edu.cn.mobilesafer.util.SharePreferenceUtil;
import cauc.edu.cn.mobilesafer.util.ToastUtil;
import cauc.edu.cn.mobilesafer.R;

/**
* FileName: SetupThirdActivity <br>
* Description: 防盗功能的安全账号设定活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/17 23:29
*/
public class SetupThirdActivity extends BaseSetupActivity {

    // 用户输入电话号码对话框
    private EditText mInputNumText;
    // 选择电话号码按钮
    private Button mSelectNumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_third);
        // 初始化布局文件中的View控件
        initView();
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     */
    @Override
    public void showNextPage() {
        String inputPhone = mInputNumText.getText().toString();
        // 如果输入文本框中获取的电话号码值不为null，则跳转到下一步
        if (!TextUtils.isEmpty(inputPhone)) {
            Intent intent = new Intent(getApplicationContext(), SetupFourthActivity.class);
            startActivity(intent);
            finish();
            // 若用户手动输入电话号码，也要进行存储
            SharePreferenceUtil.putStringToSharePreference(getApplicationContext(), ConstantValues.CONTACT_PHONE, inputPhone);
        } else {
            // 否则，提示用户输入
            ToastUtil.show(this, "请输入或选择电话号码");
        }
        // 开启平移动画效果
        overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     */
    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), SetupSecondActivity.class);
        startActivity(intent);
        finish();
        // 开启平移动画效果
        overridePendingTransition(R.anim.pre_in_anim, R.anim.next_out_anim);
    }

    /**
     * 初始化布局文件中的View控件
     */
    private void initView() {
        mInputNumText = (EditText) findViewById(R.id.text_safe_num);
        // 如果用户选择后退重新选择电话号码，回显已选中或已输入的电话号码
        String phoneNum = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(), ConstantValues.CONTACT_PHONE, "");
        mInputNumText.setText(phoneNum);
        mSelectNumButton = (Button) findViewById(R.id.select_safe_num);
        //如果用户点击“选择安全联系人”的按钮，则从手机系统通讯录里面读取联系人列表供用户选择
        mSelectNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * 处理从上一级活动(从系统联系人选中手机防盗安全联系人)销毁前返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据返回值内容进行下面的逻辑
        if (data != null) {
            String selectNum = data.getStringExtra("phone");
            // 替换掉selectNum中的空格和-等
            selectNum = selectNum.replace("-", "").replace(" ", "").trim();
            mInputNumText.setText(selectNum);
            // 存储已选中的联系人电话号码
            SharePreferenceUtil.putStringToSharePreference(getApplicationContext(), ConstantValues.CONTACT_PHONE, selectNum);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
