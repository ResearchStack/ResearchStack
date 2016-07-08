package org.researchstack.skin.ui.layout;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.PinCodeLayout;
import org.researchstack.backbone.utils.ThemeUtils;
import org.researchstack.skin.R;
import org.researchstack.skin.step.PassCodeCreationStep;

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
        this.result = result == null ? new StepResult<>(step) : result;

        if(this.step.getStateOrdinal() != - 1)
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

    @Override
    public void receiveIntentExtraOnResult(int requestCode, Intent intent) {

    }

    private void refreshState()
    {
        String pinCodeTitle;
        String pinCodeInstructions;
        int summaryColor;

        Resources res = getResources();
        int pinLength = config.getPinLength();
        String characterType = res.getString(config.getPinType().getInputTypeStringId());
        switch(state)
        {
            case CONFIRM:
                pinCodeTitle = res.getString(R.string.rss_passcode_confirm_title);
                pinCodeInstructions = res.getString(R.string.rss_passcode_confirm_summary,
                        pinLength,
                        characterType);
                summaryColor = ThemeUtils.getTextColorPrimary(getContext());
                break;

            case RETRY:
                pinCodeTitle = res.getString(R.string.rss_passcode_confirm_title);
                pinCodeInstructions = res.getString(R.string.rss_passcode_confirm_error,
                        pinLength,
                        characterType);
                summaryColor = ContextCompat.getColor(getContext(), R.color.rsb_error);
                break;

            case CREATE:
            default:
                pinCodeTitle = res.getString(R.string.rss_passcode_create_title);
                pinCodeInstructions = res.getString(R.string.rss_passcode_create_summary,
                        pinLength,
                        characterType);
                summaryColor = ThemeUtils.getTextColorPrimary(getContext());
                break;

            case CHANGE:
                pinCodeTitle = res.getString(R.string.rsb_pincode_enter_title);
                pinCodeInstructions = res.getString(R.string.rsb_pincode_enter_summary,
                        pinLength,
                        characterType);
                summaryColor = ThemeUtils.getTextColorPrimary(getContext());
                break;
        }

        updateText(pinCodeTitle, pinCodeInstructions, summaryColor);
    }

    private void updateText(String titleString, String textString, int color)
    {
        title.setText(titleString);
        summary.setText(textString);
        summary.setTextColor(color);
    }

}
