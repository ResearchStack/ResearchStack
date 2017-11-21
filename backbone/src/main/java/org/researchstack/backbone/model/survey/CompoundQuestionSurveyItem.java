package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 1/3/17.
 */

public class CompoundQuestionSurveyItem extends QuestionSurveyItem<SurveyItem> {

    /**
     * When the expectedAnswer is this String, skipToStepIdentifier will be invoked
     * If the StepResult does not contain any valid results, aka "Skip" button was clicked
     */
    public static final String SKIP_BUTTON_TAPPED_ACTION_IDENTIFIER = "whenSkipButtonClicked";

    @SerializedName("skipTitle")
    public String skipTitle;

    /* Default constructor needed for serilization/deserialization of object */
    public CompoundQuestionSurveyItem() {
        super();
    }
}
