package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by spiria on 8/2/18.
 */

public class ReadOnlyStepBlocker extends LinearLayout {
    public ReadOnlyStepBlocker(Context context) {
        super(context);
    }

    public ReadOnlyStepBlocker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadOnlyStepBlocker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
