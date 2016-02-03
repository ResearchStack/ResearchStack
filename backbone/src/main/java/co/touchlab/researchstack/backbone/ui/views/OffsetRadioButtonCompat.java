package co.touchlab.researchstack.backbone.ui.views;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

public class OffsetRadioButtonCompat extends AppCompatRadioButton
{
    public OffsetRadioButtonCompat(Context context)
    {
        super(context);
    }

    public OffsetRadioButtonCompat(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OffsetRadioButtonCompat(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(17)
    public int getHorizontalOffsetForDrawables()
    {
        return 144;
    }
}
