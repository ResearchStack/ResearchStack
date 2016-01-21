package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.core.utils.ThemeUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.step.PassCodeConfirmationStep;

public class SignUpPinCodeConfirmationStepLayout extends SignUpPinCodeStepLayout implements StepLayout
{

    public SignUpPinCodeConfirmationStepLayout(Context context)
    {
        super(context);
    }

    public SignUpPinCodeConfirmationStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpPinCodeConfirmationStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initializeLayout()
    {
        super.initializeLayout();

        int error = getResources().getColor(R.color.error);

        RxTextView.textChanges(editText).subscribe(s -> {

            if(summary.getCurrentTextColor() == error)
            {
                summary.setTextColor(ThemeUtils.getTextColorPrimary(getContext()));
                summary.setText(R.string.passcode_confirm_summary);
            }

            if(s != null && s.length() == config.getPinLength())
            {
                PassCodeConfirmationStep step = (PassCodeConfirmationStep) this.step;

                new Handler().postDelayed(() -> {
                    // If the pins are the same, move along
                    if(s.toString().equalsIgnoreCase(step.getPin()))
                    {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        result.setResult(s.toString());
                        callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, result);
                    }

                    // If the pins are not, show error
                    else
                    {
                        summary.setTextColor(error);
                        summary.setText(R.string.passcode_confirm_error);
                    }
                }, 300);

            }
        });

    }

}
