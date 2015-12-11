package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.step.ConsentSharingStep;
import co.touchlab.researchstack.core.ui.ViewWebDocumentActivity;

public class ConsentSharingScene extends SingleChoiceQuestionScene
{

    public ConsentSharingScene(Context context)
    {
        super(context);
    }

    public ConsentSharingScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentSharingScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSceneCreated(View scene)
    {
        super.onSceneCreated(scene);
        super.setMoreInfo(R.string.consent_share_more_info, o -> learnMoreAboutSharing());
    }

    private void learnMoreAboutSharing()
    {
        ConsentSharingStep step = (ConsentSharingStep) getStep();
        String title = getString(R.string.consent_learn_more);
        String docName = step.getLocalizedLearnMoreHTMLContent();
        Intent launch = ViewWebDocumentActivity.newIntent(getContext(), title, docName);
        getContext().startActivity(launch);
    }
}
