package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 1/3/17.
 */

public class FormSurveyItem extends QuestionSurveyItem<SurveyItem> {

    /**
     * When the expectedAnswer is this String, skipToStepIdentifier will be invoked
     * If the StepResult does not contain any valid results, aka "Skip" button was clicked
     */
    public static final String SKIP_BUTTON_TAPPED_ACTION_IDENTIFIER = "whenSkipButtonClicked";

    @SerializedName("skipTitle")
    public String skipTitle;

    /**
     * If true, the first question body layout with an edittext will receive focus on load
     * default is false and nothing will occur
     */
    @SerializedName("autoFocusFirst")
    public Boolean autoFocusFirstEditText;

    /* Default constructor needed for serilization/deserialization of object */
    public FormSurveyItem() {
        super();
    }
}
