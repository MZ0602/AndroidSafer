package cauc.edu.cn.mobilesafer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
/**
* FileName: AntiVirusDao <br>
* Description: 从本地数据库读取病毒特征值的数据库操作类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/30 16:22
*/
public class AntiVirusDao {
    public static final String tag = "AntiVirusDao";
    // 病毒库拷贝到本地后的路径地址
    public static String virusPathName = "data/data/cauc.edu.cn.mobilesafer/files/antivirus.db";
    /**
     * 从本地病毒数据库读取特征值列表
     * @return MD5特征值列表
     */
    public static List<String> getVirusList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(virusPathName, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            virusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
