package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import co.touchlab.touchkit.rk.common.result.StepResult;

/**
 * TODO Figure out Implementation, allow {@link FormStepLayout} to extend from this class
 * TODO Create builder object to clean up setters for title, text, etc..
 */
public abstract class StepLayout extends RelativeLayout
{
    public StepLayout(Context context)
    {
        super(context);
        init();
    }

    public StepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public StepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {

    }

    protected abstract View getBodyView(LayoutInflater inflater, LinearLayout parent);

    protected abstract StepResult getResult();

}
