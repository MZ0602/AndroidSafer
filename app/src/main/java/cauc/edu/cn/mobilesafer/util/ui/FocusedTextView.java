package cauc.edu.cn.mobilesafer.util.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;
/**
* FileName: FocusedTextView <br>
* Description: 用于在主页面显示滑动版权的UI控件 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/5/13 16:44
*/
public class FocusedTextView extends AppCompatTextView {
    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 自定义TextView获取焦点
     * @return 返回true表示获得焦点
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
