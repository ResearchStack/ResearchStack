package co.touchlab.researchstack.ui.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by bradleymcdermott on 10/9/15.
 */
@Deprecated
public class StepViewPager extends ViewPager
{
    public StepViewPager(Context context)
    {
        super(context);
    }

    public StepViewPager(Context context, AttributeSet attrs)
    {
        super(context,
                attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return false;
    }
}
