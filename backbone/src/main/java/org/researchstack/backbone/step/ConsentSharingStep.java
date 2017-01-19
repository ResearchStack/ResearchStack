package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;

/**
 * This class represents a question step that includes prepopulated content that asks users about
 * how much they're willing to allow data to be shared after collection.
 */
public class ConsentSharingStep extends QuestionStep {

    public ConsentSharingStep(String identifier) {
        super(identifier);
        setOptional(false);
    }

    @Override
    public Class getStepBodyClass() {
        return SingleChoiceQuestionBody.class;
    }


}
