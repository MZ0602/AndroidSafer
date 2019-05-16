package cauc.edu.cn.mobilesafer.activity.advancedTool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cauc.edu.cn.mobilesafer.db.NumberAddressDao;
import cauc.edu.cn.mobilesafer.R;
import cauc.edu.cn.mobilesafer.util.ToastUtil;

/**
* FileName: PhoneNumQueryActivity <br>
* Description: 号码归属地查询活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/5/16 9:06
*/
public class PhoneNumQueryActivity extends AppCompatActivity {
    private static final String tag = "PhoneNumQueryActivity";
    // 输入查询号码的文本编辑框
    private EditText inputPhoneNumView;
    // 显示查询到的电话号码地址
    private TextView showPhoneNumAddressView;
    // 查询电话号码地址按钮
    private Button queryPhoneNumButton;
    // 待查询的电话号码信息
    private String phoneNumber = "";
    // 查询返回的电话号码地址信息
    private String phoneNumAddress = "未知号码";

    //消息异步处理机制。用于处理子线程反馈回来的结果
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showPhoneNumAddressView.setText("归属地：" + phoneNumAddress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num_query);
        // 初始化布局文件中的View
        initView();
        queryPhoneNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = inputPhoneNumView.getText().toString().trim();
                Log.i(tag, "phoneNumber:" + phoneNumber);
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtil.show(PhoneNumQueryActivity.this,"您尚未输入任何号码！");
                    showPhoneNumAddressView.setText("归属地:" + "");
                } else {
                    // 查询电话号码的号码归属地
                    query(phoneNumber);
                }
            }
        });
        inputPhoneNumView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            //动态监听号码输入框是否有变化，并动态地输出查询结果
            public void afterTextChanged(Editable s) {
                String phoneNum = s.toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    showPhoneNumAddressView.setText("归属地:" + "");
                } else {
                    query(phoneNum);
                }
            }
        });
    }

    /**
     * 查询电话号码的号码归属地
     *
     * @param phoneNum 待查询的电话号码
     */
    private void query(final String phoneNum) {
        //查询号码归属地属于耗时活动，启动子线程执行任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                phoneNumAddress = NumberAddressDao.getPhoneAddress(phoneNum);
                Log.i(tag, "phoneNumAddress:" + phoneNumAddress);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        inputPhoneNumView = (EditText) findViewById(R.id.input_query_phone_number);
        showPhoneNumAddressView = (TextView) findViewById(R.id.show_phone_address_info);
        queryPhoneNumButton = (Button) findViewById(R.id.query_phone_num_btn);
    }
}
