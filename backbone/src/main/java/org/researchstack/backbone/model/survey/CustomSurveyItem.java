package org.researchstack.backbone.model.survey;

/**
 * Created by TheMDP on 1/12/17.
 */

public class CustomSurveyItem<T> extends SurveyItem<T> {

    String customSurveyItemIdentifer;

    /* Default constructor needed for serilization/deserialization of object */
    CustomSurveyItem() {
        super();
    }

    @Override
    public String getTypeIdentifier() {
        return customSurveyItemIdentifer;
    }
}
