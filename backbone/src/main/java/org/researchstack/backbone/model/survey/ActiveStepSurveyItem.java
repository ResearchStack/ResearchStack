package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by TheMDP on 12/31/16.
 */

public class ActiveStepSurveyItem extends SurveyItem<String> {

    @SerializedName("stepSpokenInstruction")
    private String stepSpokenInstruction;

    @SerializedName("stepFinishedSpokenInstruction")
    private String stepFinishedSpokenInstruction;

    @SerializedName(value="stepDuration", alternate={"duration"})
    private int    stepDuration;

    /**
     * A map of <"time_in_seconds_to_speak", "what_to_speak">
     */
    @SerializedName("spokenInstructions")
    private Map<String, String> spokenInstructionMap;

    /**
     * A string representation of a raw file resource,
     * it will play on start() of ActiveStepLayout
     */
    @SerializedName("soundRes")
    private String soundRes;

    /* Default constructor needed for serilization/deserialization of object */
    public ActiveStepSurveyItem() {
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

    public Map<String, String> getSpokenInstructionMap() {
        return spokenInstructionMap;
    }

    public void setSpokenInstructionMap(Map<String, String> spokenInstructions) {
        spokenInstructionMap = spokenInstructions;
    }

    public String getSoundRes() {
        return soundRes;
    }

    public void setSoundRes(String soundRes) {
        this.soundRes = soundRes;
    }
}
