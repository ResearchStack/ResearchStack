package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/15/16.
 */

public enum ConsentQuestionType {

    @SerializedName("boolean")
    BOOLEAN("boolean"),
    @SerializedName("singleChoiceText")
    SINGLE_CHOICE_TEXT("singleChoiceText"),
    @SerializedName("instruction")
    INSTRUCTION("instruction");

    String mIdentifier;

    ConsentQuestionType(String questionId) {
        mIdentifier = questionId;
    }

    public String getIdentifier() {
        return mIdentifier;
    }
}
