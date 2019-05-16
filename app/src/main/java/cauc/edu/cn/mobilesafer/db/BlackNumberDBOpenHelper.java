package cauc.edu.cn.mobilesafer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
* FileName: BlackNumberDBOpenHelper <br>
* Description: 用于创建通讯录黑名单数据库的类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/24 15:34
*/
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

    /**
     * 数据库创建的构造方法
     *
     * @param context 上下文环境
     */
    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库中表结构
        db.execSQL("create table blacknum (_id integer primary key autoincrement, number varchar(20), mode varchar(3)) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
