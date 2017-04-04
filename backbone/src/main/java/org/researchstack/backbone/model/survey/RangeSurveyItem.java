package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/31/16.
 */

public class RangeSurveyItem<T> extends QuestionSurveyItem {
    @SerializedName("min")
    public T min;
    @SerializedName("max")
    public T max;
    @SerializedName("defaultValue")
    public T defaultValue;
    @SerializedName("unit")
    public String unit;

    /* Default constructor needed for serialization/deserialization of object */
    RangeSurveyItem() {
        super();
    }
}
