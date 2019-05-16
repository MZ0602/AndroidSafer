package cauc.edu.cn.mobilesafer.util;

import android.content.Context;
import android.widget.Toast;

/**
* FileName: ToastUtil <br>
* Description: 用于系统中随时输出消息弹框的工具类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/17 11:37
*/

public class ToastUtil {

    /**
     * @param context 上下文环境
     * @param str toast中描述信息
     */
   public static void show(Context context, String str) {
       Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

}
