package org.researchstack.backbone.model.survey;

import java.io.Serializable;

/**
 * Created by TheMDP on 1/12/17.
 */

public class CustomSurveyItem<T extends Serializable> extends SurveyItem<T> {

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
