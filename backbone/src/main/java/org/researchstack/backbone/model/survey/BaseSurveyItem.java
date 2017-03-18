package org.researchstack.backbone.model.survey;

import java.io.Serializable;

/**
 * Created by TheMDP on 1/2/17.
 */

public class BaseSurveyItem extends SurveyItem<Serializable> {
    /* Default constructor needed for serilization/deserialization of object */
    BaseSurveyItem() {
        super();
    }
}
