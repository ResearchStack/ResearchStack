package org.researchstack.backbone.answerformat;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.model.Choice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 */

public class GenderAnswerFormat extends ChoiceAnswerFormat {

    /* Default constructor needed for serilization/deserialization of object */
    GenderAnswerFormat()
    {
        super();
    }

    public GenderAnswerFormat(Context context) {
        super(ChoiceAnswerStyle.SingleChoice, createChoices(context));
    }

    /**
     * @param context used to build strings for male, female, other
     * @return Choice array for gender
     */
    static Choice[] createChoices(Context context) {
        String female   = context.getString(R.string.rsb_gender_female);
        String male     = context.getString(R.string.rsb_gender_male);
        String other    = context.getString(R.string.rsb_gender_other);

        Choice[] genderChoices = new Choice[3];
        genderChoices[0] = new Choice(female, female);
        genderChoices[1] = new Choice(male, male);
        genderChoices[2] = new Choice(other, other);

        return genderChoices;
    }

    /**
     * Creates an answer format with the specified answerStyle(single or multichoice) and collection
     * of choices.
     *
     * @param answerStyle either MultipleChoice or SingleChoice
     * @param choices     an array of {@link Choice} objects, all of the same type
     */
    public GenderAnswerFormat(ChoiceAnswerStyle answerStyle, Choice... choices) {
        super(answerStyle, choices);
    }
}
