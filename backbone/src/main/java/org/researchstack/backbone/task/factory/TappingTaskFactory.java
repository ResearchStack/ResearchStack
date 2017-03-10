package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.TappingIntervalStep;
import org.researchstack.backbone.step.active.recorder.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by TheMDP on 2/23/17.
 */

public class TappingTaskFactory {

    /**
     * Returns a predefined task that consists of two finger tapping (Optionally with a hand specified)
     *
     * In a two finger tapping task, the participant is asked to rhythmically and alternately tap two
     * targets on the device screen.
     *
     * A two finger tapping task can be used to assess basic motor capabilities including speed, accuracy,
     * and rhythm.
     *
     * Data collected in this task includes touch activity and accelerometer information.
     *
     * @param context                 App or Activity context, used for getting resources
     * @param identifier              The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription  A localized string describing the intended use of the data
     *                                collected. If the value of this parameter is `nil`, the default
     *                                localized text will be displayed.
     * @param duration                The length of the count down timer that runs while touch data is
     *                                collected.
     * @param handOptions             Hand for determining which hand(s) to test.
     * @param optionList              Hand that affect the features of the predefined task.
     *
     * @return An active two finger tapping task that can be presented with an `ActiveTaskActivity` object.
     */
    public static OrderedTask twoFingerTappingIntervalTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            int    duration,
            HandTaskOptions.Hand handOptions,
            List<TaskExcludeOption> optionList)
    {
        // Coin toss for which hand first (in case we're doing both
        final boolean leftFirstIfDoingBoth = (new Random()).nextBoolean();

        return twoFingerTappingIntervalTask(
                context, identifier, intendedUseDescription,
                duration, handOptions, optionList, leftFirstIfDoingBoth);
    }

    // This method is separate mainly for unit testing purposes, to eliminate hand randomness
    protected static OrderedTask twoFingerTappingIntervalTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            int    duration,
            HandTaskOptions.Hand handOptions,
            List<TaskExcludeOption> optionList,
            boolean leftFirstIfDoingBoth)
    {
        List<Step> stepList = new ArrayList<>();

        String durationString = TaskFactory.convertDurationToString(context, duration);

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            String title = context.getString(R.string.rsb_TAPPING_TASK_TITLE);
            InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, intendedUseDescription);
            step.setMoreDetailText(context.getString(R.string.rsb_TAPPING_INTRO_TEXT));
            step.setImage(ResUtils.Tapping.PHONE_TAPPING_NO_TAP);

            stepList.add(step);
        }

        // Setup which hand to start with and how many hands to add based on the handOptions parameter
        // Hand order is randomly determined.
        int handCount = handOptions == HandTaskOptions.Hand.BOTH ? 2 : 1;  // 2 hands for both, 1 hand for right or left
        boolean rightHand = false;
        switch (handOptions) {
            case LEFT:
                rightHand = false;
                break;
            case RIGHT:
                rightHand = true;
                break;
            case BOTH:
                rightHand = !leftFirstIfDoingBoth;
                break;
        }

        // Obtain sensor frequency for Tapping Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_tapping_task);

        // Make steps for one or both hands
        for (int hand = 1; hand <= handCount; hand++) {
            String handIdentifier = rightHand ?
                    stepIdentifierWithHandId(Instruction1StepIdentifier, ActiveTaskRightHandIdentifier) :
                    stepIdentifierWithHandId(Instruction1StepIdentifier, ActiveTaskLeftHandIdentifier)  ;

            if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
                InstructionStep step = new InstructionStep(handIdentifier, null, null);

                if (rightHand) {
                    step.setTitle(context.getString(R.string.rsb_TAPPING_TASK_TITLE_RIGHT));
                } else {
                    step.setTitle(context.getString(R.string.rsb_TAPPING_TASK_TITLE_LEFT));
                }

                // Set the instructions for the tapping test screen that is displayed prior to each hand test
                String restText = context.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_REST_PHONE);
                String tappingTextFormat = context.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_FORMAT);
                String tappingText = String.format(tappingTextFormat, durationString);
                String handText = null;

                if (hand == 1) {  // first hand
                    if (rightHand) {
                        handText = context.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_RIGHT_FIRST);
                    } else {
                        handText = context.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_LEFT_FIRST);
                    }
                } else {
                    if (rightHand) {
                        handText = context.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_RIGHT_SECOND);
                    } else {
                        handText = context.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_LEFT_SECOND);
                    }
                }

                step.setText(String.format(Locale.getDefault(), "%s %s %s", restText, handText, tappingText));

                // Continue button will be different from first hand and second hand
                if (hand == 1) {
                    step.setMoreDetailText(context.getString(R.string.rsb_TAPPING_CALL_TO_ACTION));
                } else {
                    step.setMoreDetailText(context.getString(R.string.rsb_TAPPING_CALL_TO_ACTION_NEXT));
                }

                // Set the image
                if (rightHand) {
                    step.setImage(ResUtils.Tapping.ANIMATED_TAPPING_RIGHT);
                } else {
                    step.setImage(ResUtils.Tapping.ANIMATED_TAPPING_LEFT);
                }
                step.setIsImageAnimated(true);
                // The ANIMATED_TAPPING assets repeat at this duration
                long animDuration = 2 * context.getResources().getInteger(R.integer.rsb_config_tapping_duration_half);
                step.setAnimationRepeatDuration(animDuration);

                stepList.add(step);
            }

            // TAPPING STEP
            {
                List<RecorderConfig> recorderConfigList = new ArrayList<>();
                if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                }

                String tappingHandIdentifier = rightHand ?
                        stepIdentifierWithHandId(TappingStepIdentifier, ActiveTaskRightHandIdentifier) :
                        stepIdentifierWithHandId(TappingStepIdentifier, ActiveTaskLeftHandIdentifier)  ;

                TappingIntervalStep step = new TappingIntervalStep(tappingHandIdentifier);

                if (rightHand) {
                    step.setTitle(context.getString(R.string.rsb_TAPPING_INSTRUCTION_RIGHT));
                } else {
                    step.setTitle(context.getString(R.string.rsb_TAPPING_INSTRUCTION_LEFT));
                }

                step.setStepDuration(duration);
                step.setShouldContinueOnFinish(true);
                step.setRecorderConfigurationList(recorderConfigList);
                step.setOptional(handCount == 2);

                stepList.add(step);
            }

            // Flip to the other hand (ignored if handCount == 1)
            rightHand = !rightHand;
        }

        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }

        return new OrderedTask(identifier, stepList);
    }

    public static String stepIdentifierWithHandId(String stepId, String handId) {
        if (handId == null) {
            return stepId;
        }
        return String.format("%s.%s", stepId, handId);
    }
}
