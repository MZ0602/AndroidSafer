package cauc.edu.cn.mobilesafer.activity.appManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cauc.edu.cn.mobilesafer.util.AppInfo;
import cauc.edu.cn.mobilesafer.util.AppInfoProvider;
import cauc.edu.cn.mobilesafer.util.ToastUtil;
import cauc.edu.cn.mobilesafer.R;
/**
* FileName: AppManagerActivity <br>
* Description: 软件管理功能模块的活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/5/16 15:12
*/
public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String tag = "AppManagerActivity";
    // 显示手机内存（磁盘）可用空间的文本框
    private TextView mPhoneMemoryText;
    // 显示SD卡可用空间的文本框
    private TextView mSdCardMemoryText;
    // 展示手机已安装应用信息的列表
    private ListView mAppInfoListView;
    // 滑动过程中可以改变的描述文本框
    private TextView mTitleDesText;
    // 手机内存可用空间
    private String mPhoneMemory;
    // SD卡可用空间
    private String mSdCardMemory;
    // 手机已安装的应用信息集合
    private List<AppInfo> mAppInfoList;
    // 手机已安装的用户应用信息集合
    private List<AppInfo> mCustomerAppInfoList;
    // 手机已安装的系统应用信息集合
    private List<AppInfo> mSystemAppInfoList;
    // 应用信息展示所需的适配器
    private AppInfoAdapter mAppInfoAdapter;
    // 弹出的窗体对象
    private PopupWindow mPopupWindow;

    private AppInfo mAppInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAppInfoAdapter == null) {
                mAppInfoAdapter = new AppInfoAdapter();
                mAppInfoListView.setAdapter(mAppInfoAdapter);
            } else {
                mAppInfoAdapter.notifyDataSetChanged();
            }
            if (mTitleDesText != null && mCustomerAppInfoList != null) {
                mTitleDesText.setText("用户应用(" + mCustomerAppInfoList.size() + ")");
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        // 初始化布局文件的View
        initView();
        // 初始化列表项数据
        initData();
    }

    /**
     * 初始化列表项数据
     */
    private void initData() {
        getData();
    }


    /**
     * 获取数据库列表中的数据
     */
    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取用户手机安装的应用信息列表
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemAppInfoList = new ArrayList<AppInfo>();
                mCustomerAppInfoList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem) {
                        // 系统应用
                        mSystemAppInfoList.add(appInfo);
                    } else {
                        // 用户应用
                        mCustomerAppInfoList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 初始化布局文件的View
     */
    private void initView() {
        mPhoneMemoryText = (TextView) findViewById(R.id.phone_memory_text);
        mSdCardMemoryText = (TextView) findViewById(R.id.sd_card_memory_text);
        mAppInfoListView = (ListView) findViewById(R.id.app_info_list_view);
        mTitleDesText = (TextView) findViewById(R.id.app_info_title_des);
        String phonePath = Environment.getDataDirectory().getAbsolutePath();
        String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 获取手机内存和SD卡对应存储路径下可用空间大小
        mPhoneMemory = Formatter.formatFileSize(this, getAvailableMemorySize(phonePath));
        mSdCardMemory = Formatter.formatFileSize(this, getAvailableMemorySize(sdCardPath));
        mPhoneMemoryText.setText("手机可用空间：" + mPhoneMemory);
        mSdCardMemoryText.setText("sd卡可用空间：" + mSdCardMemory);

        mAppInfoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // AbsListView中view就是listView对象。firstVisibleItem第一个可见条目索引值。visibleItemCount当前一个屏幕的可见条目数
                if (mCustomerAppInfoList != null && mTitleDesText != null) {
                    if (firstVisibleItem >= mCustomerAppInfoList.size() + 1) {
                        // 已滚动到系统应用条目
                        mTitleDesText.setText("系统应用(" + mSystemAppInfoList.size() + ")");
                    } else {
                        // 已滚动到用户应用条目
                        mTitleDesText.setText("用户应用(" + mCustomerAppInfoList.size() + ")");
                    }
                }
            }
        });

        mAppInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerAppInfoList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerAppInfoList.size() + 1) {
                        mAppInfo = mCustomerAppInfoList.get(position - 1);
                    } else {
                        mAppInfo = mSystemAppInfoList.get(position - mCustomerAppInfoList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    /**
     * 展示popup弹出框
     *
     * @param view 被点中的listView中的item
     */
    private void showPopupWindow(View view) {
        View popupView = View.inflate(getApplicationContext(), R.layout.popup_window, null);
        TextView popupViewUnstall = (TextView) popupView.findViewById(R.id.popup_view_unstall);
        TextView popupViewStart = (TextView) popupView.findViewById(R.id.popup_view_start);
        TextView popupViewShare = (TextView) popupView.findViewById(R.id.popup_view_share);
        // 设置点击事件
        popupViewUnstall.setOnClickListener(this);
        popupViewStart.setOnClickListener(this);
        popupViewShare.setOnClickListener(this);
        // 缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        // 透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        alphaAnimation.setFillAfter(true);
        // 创建动画集，并添加动画
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        // 创建popupWindow对象
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // 设置透明背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 指定弹出的窗体的位置
        mPopupWindow.showAsDropDown(view, 150, -view.getHeight());
        // 开启动画集
        popupView.startAnimation(animationSet);
    }

    /**
     * 获取手机内存和SD卡对应存储路径下可用空间大小
     *
     * @param path 待获取可用空间大小的路劲
     */
    private long getAvailableMemorySize(String path) {
        StatFs statFs = new StatFs(path);
        // 获取可用区块的个数
        long blockCount = statFs.getAvailableBlocks();
        // 获取区块的大小
        long blockSize = statFs.getBlockSize();
        return blockCount * blockSize;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_view_unstall:
                if (mAppInfo.isSystem) {
                    ToastUtil.show(getApplicationContext(), "此应用不能被卸载");
                } else {
                    // 卸载程序
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.popup_view_start:
                // 开启程序
                PackageManager packageManager = getPackageManager();
                // 获取开启指定包名的应用的intent
                Intent intentForPackage = packageManager.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (intentForPackage != null) {
                    startActivity(intentForPackage);
                } else {
                    ToastUtil.show(getApplicationContext(), "此应用不能打开");
                }
                break;
            case R.id.popup_view_share:
                // 分享应用
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用,应用名称为"+mAppInfo.getAppName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    /**
     * App信息列表展示的适配器
     */
    class AppInfoAdapter extends BaseAdapter {

        // 获取适配器将要创建的View的类型数
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        // 获取getView方法中将要创建的具体View的类型
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerAppInfoList.size() + 1) {
                // 纯文本条目的状态码
                return 0;
            } else {
                // 图标和文本条目的状态码
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mSystemAppInfoList.size() + mCustomerAppInfoList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position < mCustomerAppInfoList.size() + 1) {
                return mCustomerAppInfoList.get(position - 1);
            } else {
                return mSystemAppInfoList.get(position - mCustomerAppInfoList.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                // 显示灰色纯文本条目
                ViewTitleHolder viewTitleHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.app_info_title_item, null);
                    viewTitleHolder = new ViewTitleHolder();
                    viewTitleHolder.titleTextView = (TextView) convertView.findViewById(R.id.app_process_info_title_view);
                    convertView.setTag(viewTitleHolder);
                } else {
                    viewTitleHolder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewTitleHolder.titleTextView.setText("用户应用(" + mCustomerAppInfoList.size() + ")");
                } else {
                    viewTitleHolder.titleTextView.setText("系统应用(" + mSystemAppInfoList.size() + ")");
                }
                return convertView;
            } else {
                // 显示图标和文字条目
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.app_info_list_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.appIconImage = (ImageView) convertView.findViewById(R.id.app_info_icon);
                    viewHolder.appNameText = (TextView) convertView.findViewById(R.id.app_info_name);
                    viewHolder.appIsSdCardText = (TextView) convertView.findViewById(R.id.app_info_is_sdcard);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.appIconImage.setBackgroundDrawable(getItem(position).icon);
                viewHolder.appNameText.setText(getItem(position).appName);
                if (getItem(position).isSDCard) {
                    viewHolder.appIsSdCardText.setText("sd卡应用");
                } else {
                    viewHolder.appIsSdCardText.setText("手机应用");
                }
                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView appIconImage;
        TextView appNameText;
        TextView appIsSdCardText;
    }

    static class ViewTitleHolder {
        TextView titleTextView;
    }
}
