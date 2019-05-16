package cauc.edu.cn.mobilesafer.activity.advancedTool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import cauc.edu.cn.mobilesafer.R;
/**
* FileName: ToolActivity <br>
* Description: 手机工具功能模块的主活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/5/16 10:03
*/
public class ToolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        // 初始化归属地itemView
        initQueryAddressView();
        // 初始化常用号码查询
        initCommonNumberView();
    }

    /**
     * 初始化归属地itemView
     */
    private void initQueryAddressView() {
        TextView phoneNumAddressQuery = (TextView) findViewById(R.id.phone_num_query);
        phoneNumAddressQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToolActivity.this, PhoneNumQueryActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化常用号码查询
     */
    private void initCommonNumberView() {
        TextView commonNumQuery = (TextView) findViewById(R.id.common_number_query);
        commonNumQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToolActivity.this, CommonNumberActivity.class);
                startActivity(intent);
            }
        });
    }
}
