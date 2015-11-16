package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;
import co.touchlab.researchstack.ui.callbacks.ActivityCallback;

public class SignUpEligibleScene extends Scene
{

    public static final int CONSENT_REQUEST = 1001;

    private ActivityCallback permissionCallback;

    public SignUpEligibleScene(Context context, Step step)
    {
        super(context, step);

        //TODO Fix this, very disgusting
        if (context instanceof ActivityCallback)
        {
            permissionCallback = (ActivityCallback) context;
        }
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_eligible, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        super.onBodyCreated(body);

        RxView.clicks(body.findViewById(R.id.start_consent_button))
                .subscribe(v -> startConsentActivity());

        hideNextButtons();
    }

    private void startConsentActivity()
    {
        permissionCallback.startConsentTask();
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }

}
