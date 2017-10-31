package org.researchstack.backbone.model.survey;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/31/16.
 */

public class InstructionSurveyItem extends SurveyItem<String> {

    @SerializedName("detailText")
    public String detailText;

    @SerializedName("image")
    public String image;

    @SerializedName("isImageAnimated")
    public boolean isImageAnimated;

    @SerializedName("animationRepeatDuration")
    public long animationRepeatDuration;

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

    /**
     * Defaults to centerInside, but can be any scale type in the form of
     * CENTER, CENTER_CROP, etc...
     */
    @SerializedName("scaleType")
    public ImageView.ScaleType scaleType;

    /* Default constructor needed for serialization/deserialization of object */
    public InstructionSurveyItem() {
        super();
    }

    public boolean usesNavigation() {
        return nextIdentifier != null;
    }
}
