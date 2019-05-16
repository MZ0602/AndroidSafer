package cauc.edu.cn.mobilesafer.db;

/**
* FileName: BlackNumberInfo <br>
* Description: 黑名单用户的底层JavaBean类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/24 14:17
*/

public class BlackNumberInfo {
    // 已添加到黑名单中的号码
    public String number;
    // 已添加到黑名单中的号码的拦截模式
    public String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "number:" + number + ",mode:" + mode;
    }
}
