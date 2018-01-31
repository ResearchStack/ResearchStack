package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TheMDP on 12/31/16.
 */

public class QuestionSurveyItem<T extends Serializable> extends SurveyItem<T> {

    @SerializedName("questionStyle")
    public boolean questionStyle;
    @SerializedName("placeholderText")
    public String placeholderText;
    @SerializedName("optional")
    public boolean optional;
    @SerializedName("range")
    public RangeSurveyItem range;

    @SerializedName(value="expectedAnswer", alternate={"matchingAnswer"})
    public Object expectedAnswer;

    @SerializedName("skipIdentifier")
    public String skipIdentifier;

    @SerializedName("skipIfPassed")
    public boolean skipIfPassed;

    /* Default constructor needed for serialization/deserialization of object */
    public QuestionSurveyItem() {
        super();
    }

    public boolean isValidQuestionItem() {
        return identifier != null && type.isQuestionSubtype();
    }

    public boolean isCompoundStep() {
        return type == SurveyItemType.QUESTION_FORM;
    }

    /**
     * @return false by default, true if this question survey item
     *         can be used to create a QuestionStep that will implement
     *         an interface in NavigableOrderedTask
     */
    public boolean usesNavigation() {
        if (skipIdentifier != null || expectedAnswer != null) {
            return true;
        }
        return false;
    }
}
