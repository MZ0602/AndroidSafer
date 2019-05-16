package cauc.edu.cn.mobilesafer.util;

import android.graphics.drawable.Drawable;
/**
* FileName: AppInfo <br>
* Description: 手机应用信息的实体类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/5/16 10:15
*/
public class AppInfo {
    // 应用名称
    public String appName;
    // 应用所在的包名
    public String packageName;
    // 应用图标
    public Drawable icon;
    // 应用是否安装在SD卡上
    public boolean isSDCard;
    // 应用是否是系统应用
    public boolean isSystem;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSDCard() {
        return isSDCard;
    }

    public void setSDCard(boolean SDCard) {
        isSDCard = SDCard;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
