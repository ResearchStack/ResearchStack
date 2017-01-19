package org.researchstack.backbone.model.survey;

/**
 * Created by TheMDP on 1/3/17.
 *
 * This class represents a question survey item that has QuestionSurveyItems as its items
 * that will be QuestionSurveyItems with surveyType of QUESTION_BOOLEAN
 */

public class ToggleQuestionSurveyItem extends QuestionSurveyItem<BooleanQuestionSurveyItem> {
    /* Default constructor needed for serilization/deserialization of object */
    ToggleQuestionSurveyItem() {
        super();
    }
}
