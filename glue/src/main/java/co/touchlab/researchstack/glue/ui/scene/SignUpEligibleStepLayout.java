package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.step.layout.StepLayoutImpl;
import co.touchlab.researchstack.glue.R;

public class SignUpEligibleStepLayout extends StepLayoutImpl
{

    public static final int CONSENT_REQUEST = 1001;

    private ActivityCallback permissionCallback;

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
    public void initializeScene()
    {
        if (getContext() instanceof ActivityCallback)
        {
            permissionCallback = (ActivityCallback) getContext();
        }

        super.initializeScene();
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

}
