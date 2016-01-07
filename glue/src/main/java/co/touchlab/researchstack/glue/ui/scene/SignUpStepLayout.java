package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.glue.R;

public class SignUpStepLayout extends RelativeLayout implements StepLayout
{
    private Step step;

    public SignUpStepLayout(Context context)
    {
        super(context);
    }

    public SignUpStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    SceneCallbacks callbacks;

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;

        View layout = LayoutInflater.from(getContext()).inflate(R.layout.item_sign_up, this, true);

        Button next = (Button) layout.findViewById(R.id.next);

        RxView.clicks(next).subscribe(v -> {
            signUp();
        });
    }

    private void signUp()
    {
        // TODO sign up using network interface, then call on next on success, maybe save user/pass to result
        callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, null);
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
