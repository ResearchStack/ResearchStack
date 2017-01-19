package org.researchstack.backbone.model.survey;

/**
 * Created by TheMDP on 1/3/17.
 */

public class CompoundQuestionSurveyItem extends QuestionSurveyItem<QuestionSurveyItem> {

    /* Default constructor needed for serilization/deserialization of object */
    CompoundQuestionSurveyItem() {
        super();
    }

    /**
     * @return false by default, true if any of the sub-questions use navigation
     */
    @Override
    public boolean usesNavigation() {
        boolean usesNavigation = super.usesNavigation();
        if (usesNavigation) {
            return true;
        }
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (SurveyItem item : items) {
            if (item instanceof QuestionSurveyItem) {
                usesNavigation = ((QuestionSurveyItem) item).usesNavigation();
                if (usesNavigation) {
                    return true;
                }
            }
        }
        return false;
    }
}
