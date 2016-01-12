package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import java.util.Date;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.ui.views.SubmitBar;

public class SignUpEligibleStepLayout extends RelativeLayout implements StepLayout
{

    public static final int CONSENT_REQUEST = 1001;

    private ActivityCallback permissionCallback;
    private Step             step;
    private StepResult       result;
    private SceneCallbacks   callbacks;

    public SignUpEligibleStepLayout(Context context)
    {
        super(context);
    }

    public SignUpEligibleStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpEligibleStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result;

        if(getContext() instanceof ActivityCallback)
        {
            permissionCallback = (ActivityCallback) getContext();
        }

        initializeScene();
    }

    private void initializeScene()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.item_eligible, this, true);

        // TODO only for testing
        RxView.clicks(findViewById(R.id.DEBUG_skip_consent)).subscribe(v -> skipConsentActivity());

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setSubmitAction((v) -> startConsentActivity());
        submitBar.setExitAction((v) -> exitSignUpActivity());
    }

    private void skipConsentActivity()
    {
        ResearchStack.getInstance()
                .getDataProvider()
                .saveConsent(getContext(),
                        "test name",
                        new Date(662748042000l),
                        "VGhpcyBpc24ndCBhIHJlYWwgaW1hZ2Uu",
                        "",
                        "all_qualified_researchers");
        callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, null);
    }

    private void startConsentActivity()
    {
        permissionCallback.startConsentTask();
    }

    private void exitSignUpActivity()
    {
        callbacks.onSaveStep(SceneCallbacks.ACTION_END, step, null);
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(SceneCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
