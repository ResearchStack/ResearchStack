package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 1/3/17.
 *
 * Represents a slider that can go from integer min to integer high, at a certain step value
 */

public class ScaleQuestionSurveyItem extends IntegerRangeSurveyItem {
    @SerializedName("step")
    public int step;

    /* Default constructor needed for serilization/deserialization of object */
    ScaleQuestionSurveyItem() {
        super();
    }
}
