package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.Choice;

/**
 * Created by TheMDP on 1/3/17.
 */

public class ConsentSharingOptionsSurveyItem extends SurveyItem<Choice<Boolean>> {
    @SerializedName("investigatorShortDescription")
    public String investigatorShortDescription;
    @SerializedName("investigatorLongDescription")
    public String investigatorLongDescription;
    @SerializedName("learnMoreHTMLContentURL")
    public String learnMoreHTMLContentURL;

    /* Default constructor needed for serilization/deserialization of object */
    ConsentSharingOptionsSurveyItem() {
        super();
    }
}
