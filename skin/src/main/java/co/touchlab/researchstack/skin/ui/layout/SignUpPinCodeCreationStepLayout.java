package co.touchlab.researchstack.skin.ui.layout;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.ui.callbacks.StepCallbacks;
import co.touchlab.researchstack.backbone.ui.step.layout.StepLayout;
import co.touchlab.researchstack.backbone.ui.views.PinCodeLayout;
import co.touchlab.researchstack.backbone.utils.ThemeUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.step.PassCodeCreationStep;

public class SignUpPinCodeCreationStepLayout extends PinCodeLayout implements StepLayout
{
    public static final String RESULT_OLD_PIN = "PassCodeCreationStep.oldPin";

    protected StepCallbacks        callbacks;
    protected PassCodeCreationStep step;
    protected StepResult<String>   result;

    private CharSequence currentPin = null;
    private State        state      = State.CREATE;

    public enum State
    {
        CHANGE,
        CREATE,
        CONFIRM,
        RETRY
    }

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
    public void initialize(Step step, StepResult result)
    {
        this.step = (PassCodeCreationStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;

        if (this.step.getStateOrdinal() != -1)
        {
            this.state = State.values()[this.step.getStateOrdinal()];
        }

        initializeLayout();
    }

    private void initializeLayout()
    {
        refreshState();

        RxTextView.textChanges(editText)
                .map(CharSequence:: toString)
                .filter(pin -> pin.length() == config.getPinLength())
                .subscribe(pin -> {
                    if(state == State.CHANGE)
                    {
                        result.setResultForIdentifier(RESULT_OLD_PIN, pin);

                        currentPin = pin;
                        editText.setText("");
                        state = State.CREATE;
                        refreshState();
                    }
                    else if(state == State.CREATE)
                    {
                        currentPin = pin;
                        editText.setText("");
                        state = State.CONFIRM;
                        refreshState();
                    }
                    else
                    {
                        if(pin.equals(currentPin))
                        {
                            new Handler().postDelayed(() -> {
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                result.setResult(pin);
                                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
                            }, 300);
                        }
                        else
                        {
                            state = State.RETRY;
                            editText.setText("");
                            refreshState();
                        }
                    }
                });

        editText.post(() -> imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY));
    }

    @Override
    public boolean isBackEventConsumed()
    {
        if(state == State.CREATE)
        {
            callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        else
        {
            // pressed back while confirming, go back to creation state
            currentPin = null;
            editText.setText("");
            state = State.CREATE;
            refreshState();
        }
        return true;
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

    private void refreshState()
    {
        Resources res = getResources();
        switch(state)
        {
            case CONFIRM:
                updateText(res.getString(R.string.passcode_confirm_title),
                        res.getString(R.string.passcode_confirm_summary),
                        ThemeUtils.getTextColorPrimary(getContext()));
                break;

            case RETRY:
                updateText(res.getString(R.string.passcode_confirm_title),
                        res.getString(R.string.passcode_confirm_error),
                        res.getColor(R.color.error));
                break;

            case CREATE:
            default:
                updateText(res.getString(R.string.passcode_create_title),
                        res.getString(R.string.passcode_create_summary),
                        ThemeUtils.getTextColorPrimary(getContext()));
                break;

            case CHANGE:
                updateText(res.getString(R.string.rsc_pincode_enter_title),
                        res.getString(R.string.rsc_pincode_enter_summary),
                        ThemeUtils.getTextColorPrimary(getContext()));
                break;
        }
    }

    private void updateText(String titleString, String textString, int color)
    {
        title.setText(titleString);
        summary.setText(textString);
        summary.setTextColor(color);
    }

}
