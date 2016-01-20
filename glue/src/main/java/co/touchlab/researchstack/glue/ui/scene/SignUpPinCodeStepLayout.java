package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.glue.R;

public class SignUpPinCodeStepLayout extends RelativeLayout implements StepLayout
{
    protected InputMethodManager imm;

    protected PinCodeConfig      config;
    protected SceneCallbacks     callbacks;
    protected Step               step;
    protected StepResult<String> result;

    protected TextView summary;
    protected EditText editText;

    public SignUpPinCodeStepLayout(Context context)
    {
        super(context);
    }

    public SignUpPinCodeStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpPinCodeStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
        this.config = ((AuthDataAccess) StorageManager.getFileAccess()).getPinCodeConfig(); //TODO get config from AesFileAccess
        this.imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        initializeLayout();
    }

    @CallSuper
    protected void initializeLayout()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.step_layout_pincode, this, true);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(step.getTitle());

        summary = (TextView) findViewById(R.id.text);
        summary.setText(step.getText());

        editText = (EditText) findViewById(R.id.passcode);
        editText.requestFocus();
        //TODO Use config variable to set LengthFilter
        //TODO Invalid way of setting input filter. This clears any filters set via XML
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(SceneCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
