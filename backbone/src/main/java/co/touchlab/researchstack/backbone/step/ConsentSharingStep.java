package co.touchlab.researchstack.backbone.step;

import co.touchlab.researchstack.backbone.R;
import co.touchlab.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;

public class ConsentSharingStep extends QuestionStep
{

    public ConsentSharingStep(String identifier)
    {
        super(identifier);
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.rsc_consent;
    }

    @Override
    public Class getSceneClass()
    {
        return SingleChoiceQuestionBody.class;
    }
}
