package huajiteam.zhuhaibus;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by KelaKim on 2016/5/19.
 */
public class FocusedTextView extends TextView {

    public FocusedTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context) {
        super(context);
    }

    public boolean isFocused() {
        return true;
    }
}
