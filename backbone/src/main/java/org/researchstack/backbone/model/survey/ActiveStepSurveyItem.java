package org.researchstack.backbone.model.survey;

/**
 * Created by TheMDP on 12/31/16.
 */

public class ActiveStepSurveyItem extends SurveyItem<String> {
    private String stepSpokenInstruction;
    private String stepFinishedSpokenInstruction;

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
}
