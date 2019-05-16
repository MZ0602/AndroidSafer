package cauc.edu.cn.mobilesafer.activity.virusCheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cauc.edu.cn.mobilesafer.R;
import cauc.edu.cn.mobilesafer.db.AntiVirusDao;
import cauc.edu.cn.mobilesafer.util.ToastUtil;
/**
* FileName: AntiVirusActivity <br>
* Description: 手机杀毒模块进行手机病毒查杀的活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/30 15:49
*/
public class AntiVirusActivity extends AppCompatActivity {
    // 正在扫描病毒
    private static final int SCANNING = 0;
    // 扫描完成
    private static final int SCANNING_END = 1;
    //扫毒过程动态转圈图形
    private ImageView mActScanningImage;
    // 动态添加每个软件的扫描结果
    private LinearLayout mScanningAddLayout;
    //当前正在扫描的软件的名称
    private TextView mScanningNameText;
    private ProgressBar mScanningProgressBar;
    //用于记录扫描过程中发现的病毒列表
    private List<VirusScanningInfo> mVirusScanningList;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        // 初始化布局文件中view
        initView();
        // 初始化扫描动画
        initScanningAnimation();
        // 检测手机病毒
        checkVirus();
    }

    // 异步处理机制。处理扫毒子线程传递过来的反馈信息并进行UI更新(Aandroid子线程中不能有UI更新操作)
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在扫描中，处理扫毒子线程实时传过来的每个应用的染毒信息并显示
                case 0:
                    VirusScanningInfo info = (VirusScanningInfo) msg.obj;
                    TextView textView = new TextView(getApplicationContext());
                    mScanningNameText.setText(info.name);
                    if (info.isVirus) {
                        textView.setText("发现病毒:" + info.name);
                        textView.setTextColor(Color.RED);
                    } else {
                        textView.setText("扫描安全:" + info.name);
                        textView.setTextColor(Color.GREEN);
                    }
                    //将每个应用的扫毒结果实时动态地显示在UI界面中
                    mScanningAddLayout.addView(textView, 0);
                    break;
                // 扫描已结束，进行病毒处理或无毒提示
                case 1:
                    //停止扫毒动画的播放
                    mActScanningImage.clearAnimation();
                    mScanningNameText.setText("扫描完成");
                    // 清除病毒
                    unStallVirus(mVirusScanningList);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 检测手机病毒
     */
    private void checkVirus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                count = 0;
                // 获取病毒相关信息集合
                List<String> virusList = AntiVirusDao.getVirusList();
                // 将扫描到的病毒信息添加到病毒集合
                mVirusScanningList = new ArrayList<VirusScanningInfo>();
                // 扫描到的所有应用的信息集合
                List<VirusScanningInfo> scanningList = new ArrayList<VirusScanningInfo>();
                // 获取包管理者对象
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES
                        + PackageManager.GET_UNINSTALLED_PACKAGES);
                mScanningProgressBar.setMax(packageInfoList.size());
                // 循环遍历所有已获得的应用包集合
                for (PackageInfo packageInfo : packageInfoList) {
                    // 包信息获取所有的签名的数组
                    Signature[] signatures = packageInfo.signatures;
                    // 获取签名文件的第一位
                    String signature = signatures[0].toCharsString();
                    // 加密获取到的签名文件的第一位（感觉不对，无需进行MD5加密，直接与病毒特征库比较即可）
                    // String signatureMD = MD5Util.encodePassword(signature);
                    VirusScanningInfo scanningInfo = new VirusScanningInfo();
                    // if (virusList.contains(signatureMD)) {
                    if (virusList.contains(signature)) {
                        // 扫描到病毒,记录病毒后提醒用户卸载
                        scanningInfo.isVirus = true;
                        mVirusScanningList.add(scanningInfo);
                    } else {
                        scanningInfo.isVirus = false;
                    }
                    // 扫描到的所有应用的信息集合
                    scanningList.add(scanningInfo);
                    scanningInfo.packageName = packageInfo.packageName;
                    scanningInfo.name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    // 暂缓线程
                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 扫描过程中,实时更新进度条
                    count++;
                    mScanningProgressBar.setProgress(count);
                    //扫描尚未结束，将每个应用的扫毒结果实时传给主线程给Handler处理，实时更新UI
                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scanningInfo;
                    mHandler.sendMessage(msg);
                }
                //扫描已结束，返回到Handler进行病毒扫描结果最终处理
                Message msg = Message.obtain();
                msg.what = SCANNING_END;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 卸载病毒
     */
    private void unStallVirus(final List<VirusScanningInfo> virusScanningList) {
        // 卸载病毒程序
        if (!virusScanningList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("警告！");
            builder.setMessage("发现" + virusScanningList.size() + "个病毒，请立即清理！");
            builder.setPositiveButton("立即清理", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (VirusScanningInfo virusScanningInfo : virusScanningList) {
                        Intent intent = new Intent("android.intent.action.DELETE");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:" + virusScanningInfo.packageName));
                        startActivity(intent);
                    }
                }
            });
        } else {
            ToastUtil.show(getApplicationContext(), "扫描完成，未发现木马病毒");
        }
    }

    /**
     * 初始化扫描动画
     */
    private void initScanningAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        // 设置动画无限循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        // 设置插值器,匀速循环且不停顿
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);
        mActScanningImage.startAnimation(rotateAnimation);
    }

    /**
     * 初始化布局文件中view
     */
    private void initView() {
        mActScanningImage = (ImageView) findViewById(R.id.act_scanning_img);
        mScanningNameText = (TextView) findViewById(R.id.current_scanning_text);
        mScanningAddLayout = (LinearLayout) findViewById(R.id.linear_anti_add_text);
        mScanningProgressBar = (ProgressBar) findViewById(R.id.scanning_progress);
    }

    class VirusScanningInfo {
        String packageName;
        String name;
        boolean isVirus;
    }
}
