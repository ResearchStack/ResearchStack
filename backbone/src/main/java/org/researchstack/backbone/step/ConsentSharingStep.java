package org.researchstack.backbone.step;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;

/**
 * This class represents a question step that includes prepopulated content that asks users about
 * how much they're willing to allow data to be shared after collection.
 */
public class ConsentSharingStep extends QuestionStep {
    /* Default constructor needed for serilization/deserialization of object */
    ConsentSharingStep() {
        super();
    }

    public ConsentSharingStep(String identifier) {
        super(identifier);
        setOptional(false);
    }

    public ConsentSharingStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
        setOptional(false);
    }

    @Override
    public Class getStepBodyClass() {
        return SingleChoiceQuestionBody.class;
    }
}
