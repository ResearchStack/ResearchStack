package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/15/16.
 */

@Deprecated // no longer needed since ConsentQuizModel is deprecated
public enum ConsentQuestionType {

    @SerializedName("boolean")
    BOOLEAN("boolean"),
    @SerializedName("singleChoiceText")
    SINGLE_CHOICE_TEXT("singleChoiceText"),
    @SerializedName("instruction")
    INSTRUCTION("instruction");

    ConsentQuestionType(String questionId) {
        mIdentifier = questionId;
    }

    String mIdentifier;
    public String getIdentifier() {
        return mIdentifier;
    }
}
