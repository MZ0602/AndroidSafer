package cauc.edu.cn.mobilesafer.activity.blackNumber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import cauc.edu.cn.mobilesafer.db.BlackNumberInfo;
import cauc.edu.cn.mobilesafer.db.BlackNumberDao;
import cauc.edu.cn.mobilesafer.service.InterceptService;
import cauc.edu.cn.mobilesafer.util.MyMobileApplication;
import cauc.edu.cn.mobilesafer.util.ToastUtil;
import cauc.edu.cn.mobilesafer.R;

/**
* FileName: CommunicationGuardActivity <br>
* Description: 通讯卫士的主活动，用于增删通讯黑名单和启动拦截服务 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/28 19:40
*/
public class CommunicationGuardActivity extends AppCompatActivity {

    private static final String tag = "ComuncateGuardActivity";
    // 黑名单中添加号码的按钮
    private Button mAddBlackNumButton;
    // 用于展示已加入黑名单的电话号码的列表
    private ListView mBlackNumListView;
    // 数据列表中取出已存储的黑名单列表
    private List<BlackNumberInfo> mBlackNumList;
    // 操作数据库表的工具类的对象
    private BlackNumberDao mBlackNumDao;
    // 黑名单的数据适配器
    private BlackNumListAdapter mBlackNumListAdapter;
    // 选中拦截电话号码的模式
    private int mode = -1;
    // 判断是否正在加载新的数据,防止重复加载数据
    private boolean mIsLoad = false;
    // 数据库表中总的条目数
    private int mDBCount;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mBlackNumListAdapter == null) {
                mBlackNumListAdapter = new BlackNumListAdapter();
                mBlackNumListView.setAdapter(mBlackNumListAdapter);
            } else {
                mBlackNumListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_guard);
        //拦截电话时需要用到CALL_PHONE和PROCESS_OUTGOING_CALLS权限，为了避免在拦截服务中再申请权限(无法做到)，故此处在用户打开通讯模块主界面的时候先申请相应权限
        if (ContextCompat.checkSelfPermission(MyMobileApplication.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //如果用户尚未授权此权限，则用requestPermissions函数向用户提交授权申请
            ActivityCompat.requestPermissions(CommunicationGuardActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            // 初始化布局文件中的View
            initView();
            // 初始化ListView中的数据值
            initData();
        }
        //启动后台垃圾短信、电话拦截服务
        Intent startIntent=new Intent(this,InterceptService.class);
        startService(startIntent);
    }

    /**
     * 向用户提交动态授权申请后，自动对用户做出的授权决定做出应答
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //若用户同意将CALL_PHONE权限授权给用户，则初始化布局文件中的View
                    initView();
                    // 初始化ListView中的数据值
                    initData();
                }else{
                    ToastUtil.show(this,"你拒绝了授权！");
                }
                break;
            default:
        }
    }

    /**
     * 初始化ListView中的数据值
     */
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取操作数据库列表的对象
                mBlackNumDao = BlackNumberDao.getInstance(getApplicationContext());
                // 一次查询数据库表中的所有数据
                // mBlackNumList = mBlackNumDao.findAll();
                mDBCount = mBlackNumDao.getCount();
                Log.i(tag, "mDBCount:" + mDBCount);
                // 一次查询数据库表中的部分数据（20条）
                mBlackNumList = mBlackNumDao.findPart(0);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        mAddBlackNumButton = (Button) findViewById(R.id.add_black_phone);
        mBlackNumListView = (ListView) findViewById(R.id.black_num_list_view);
        mAddBlackNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加号码到黑名单中的对话框
                showBlackNumDialog();
            }
        });
        // 黑名单列表添加滚动事件监听
        mBlackNumListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 黑名单列表的滚动状态发生改变时调用
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mBlackNumList != null) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && mBlackNumListView.getLastVisiblePosition()>=mBlackNumList.size()-1
                            && !mIsLoad) {
                        // 正在请求数据时，设置mIsLoad为true，防止重复加载
                        // mIsLoad = true;
                        // 如果数据库中的总的条目数大于已有列表中的条目数，则再次请求数据
                        if (mDBCount > mBlackNumList.size()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 获取操作数据库列表的对象
                                    mBlackNumDao = BlackNumberDao.getInstance(getApplicationContext());
                                    // 一次查询数据库表中的部分数据（20条）
                                    List<BlackNumberInfo> moreNumList = mBlackNumDao.findPart(mBlackNumList.size());
                                    mBlackNumList.addAll(moreNumList);
                                    mHandler.sendEmptyMessage(0);
                                    // 请求数据完成后，重新设置mIsLoad为false
                                    // mIsLoad = false;
                                }
                            }).start();
                        }
                    }
                }
            }

            // 黑名单列表滚动时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    /**
     * 添加号码到黑名单中的对话框
     */
    private void showBlackNumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_select_view, null);
        // 对话框设置我们自定义的布局文件
        dialog.setView(dialogView, 0, 0, 0, 0);
        final EditText inputHookNum = (EditText) dialogView.findViewById(R.id.input_hook_number);
        RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group);
        // 默认点击RadioGroup时，模式为1
        mode = 1;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.selected_phone:
                        // 拦截电话模式
                        mode = 1;
                        break;
                    case R.id.selected_sms:
                        // 拦截短信模式
                        mode = 2;
                        break;
                    case R.id.selected_all:
                        // 拦截所有的模式
                        mode = 3;
                        break;
                }
            }
        });
        Button affirmAddNum = (Button) dialogView.findViewById(R.id.affirm_add_black_num);
        affirmAddNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputHookPhoneNum = inputHookNum.getText().toString();
                if (!TextUtils.isEmpty(inputHookPhoneNum)) {
                    // 添加到黑名单列表中
                    mBlackNumDao.insert(inputHookPhoneNum, mode + "");
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.number = inputHookPhoneNum;
                    blackNumberInfo.mode = mode + "";
                    mBlackNumList.add(0, blackNumberInfo);
                    if (mBlackNumListAdapter != null) {
                        mBlackNumListAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入要拦截的电话号码！");
                }
            }
        });
        Button cancleAddNum = (Button) dialogView.findViewById(R.id.cancle_add_black_num);
        cancleAddNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭添加黑名单对话框
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 自定义黑名单列表的适配器
     */
    class BlackNumListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackNumList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.black_num_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.blackPhoneNumText = (TextView) convertView.findViewById(R.id.black_phone_num);
                viewHolder.blackPhoneModeText = (TextView) convertView.findViewById(R.id.black_phone_mode);
                viewHolder.deleteBlackNum = (ImageView) convertView.findViewById(R.id.delete_black_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.blackPhoneNumText.setText(mBlackNumList.get(position).getNumber());
            int storagedMode = Integer.parseInt(mBlackNumList.get(position).getMode());
            switch (storagedMode) {
                case 1:
                    viewHolder.blackPhoneModeText.setText("拦截电话");
                    break;
                case 2:
                    viewHolder.blackPhoneModeText.setText("拦截短信");
                    break;
                case 3:
                    viewHolder.blackPhoneModeText.setText("拦截所有");
                    break;
            }
            viewHolder.deleteBlackNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 数据列表中删除当前选项
                    mBlackNumDao.delete(mBlackNumList.get(position).getNumber());
                    if (mBlackNumList != null) {
                        mBlackNumList.remove(position);
                    }
                    if (mBlackNumListAdapter != null) {
                        mBlackNumListAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    /**
     * 静态类，减少类对象的创建次数
     */
    static class ViewHolder {
        TextView blackPhoneNumText;
        TextView blackPhoneModeText;
        ImageView deleteBlackNum;
    }
}
