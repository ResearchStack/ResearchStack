package org.researchstack.backbone.model.survey;

import org.researchstack.backbone.model.Choice;

/**
 * Created by TheMDP on 1/3/17.
 */

public class BooleanQuestionSurveyItem extends QuestionSurveyItem<Choice<Boolean>> {
    /* Default constructor needed for serialization/deserialization of object */
    public BooleanQuestionSurveyItem() {
        super();
    }
}
