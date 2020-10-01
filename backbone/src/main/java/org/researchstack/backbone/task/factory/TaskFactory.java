package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.CompletionStep;

import java.util.Locale;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.ConclusionStepIdentifier;

/**
 * Created by TheMDP on 2/15/17.
 *
 * Central location for the constants shared by the task factory
 */

public class TaskFactory {

    public static class Constants {
        // Recorder Config Identifiers
        public static final String AccelerometerRecorderIdentifier  = "accel";
        public static final String PedometerRecorderIdentifier      = "pedometer";
        public static final String DeviceMotionRecorderIdentifier   = "deviceMotion";
        public static final String LocationRecorderIdentifier       = "location";
        public static final String AudioRecorderIdentifier          = "audio";

        // Step Identifiers for instructions
        public static final String Instruction0StepIdentifier = "instruction";
        public static final String Instruction1StepIdentifier = "instruction1";
        public static final String Instruction2StepIdentifier = "instruction2";
        public static final String Instruction3StepIdentifier = "instruction3";
        public static final String Instruction4StepIdentifier = "instruction4";
        public static final String Instruction5StepIdentifier = "instruction5";
        public static final String Instruction6StepIdentifier = "instruction6";
        public static final String Instruction7StepIdentifier = "instruction7";

        // Countdown identifiers
        public static final String CountdownStepIdentifier = "countdown";
        public static final String Countdown1StepIdentifier = "countdown1";
        public static final String Countdown2StepIdentifier = "countdown2";
        public static final String Countdown3StepIdentifier = "countdown3";
        public static final String Countdown4StepIdentifier = "countdown4";
        public static final String Countdown5StepIdentifier = "countdown5";

        // Tapping Identifiers
        public static final String TappingStepIdentifier = "tapping";

        // Conclusion Step Identifiers
        public static final String ConclusionStepIdentifier = "conclusion";

        // Active Task Steps Hand Identifier
        public static final String ActiveTaskMostAffectedHandIdentifier = "mostAffected";
        public static final String ActiveTaskLeftHandIdentifier         = "left";
        public static final String ActiveTaskRightHandIdentifier        = "right";
        public static final String ActiveTaskSkipHandStepIdentifier     = "skipHand";
    }

    public static CompletionStep makeCompletionStep(Context context) {
        String title = context.getString(R.string.rsb_TASK_COMPLETE_TITLE);
        String text = context.getString(R.string.rsb_TASK_COMPLETE_TEXT);
        return new CompletionStep(ConclusionStepIdentifier, title, text);
    }

    /**
     * In iOS, this method turns duration into "for X minutes, Y seconds" but in Android,
     * you can only localize a duration to be  "in X minutes, Y seconds", so use that instead
     * @param context can be app or activity, need for resources
     * @param durationInSeconds the duration in seconds
     * @return a string formatted to "in X minutes, Y seconds" where x & y are from durationInSeconds
     */
    public static String convertDurationToString(Context context, int durationInSeconds) {
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds - minutes * 60;
        if (minutes > 0) {
            return String.format(Locale.getDefault(), "%d %s, %d %s",
                    minutes, context.getString(R.string.rsb_time_minutes).toLowerCase(),
                    seconds, context.getString(R.string.rsb_time_seconds).toLowerCase());
        } else {
            return String.format(Locale.getDefault(), "%d %s",
                    seconds, context.getString(R.string.rsb_time_seconds).toLowerCase());
        }
    }
}
