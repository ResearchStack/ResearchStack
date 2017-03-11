package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/31/16.
 */

public class ActiveStepSurveyItem extends SurveyItem<String> {

    @SerializedName("stepSpokenInstruction")
    private String stepSpokenInstruction;

    @SerializedName("stepFinishedSpokenInstruction")
    private String stepFinishedSpokenInstruction;

    @SerializedName("stepDuration")
    private int    stepDuration;

    /* Default constructor needed for serilization/deserialization of object */
    ActiveStepSurveyItem() {
        super();
    }

    public String getStepSpokenInstruction() {
        return stepSpokenInstruction;
    }

    public void setStepSpokenInstruction(String stepSpokenInstruction) {
        this.stepSpokenInstruction = stepSpokenInstruction;
    }

    public String getStepFinishedSpokenInstruction() {
        return stepFinishedSpokenInstruction;
    }

    public void setStepFinishedSpokenInstruction(String stepFinishedSpokenInstruction) {
        this.stepFinishedSpokenInstruction = stepFinishedSpokenInstruction;
    }

    public int getStepDuration() {
        return stepDuration;
    }

    public void setStepDuration(int stepDuration) {
        this.stepDuration = stepDuration;
    }
}
