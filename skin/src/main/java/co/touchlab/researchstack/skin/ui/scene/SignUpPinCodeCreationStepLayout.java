package co.touchlab.researchstack.skin.ui.scene;
import android.content.Context;
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

public class SignUpPinCodeCreationStepLayout extends PinCodeLayout implements StepLayout
{

    protected StepCallbacks callbacks;
    protected Step               step;
    protected StepResult<String> result;

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
        this.step = step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;

        initializeLayout();
    }

    private void initializeLayout()
    {
        summary.setText(step.getText());
        title.setText(step.getTitle());

        RxTextView.textChanges(editText)
                .map(CharSequence:: toString)
                .filter(pin -> pin.length() == config.getPinLength())
                .subscribe(pin -> {
                    new Handler().postDelayed(() -> {
                        result.setResult(pin);
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
                    }, 300);
                });

        editText.post(() -> imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY));
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
