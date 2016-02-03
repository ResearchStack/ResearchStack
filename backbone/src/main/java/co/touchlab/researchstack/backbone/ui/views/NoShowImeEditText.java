package co.touchlab.researchstack.backbone.ui.views;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Custom EditText that allows us to enable / disable showing soft input method.
 */
public class NoShowImeEditText extends EditText
{

    private boolean isTextEditingEnabled = true;

    public NoShowImeEditText(Context context)
    {
        super(context);
    }

    public NoShowImeEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NoShowImeEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public NoShowImeEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onCheckIsTextEditor()
    {
        return isTextEditingEnabled && super.onCheckIsTextEditor();
    }

    public void setIsTextEdittingEnalbed(boolean isTextEdittingEnalbed)
    {
        this.isTextEditingEnabled = isTextEdittingEnalbed;
    }
}
