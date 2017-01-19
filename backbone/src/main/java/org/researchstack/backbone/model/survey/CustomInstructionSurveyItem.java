package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 1/12/17.
 */

public class CustomInstructionSurveyItem extends CustomSurveyItem {
    @SerializedName("detailText")
    public String detailText;

    @SerializedName("image")
    public String image;

    @SerializedName("iconImage")
    public String iconImage;

    /**
     * Pointer to the next step to show after this one. If nil, then the next step
     * is determined by the navigation rules setup by NavigableOrderedTask.
     */
    @SerializedName("nextIdentifier")
    public String nextIdentifier;

    @SerializedName("learnMoreHTMLContentURL")
    public String learnMoreHTMLContentURL;

    /* Default constructor needed for serilization/deserialization of object */
    CustomInstructionSurveyItem() {
        super();
    }

    public boolean usesNavigation() {
        return nextIdentifier != null;
    }
}
