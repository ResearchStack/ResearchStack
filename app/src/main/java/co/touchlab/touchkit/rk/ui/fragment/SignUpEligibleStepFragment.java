package co.touchlab.touchkit.rk.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentSignature;
import co.touchlab.touchkit.rk.common.model.User;
import co.touchlab.touchkit.rk.common.result.ConsentSignatureResult;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.ConsentTask;
import co.touchlab.touchkit.rk.ui.ViewTaskActivity;

public class SignUpEligibleStepFragment extends StepFragment
{

    private static final int CONSENT_REQUEST = 1001;

    public SignUpEligibleStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpEligibleStepFragment fragment = new SignUpEligibleStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_eligible,
                null);

        root.findViewById(R.id.start_consent_button)
                .setOnClickListener(v -> startConsentActivity());

        hideNextButtons();

        return root;
    }

    private void startConsentActivity()
    {
        ConsentTask task = new ConsentTask(getContext());
        Intent intent = ViewTaskActivity.newIntent(getContext(),
                task);
        startActivityForResult(intent,
                CONSENT_REQUEST);
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CONSENT_REQUEST && resultCode == Activity.RESULT_OK)
        {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            boolean sharing = ((QuestionResult<Boolean>) result.getStepResultForStepIdentifier("sharing")
                    .getResultForIdentifier("sharing")).getAnswer();

            ConsentSignatureResult signatureResult = ((ConsentSignatureResult) result.getStepResultForStepIdentifier("reviewStep"));
            ConsentSignature signature = signatureResult.getSignature();
            boolean consented = signatureResult.isConsented();


            User currentUser = AppDelegate.getInstance()
                    .getCurrentUser();
            // TODO check for valid signature/names
            if (consented)
            {
                // TODO just use full name to begin with and don't concat names like this
                // TODO get signature date
                String fullName = signature.getGivenName() + " " + signature.getFamilyName();
                currentUser.setName(fullName);
                currentUser.setConsentSignatureName(fullName);
                currentUser.setConsentSignatureImage(signature.getSignatureImage());
                currentUser.setUserConsented(true);

                callbacks.onNextPressed(step);
            }
            else
            {
                // Clear activity and show Welcome screen
                getActivity().finish();
            }
        }
        super.onActivityResult(requestCode,
                resultCode,
                data);
    }
}
