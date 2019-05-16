package cauc.edu.cn.mobilesafer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
* FileName: SharePreferenceUtil <br>
* Description: 在本地文件存储/读取数据的工具类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/17 10:54
*/

public class SharePreferenceUtil {

    private static SharedPreferences mSharedPreferences;

    /**
     * 将所要存储的数据的键值对存储到SharedPreferences中
     * @param context 上下文环境
     * @param key     所要存储数据的键值
     * @param value   所要存储的数据值
     */
    public static void putBooleanToSharePreference(Context context, String key, boolean value) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 从SharedPreferences中读取指定键值的数据值
     * @param context  上下文环境
     * @param key      所要读取数据的键值
     * @param defValue 如果读取不到指定的值，使用默认值
     * @return 返回读取到的指定键值的值
     */
    public static boolean getBooleanFromSharePreference(Context context, String key, boolean defValue) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 将所要存储的数据的键值对存储到SharedPreferences中
     * @param context 上下文环境
     * @param key     所要存储数据的键值
     * @param value   所要存储的数据值
     */
    public static void putStringToSharePreference(Context context, String key, String value) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 从SharedPreferences中读取指定键值的数据值
     * @param context  上下文环境
     * @param key      所要读取数据的键值
     * @param defValue 如果读取不到指定的值，使用默认值
     * @return 返回读取到的指定键值的值
     */
    public static String getStringFromSharePreference(Context context, String key, String defValue) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getString(key, defValue);
    }

    /**
     * 从SharedPreferences中移除指定键值的数据值
     * @param context 上下文环境
     * @param key 所要移除数据的键值
     */
    public static void removeStringFromSharePreference(Context context, String key) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().remove(key).commit();
    }

    /**
     * 将所要存储的数据的键值对存储到SharedPreferences中
     * @param context 上下文环境
     * @param key     所要存储数据的键值
     * @param value   所要存储的数据值
     */
    public static void putIntToSharePreference(Context context, String key, int value) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 从SharedPreferences中读取指定键值的数据值
     * @param context  上下文环境
     * @param key      所要读取数据的键值
     * @param defValue 如果读取不到指定的值，使用默认值
     * @return 返回读取到的指定键值的值
     */
    public static int getIntFromSharePreference(Context context, String key, int defValue) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getInt(key, defValue);
    }
}
