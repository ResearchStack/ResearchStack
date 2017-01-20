package org.researchstack.skin.utils;

import android.content.Context;
import android.util.Log;

import org.researchstack.skin.R;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentQuizModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 12/15/16.
 *
 * This class the business logic of interacting with the ConsentQuizModel object
 */

@Deprecated // We no long need this class now that we have deprecated ConsentQuizModel
public class ConsentQuizQuestionUtils {

    static final String LOG_TAG = ConsentQuizQuestionUtils.class.getCanonicalName();

    /**
     * @param ctx - Used to access String resources when building question Strings
     * @param question - Question to create choices from
     * @return A list of choices which can be fabricated in various ways
     */
    public static List<Choice> createChoices(Context ctx, ConsentQuizModel.QuizQuestion question) {

        if (question == null) {
            Log.e(LOG_TAG, "Question was null, returning empty list");
            return new ArrayList<>();
        }

        switch (question.getType()) {
            case BOOLEAN:
                return createBooleanChoices(ctx, question);
            case SINGLE_CHOICE_TEXT:
                return createSingleTextChoices(question);
        }

        return new ArrayList<>();
    }

    static List<Choice> createSingleTextChoices(ConsentQuizModel.QuizQuestion question) {
        if (question.getItems() != null && !question.getItems().isEmpty()) {
            return new ArrayList<>(question.getItems());
        } else if (question.getTextChoices() != null && !question.getTextChoices().isEmpty()) {
            List<Choice> choices = new ArrayList();
            for (int i = 0; i < question.getTextChoices().size(); i++) {
                choices.add(new Choice(question.getTextChoices().get(i), String.valueOf(i)));
            }
            return choices;
        }
        return new ArrayList<>();
    }

    static List<Choice> createBooleanChoices(Context ctx, ConsentQuizModel.QuizQuestion question) {
        List<Choice> choices = new ArrayList();
        choices.add(new Choice<>(ctx.getString(R.string.rss_btn_true), "true"));
        choices.add(new Choice<>(ctx.getString(R.string.rss_btn_false), "false"));
        return choices;
    }
}
