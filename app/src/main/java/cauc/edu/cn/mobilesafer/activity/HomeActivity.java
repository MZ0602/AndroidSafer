package cauc.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import cauc.edu.cn.mobilesafer.activity.appManager.AppManagerActivity;
import cauc.edu.cn.mobilesafer.activity.advancedTool.ToolActivity;
import cauc.edu.cn.mobilesafer.activity.blackNumber.CommunicationGuardActivity;
import cauc.edu.cn.mobilesafer.activity.cacheClear.BaseOptimizeClearActivity;
import cauc.edu.cn.mobilesafer.activity.virusCheck.AntiVirusActivity;
import cauc.edu.cn.mobilesafer.util.ConstantValues;
import cauc.edu.cn.mobilesafer.util.MD5Util;
import cauc.edu.cn.mobilesafer.util.SharePreferenceUtil;
import cauc.edu.cn.mobilesafer.util.ToastUtil;
import cauc.edu.cn.mobilesafer.R;
import cauc.edu.cn.mobilesafer.activity.burglar.SetupOverActivity;
/**
* FileName: HomeActivity <br>
* Description: 系统的功能主界面 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/17 10:30
*/
public class HomeActivity extends AppCompatActivity {
    // 应用主界面九宫格的布局
    private GridView mGridView;
    // 应用主界面九宫格中每个子项的标题
    private String[] mNameList;
    // 应用主界面九宫格中每个子项的图标
    private int[] mIconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // 初始化Activity中的视图
        initView();
        // 初始化数据
        initDate();
    }

    /**
     * 初始化Activity中的视图
     */
    private void initView() {
        mGridView = (GridView) findViewById(R.id.home_grid_view);
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        mNameList = new String[]{ "手机防盗","通讯卫士", "手机杀毒",
                "软件管理","缓存清理","手机工具"};
        mIconList = new int[]{ R.drawable.anti_theft, R.drawable.communication_guards, R.drawable.antivirus,
               R.drawable.process_manage,R.drawable.sys_optimize,R.drawable.advanced_tools};
        // GridView添加适配器
        mGridView.setAdapter(new MyAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 点击进入手机防盗模块，根据逻辑打开相应对话框
                        showMobileSaferDialog();
                        break;
                    case 1:
                        // 点击进入通讯卫士模块
                        Intent intentCommunication = new Intent(HomeActivity.this, CommunicationGuardActivity.class);
                        startActivity(intentCommunication);
                        break;
                    case 2:
                        // 点击进入杀毒模块
                        Intent intentAntiVirus = new Intent(HomeActivity.this, AntiVirusActivity.class);
                        startActivity(intentAntiVirus);
                        break;
                    case 3:
                        // 点击进入软件管理模块
                        Intent intentAppManager = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intentAppManager);
                        break;
                    case 4:
                        // 点击进入缓存清理模块;
                        Intent intentOptimizeClear = new Intent(HomeActivity.this, BaseOptimizeClearActivity.class);
                        startActivity(intentOptimizeClear);
                        break;
                    case 5:
                        // 点击进入手机工具选项
                        Intent intentTool = new Intent(HomeActivity.this, ToolActivity.class);
                        startActivity(intentTool);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 点击进入手机防盗模块，根据逻辑打开相应对话框
     */
    private void showMobileSaferDialog() {
        String psd = SharePreferenceUtil.getStringFromSharePreference(this, ConstantValues.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            // 获取到的密码为空，则为第一次进入，打开设置密码对话框
            showSetPassWord();
        } else {
            // 获取到的密码不为空，表明已设置过密码，则打开确认密码对话框
            showConfirmPassWord();
        }
    }

    /**
     * 设置密码对话框
     */
    private void showSetPassWord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_psd, null);
        // 兼容低版本，消除Android原生的设置边距都为0
        dialog.setView(view, 0, 0, 0, 0);
        // dialog.setView(view);
        dialog.show();
        // 设置密码对话框中确认和取消按钮
        Button submitButton = (Button) view.findViewById(R.id.button_submit);
        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        // 设置密码对话框中确认按钮的点击事件
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置密码对话框中输入密码和再次输入密码的文本编辑框
                EditText setPasswordText = (EditText) view.findViewById(R.id.edit_text_set_psd);
                EditText resetPasswordText = (EditText) view.findViewById(R.id.edit_text_reset_psd);
                // 获取输入密码和再次输入密码两个文本编辑框中的密码值
                String setPassword = setPasswordText.getText().toString();
                String resetPassword = resetPasswordText.getText().toString();
                if (! TextUtils.isEmpty(setPassword) && ! TextUtils.isEmpty(resetPassword)) {
                    if (setPassword.equals(resetPassword)) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        // 隐藏设置密码对话框
                        dialog.dismiss();
                        SharePreferenceUtil.putStringToSharePreference(getApplicationContext(),
                                ConstantValues.MOBILE_SAFE_PSD, MD5Util.encodePassword(setPassword));
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码不一致，请确认后重新输入！");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "密码输入错误！");
                }
            }
        });
        // 设置密码对话框中取消按钮的点击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏设置密码对话框
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认密码对话框
     */
    private void showConfirmPassWord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
        // 兼容低版本，消除Android原生的设置边距都为0
        dialog.setView(view, 0, 0, 0, 0);
        // dialog.setView(view);
        dialog.show();
        // 设置密码对话框中确认和取消按钮
        Button submitButton = (Button) view.findViewById(R.id.button_submit);
        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        // 设置密码对话框中确认按钮的点击事件
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确认密码对话框中输入密码的文本编辑框
                EditText confirmPasswordText = (EditText) view.findViewById(R.id.confirm_password);
                // 获取输入密码文本编辑框中的密码值
                String confirmPassword = confirmPasswordText.getText().toString();
                // 对确认密码进行加密处理，以进行密码比较
                String encodeConfirmPassword = MD5Util.encodePassword(confirmPassword);
                // 从SharedPreferences中获取已存储的密码值
                String storagePassword = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(),
                        ConstantValues.MOBILE_SAFE_PSD, "");
                if (! TextUtils.isEmpty(encodeConfirmPassword) && ! TextUtils.isEmpty(storagePassword) && encodeConfirmPassword.equals(storagePassword) ){
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        // 隐藏设置密码对话框
                        dialog.dismiss();
                }else{
                    ToastUtil.show(getApplicationContext(), "密码输入错误！");
                }
            }
        });
        // 设置密码对话框中取消按钮的点击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏设置密码对话框
                dialog.dismiss();
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNameList.length;
        }

        @Override
        public Object getItem(int position) {
            return mNameList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
                viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
                // 将ViewHolder存储在View中
                view.setTag(viewHolder);
            } else {
                view = convertView;
                // 重新获取ViewHolder
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.imageView.setImageResource(mIconList[position]);
            viewHolder.textView.setText(mNameList[position]);
            return view;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }
}
