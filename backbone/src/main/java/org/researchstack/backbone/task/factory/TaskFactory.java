package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.CompletionStep;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.ConclusionStepIdentifier;

/**
 * Created by TheMDP on 2/15/17.
 *
 * Central location for the constants shared by the task factory
 */

public class TaskFactory {

    public static class Constants {
        // Recorder Config Identifiers
        public static final String Accelerometer1ConfigIdentifier = "ac1_acc";
        public static final String DeviceMotion1ConfigIdentifier = "ac1_motion";
        public static final String Accelerometer2ConfigIdentifier = "ac2_acc";
        public static final String DeviceMotion2ConfigIdentifier = "ac2_motion";
        public static final String Accelerometer3ConfigIdentifier = "ac3_acc";
        public static final String DeviceMotion3ConfigIdentifier = "ac3_motion";
        public static final String Accelerometer4ConfigIdentifier = "ac4_acc";
        public static final String DeviceMotion4ConfigIdentifier = "ac4_motion";
        public static final String Accelerometer5ConfigIdentifier = "ac5_acc";
        public static final String DeviceMotion5ConfigIdentifier = "ac5_motion";
        public static final String AccelerometerRecorderIdentifier = "accelerometer";
        public static final String PedometerRecorderIdentifier = "pedometer";
        public static final String DeviceMotionRecorderIdentifier = "deviceMotion";

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

        // Conclusion Step Identifiers
        public static final String ConclusionStepIdentifier = "conclusion";
    }

    public static CompletionStep makeCompletionStep(Context context) {
        String title = context.getString(R.string.rsb_TASK_COMPLETE_TITLE);
        String text = context.getString(R.string.rsb_TASK_COMPLETE_TEXT);
        return new CompletionStep(ConclusionStepIdentifier, title, text);
    }
}
