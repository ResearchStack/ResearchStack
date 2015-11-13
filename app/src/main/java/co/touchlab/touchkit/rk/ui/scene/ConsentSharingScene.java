package co.touchlab.touchkit.rk.ui.scene;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.step.ConsentSharingStep;
import co.touchlab.touchkit.rk.ui.ViewWebDocumentActivity;

public class ConsentSharingScene extends SingleChoiceQuestionScene
{

    public ConsentSharingScene(Context context, ConsentSharingStep step)
    {
        super(context, step);
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
