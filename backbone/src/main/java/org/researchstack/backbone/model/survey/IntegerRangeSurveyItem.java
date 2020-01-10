package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 1/3/17.
 */

public class IntegerRangeSurveyItem extends RangeSurveyItem<Integer> {

    @SerializedName("maxLength")
    public Integer maxLength;

    /* Default constructor needed for serilization/deserialization of object */
    public IntegerRangeSurveyItem() {
        super();
    }
}
