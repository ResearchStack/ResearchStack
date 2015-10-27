package co.touchlab.touchkit.rk.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;
import co.touchlab.touchkit.rk.common.model.ConsentSection;
import co.touchlab.touchkit.rk.common.model.ConsentSignature;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentStep;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.ui.ConsentActivity;
import co.touchlab.touchkit.rk.ui.ViewTaskActivity;

public class SignUpEligibleStepFragment extends StepFragment
{

    private static final int CONSENT_REQUEST = 0;

    public SignUpEligibleStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpEligibleStepFragment fragment = new SignUpEligibleStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_eligible, null);

        root.findViewById(R.id.start_consent_button)
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startConsentActivity();
                    }
                });

        hideNextButtons();

        return root;
    }

    private void startConsentActivity()
    {
        List<ConsentSection> sections = AppDelegate.getInstance().getConsentSectionsAndHtmlContent(getResources());
        ConsentSignature signature = new ConsentSignature(getString(R.string.participant), null, "participant");

        ConsentDocument consent = new ConsentDocument();
        consent.setTitle(getString(R.string.signature_page_title));
        consent.setSignaturePageTitle(getString(R.string.signature_page_title));
        consent.setSignaturePageContent(getString(R.string.signature_page_content));
        consent.setSections(sections);
        consent.addSignature(signature);

        ConsentStep step = new ConsentStep("visual", consent);

        OrderedTask task = new OrderedTask("consent", step);
        Intent intent = ViewTaskActivity.newIntent(getContext(), task);
        startActivityForResult(intent, CONSENT_REQUEST);
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
            boolean eligible = data.getBooleanExtra(ConsentActivity.CONSENT_RESULT,
                    false);
            if (eligible)
            {
                callbacks.onNextPressed(step);
            }
        }
        super.onActivityResult(requestCode,
                resultCode,
                data);
    }
}
