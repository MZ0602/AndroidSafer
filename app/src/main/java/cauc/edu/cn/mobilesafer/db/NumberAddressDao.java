package cauc.edu.cn.mobilesafer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
* FileName: NumberAddressDao <br>
* Description: 号码归属地查询的工具类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/5/16 9:20
*/
public class NumberAddressDao {
    public static final String tag = "NumberAddressDao";
    //号码归属地数据库的本地存储路径
    public static String addressPathName = "data/data/cauc.edu.cn.mobilesafer/files/address.db";
    private static String mAddress = "未知号码";

    /**
     * 根据电话号码查询电话号码的归属地
     *
     * @param phoneNumber 待查询的电话号码
     */
    public static String getPhoneAddress(String phoneNumber) {
        mAddress = "未知号码";
        // 构建正则表达式
        String regularExpression = "^1[3458]\\d{9}$";
        // 获取指定路径下数据库的对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(addressPathName, null, SQLiteDatabase.OPEN_READONLY);
        if (phoneNumber.matches(regularExpression)) {
            // 获取手机号码的前七位
            phoneNumber = phoneNumber.substring(0, 7);
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phoneNumber}, null, null, null);
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
                Log.i(tag, "outkey = " + outkey);
                Cursor innerCursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
                if (innerCursor.moveToNext()) {
                    mAddress = innerCursor.getString(0);
                    Log.i(tag, "position = " + mAddress);
                }
            }
        } else {
            int phoneLength = phoneNumber.length();
            switch (phoneLength) {
                case 3:
                    mAddress = "特殊号码";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "客服电话";
                    break;
                case 7:
                case 8:
                    mAddress = "本地电话";
                    break;
                default:
                    if (phoneLength == 11 && phoneNumber.startsWith("0")) {
                        String number = phoneNumber.substring(1, 3);
                        Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{number}, null, null, null);
                        if (cursor.moveToNext()) {
                            mAddress = cursor.getString(0);
                        } else {
                            mAddress = "未知号码";
                        }
                    } else if (phoneLength == 12 && phoneNumber.startsWith("0")) {
                        String number = phoneNumber.substring(1, 4);
                        Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{number}, null, null, null);
                        if (cursor.moveToNext()) {
                            mAddress = cursor.getString(0);
                        } else {
                            mAddress = "未知号码";
                        }
                    }
                    break;
            }
        }
        return mAddress;
    }
}
