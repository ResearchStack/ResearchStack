package org.researchstack.backbone.model.survey;

/**
 * Created by TheMDP on 12/31/16.
 */

public class ActiveStepSurveyItem extends SurveyItem<String> {
    String stepSpokenInstruction;
    String stepFinishedSpokenInstruction;

    /* Default constructor needed for serilization/deserialization of object */
    ActiveStepSurveyItem() {
        super();
    }
}
