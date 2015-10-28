package co.touchlab.touchkit.rk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.step.ConsentSharingStep;
import co.touchlab.touchkit.rk.ui.ViewWebDocumentActivity;

public class ConsentSharingFragment extends BooleanQuestionStepFragment
{

    public ConsentSharingFragment()
    {
        super();
    }

    public static Fragment newInstance(ConsentSharingStep step)
    {
        ConsentSharingFragment fragment = new ConsentSharingFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initMoreInfoView(TextView moreInfo)
    {
        moreInfo.setText(R.string.consent_share_more_info);
        RxView.clicks(moreInfo).subscribe(v -> consentShareLearnMore());
    }

    private void consentShareLearnMore()
    {
        ConsentSharingStep step = (ConsentSharingStep) getStep();
        String title = getString(R.string.consent_learn_more);
        String docName = step.getLocalizedLearnMoreHTMLContent();
        Intent launch = ViewWebDocumentActivity.newIntent(getContext(), title, docName);
        startActivity(launch);
    }
}
