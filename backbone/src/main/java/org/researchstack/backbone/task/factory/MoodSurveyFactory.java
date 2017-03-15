package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.MoodScaleAnswerFormat;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by TheMDP on 3/14/17.
 */

public class MoodSurveyFactory {

    public static final String MoodSurveyCustomQuestionStepIdentifier   = "mood.custom";
    public static final String MoodSurveyClarityQuestionStepIdentifier  = "mood.clarity";
    public static final String MoodSurveyOverallQuestionStepIdentifier  = "mood.overall";
    public static final String MoodSurveySleepQuestionStepIdentifier    = "mood.sleep";
    public static final String MoodSurveyExerciseQuestionStepIdentifier = "mood.exercise";
    public static final String MoodSurveyPainQuestionStepIdentifier     = "mood.pain";

    /**
     * Returns a predefined survey that asks the user questions about their mood and general health.
     *
     * The mood survey includes questions about the daily or weekly mental and physical health status and
     * includes asking about clarity of thinking, overall mood, pain, sleep and exercise. Additionally,
     * the survey is setup to allow for an optional custom question that uses a similar-looking set of images
     * as the other questions.
     *
     * @param context                 Can be app or activity, used to grab resources
     * @param identifier              The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription  A localized string describing the intended use of the data
     *                                collected. If the value of this parameter is `nil`, the default
     *                                localized text is displayed.
     * @param frequency               How frequently the survey is asked (daily or weekly)
     * @param customQuestionText      A localized string to use for a custom question. If `nil`, this step
     *                                is not included.
     * @param optionList              Options that affect the features of the predefined task.
     *
     * @return An mood survey that can be presented with an `ORKTaskViewController` object.
     */
    public static OrderedTask moodSurvey(
            Context context,
            String identifier,
            String intendedUseDescription,
            MoodSurveyFrequency frequency,
            String customQuestionText,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            String title = (frequency == MoodSurveyFrequency.DAILY) ?
                    context.getString(R.string.rsb_MOOD_SURVEY_INTRO_DAILY_TITLE) :
                    context.getString(R.string.rsb_MOOD_SURVEY_INTRO_WEEKLY_TITLE);
            String text = intendedUseDescription;
            if (text == null) {
                text = (frequency == MoodSurveyFrequency.DAILY) ?
                        context.getString(R.string.rsb_MOOD_SURVEY_INTRO_DAILY_TEXT) :
                        context.getString(R.string.rsb_MOOD_SURVEY_INTRO_WEEKLY_TEXT);
            }
            InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, text);
            step.setMoreDetailText(context.getString(R.string.rsb_MOOD_SURVEY_INTRO_DETAIL));
            stepList.add(step);
        }

        // Custom
        if (customQuestionText != null) {
            stepList.add(moodQuestionStep(context, MoodSurveyCustomQuestionStepIdentifier,
                    customQuestionText, MoodScaleAnswerFormat.MoodQuestionType.CUSTOM));
        }

        // Clarity
        {
            String title = (frequency == MoodSurveyFrequency.DAILY) ?
                    context.getString(R.string.rsb_MOOD_CLARITY_DAILY_PROMPT) :
                    context.getString(R.string.rsb_MOOD_CLARITY_WEEKLY_PROMPT);
            stepList.add(moodQuestionStep(context, MoodSurveyClarityQuestionStepIdentifier,
                    title, MoodScaleAnswerFormat.MoodQuestionType.CLARITY));
        }

        // Overall
        {
            String title = (frequency == MoodSurveyFrequency.DAILY) ?
                    context.getString(R.string.rsb_MOOD_OVERALL_DAILY_PROMPT) :
                    context.getString(R.string.rsb_MOOD_OVERALL_WEEKLY_PROMPT);
            stepList.add(moodQuestionStep(context, MoodSurveyOverallQuestionStepIdentifier,
                    title, MoodScaleAnswerFormat.MoodQuestionType.OVERALL));
        }

        // Pain
        {
            String title = (frequency == MoodSurveyFrequency.DAILY) ?
                    context.getString(R.string.rsb_MOOD_PAIN_DAILY_PROMPT) :
                    context.getString(R.string.rsb_MOOD_PAIN_WEEKLY_PROMPT);
            stepList.add(moodQuestionStep(context, MoodSurveyPainQuestionStepIdentifier,
                    title, MoodScaleAnswerFormat.MoodQuestionType.PAIN));
        }

        // Sleep
        {
            String title = (frequency == MoodSurveyFrequency.DAILY) ?
                    context.getString(R.string.rsb_MOOD_SLEEP_DAILY_PROMPT) :
                    context.getString(R.string.rsb_MOOD_SLEEP_WEEKLY_PROMPT);
            stepList.add(moodQuestionStep(context, MoodSurveySleepQuestionStepIdentifier,
                    title, MoodScaleAnswerFormat.MoodQuestionType.SLEEP));
        }

        // Exercise
        {
            String title = (frequency == MoodSurveyFrequency.DAILY) ?
                    context.getString(R.string.rsb_MOOD_EXERCISE_DAILY_PROMPT) :
                    context.getString(R.string.rsb_MOOD_EXERCISE_WEEKLY_PROMPT);
            stepList.add(moodQuestionStep(context, MoodSurveyExerciseQuestionStepIdentifier,
                    title, MoodScaleAnswerFormat.MoodQuestionType.EXERCISE));
        }

        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }

        return new OrderedTask(identifier, stepList);
    }

    /**
     * @param context can be app or activity, used for string resources in MoodScaleAnswerFormat
     * @param identifier identifier of the QuestionStep
     * @param title title of the QuestionStep
     * @param type MoodQuestionType for the MoodScaleAnswerFormat
     * @return a QuestionStep for the Mood Survey
     */
    private static QuestionStep moodQuestionStep(
            Context context,
            String identifier,
            String title,
            MoodScaleAnswerFormat.MoodQuestionType type)
    {
        AnswerFormat answerFormat = new MoodScaleAnswerFormat(context, type);
        return new QuestionStep(identifier, title, answerFormat);
    }
}
