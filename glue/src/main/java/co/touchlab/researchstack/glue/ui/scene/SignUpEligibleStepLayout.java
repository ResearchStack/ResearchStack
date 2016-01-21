package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.ConsentSignatureStepLayout;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.core.ui.views.SubmitBar;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.task.ConsentTask;

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
        submitBar.setPositiveAction((v) -> startConsentActivity());
        submitBar.setNegativeAction((v) -> exitSignUpActivity());
    }

    private void skipConsentActivity()
    {
        // Save user consented
        StepResult<Boolean> consented = new StepResult<>(ConsentTask.ID_CONSENT_DOC);
        consented.setResult(true);
        callbacks.onSaveStep(SceneCallbacks.ACTION_NONE,
                new Step(ConsentTask.ID_CONSENT_DOC),
                consented);

        // Create formResult
        StepResult<StepResult> formResult = new StepResult<>(ConsentTask.ID_FORM);

        // Save user fullname to formResult
        StepResult<String> fullname = new StepResult<>(ConsentTask.ID_FORM_NAME);
        fullname.setResult("test name");
        formResult.setResultForIdentifier(ConsentTask.ID_FORM_NAME, fullname);

        // Save user Birthdate to formResult
        StepResult<Long> birthdate = new StepResult<>(ConsentTask.ID_FORM_DOB);
        birthdate.setResult(662748042000l);
        formResult.setResultForIdentifier(ConsentTask.ID_FORM_DOB, birthdate);

        // Save formResult to TaskResult
        callbacks.onSaveStep(SceneCallbacks.ACTION_NONE, new Step(ConsentTask.ID_FORM), formResult);

        // Save a scope
        StepResult<String> sharingScope = new StepResult<>(ConsentTask.ID_SHARING);
        sharingScope.setResult("all_qualified_researchers");
        callbacks.onSaveStep(SceneCallbacks.ACTION_NONE,
                new Step(ConsentTask.ID_SHARING),
                sharingScope);

        // Create Signature result
        StepResult<String> signatureResult = new StepResult<>(ConsentTask.ID_SIGNATURE);

        // Save a fake image to signatureResult
        signatureResult.setResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE,
                "VGhpcyBpc24ndCBhIHJlYWwgaW1hZ2Uu");

        // Save a signature date to signatureResult
        signatureResult.setResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE,
                "10202011");
        // Save Signature result
        callbacks.onSaveStep(SceneCallbacks.ACTION_NONE,
                new Step(ConsentTask.ID_SIGNATURE), signatureResult);

        // Go to the next step
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
