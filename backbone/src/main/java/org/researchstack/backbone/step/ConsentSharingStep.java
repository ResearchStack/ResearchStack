package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;

public class ConsentSharingStep extends QuestionStep
{

    public ConsentSharingStep(String identifier)
    {
        super(identifier);
    }

    @Override
    public Class getStepBodyClass()
    {
        return SingleChoiceQuestionBody.class;
    }


}
