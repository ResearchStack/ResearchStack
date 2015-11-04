package co.touchlab.touchkit.rk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.step.ConsentSharingStep;
import co.touchlab.touchkit.rk.ui.ViewWebDocumentActivity;

public class ConsentSharingStepFragment extends SingleChoiceQuestionStepFragment
{

    public ConsentSharingStepFragment()
    {
        super();
    }

    public static Fragment newInstance(ConsentSharingStep step)
    {
        ConsentSharingStepFragment fragment = new ConsentSharingStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initMoreInfoView(TextView moreInfo)
    {
        moreInfo.setVisibility(View.VISIBLE);
        moreInfo.setText(R.string.consent_share_more_info);
        RxView.clicks(moreInfo).subscribe(v -> learnMoreAboutSharing());
    }

    private void learnMoreAboutSharing()
    {
        ConsentSharingStep step = (ConsentSharingStep) getStep();
        String title = getString(R.string.consent_learn_more);
        String docName = step.getLocalizedLearnMoreHTMLContent();
        Intent launch = ViewWebDocumentActivity.newIntent(getContext(), title, docName);
        startActivity(launch);
    }
}
