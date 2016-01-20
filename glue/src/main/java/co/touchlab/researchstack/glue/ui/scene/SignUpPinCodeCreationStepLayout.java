package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;

public class SignUpPinCodeCreationStepLayout extends SignUpPinCodeStepLayout implements StepLayout
{
    public SignUpPinCodeCreationStepLayout(Context context)
    {
        super(context);
    }

    public SignUpPinCodeCreationStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpPinCodeCreationStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initializeLayout()
    {
        super.initializeLayout();

        RxTextView.textChanges(editText)
                .filter(charSequence -> charSequence.length() == config.getLength())
                .subscribe(charSequence -> {
                    new Handler().postDelayed(() -> {
                        result.setResult(charSequence.toString());
                        callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, result);
                    }, 300);
                });

        editText.post(() -> imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY));
    }

}
