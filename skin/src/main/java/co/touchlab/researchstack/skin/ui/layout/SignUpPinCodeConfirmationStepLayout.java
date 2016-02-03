package co.touchlab.researchstack.skin.ui.layout;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.ui.callbacks.StepCallbacks;
import co.touchlab.researchstack.backbone.ui.step.layout.StepLayout;
import co.touchlab.researchstack.backbone.ui.views.PinCodeLayout;
import co.touchlab.researchstack.backbone.utils.ThemeUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.step.PassCodeConfirmationStep;

public class SignUpPinCodeConfirmationStepLayout extends PinCodeLayout implements StepLayout
{

    protected StepCallbacks callbacks;
    protected Step               step;
    protected StepResult<String> result;

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
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;

        initializeLayout();
    }

    private void initializeLayout()
    {
        summary.setText(step.getText());
        title.setText(step.getTitle());

        int error = getResources().getColor(R.color.error);

        RxTextView.textChanges(editText)
                .map(CharSequence:: toString)
                .subscribe(pin -> {

                    // TODO Figure out a better way of handling if we are in an error state. Its probably
                    // better to use the views state and set enabled/disabled instead
                    if(summary.getCurrentTextColor() == error)
                    {
                        summary.setTextColor(ThemeUtils.getTextColorPrimary(getContext()));
                        summary.setText(R.string.passcode_confirm_summary);
                    }

                    if(pin != null && pin.length() == config.getPinLength())
                    {
                        PassCodeConfirmationStep step = (PassCodeConfirmationStep) this.step;

                        new Handler().postDelayed(() -> {
                            // If the pins are the same, move along
                            if(pin.equalsIgnoreCase(step.getPin()))
                            {
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                                result.setResult(pin);
                                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
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

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
