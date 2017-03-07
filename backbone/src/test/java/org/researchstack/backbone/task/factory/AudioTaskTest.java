package org.researchstack.backbone.task.factory;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.researchstack.backbone.R;
import org.researchstack.backbone.result.AudioResult;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.AudioTooLoudStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.active.recorder.AudioRecorderSettings;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.researchstack.backbone.task.factory.AudioTaskFactory.*;
import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by TheMDP on 2/27/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class AudioTaskTest {

    private static final String PACKAGE_NAME = "org.researchstack.backbone";

    @Mock private Context mockContext;
    @Mock private Resources mockResources;
    @Mock private PackageManager mockPackageManager;

    @Before
    public void setUp() throws Exception {

        // Mocks the static variable SDK_INT to be Android.M so that Location permission checks work
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.M);
        PowerMockito.mockStatic(Log.class);

        mockContext = Mockito.mock(Context.class);
        mockResources = Mockito.mock(Resources.class);
        Mockito.when(mockContext.getResources()).thenReturn(mockResources);

        Mockito.when(mockContext.getPackageName()).thenReturn(PACKAGE_NAME);
        mockPackageManager = Mockito.mock(PackageManager.class);
        Mockito.when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
        Mockito.when(mockPackageManager.checkPermission(
                Manifest.permission.RECORD_AUDIO, PACKAGE_NAME))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        // All the strings that the TremorTask uses
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_TASK_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_INTENDED_USE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_CALL_TO_ACTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_LEVEL_CHECK_LABEL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_TOO_LOUD_MESSAGE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_TOO_LOUD_ACTION_NEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TEXT)).thenReturn("");

        Mockito.when(mockResources.getInteger(R.integer.rsb_sensor_frequency_default)).thenReturn(100);
    }

    @Test
    public void testAudioTaskSkipTooLoudStep() {
        NavigableOrderedTask task = AudioTaskFactory.audioTask(
                mockContext, "audiotaskid", "intendedUseDescription",
                "speech description", "short speech description", 8,
                AudioRecorderSettings.defaultSettings(), true,
                Arrays.asList(new TaskExcludeOption[] {}));

        List<String> stepIds = getAudioStepIdsSkipTooLoudStep();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testAudioTaskNoInstructionsSkipTooLoud() {
        NavigableOrderedTask task = AudioTaskFactory.audioTask(
                mockContext, "audiotaskid", "intendedUseDescription",
                "speech description", "short speech description", 8,
                AudioRecorderSettings.defaultSettings(), true,
                Collections.singletonList(TaskExcludeOption.INSTRUCTIONS));

        List<String> stepIds = getAudioStepIdsNoInstructions();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testAudioTaskNoConclusionSkipTooLoud() {
        NavigableOrderedTask task = AudioTaskFactory.audioTask(
                mockContext, "audiotaskid", "intendedUseDescription",
                "speech description", "short speech description", 8,
                AudioRecorderSettings.defaultSettings(), true,
                Collections.singletonList(TaskExcludeOption.CONCLUSION));

        List<String> stepIds = getAudioStepIdsNoConclusion();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testAudioTaskOneTooLoudCycle() {
        NavigableOrderedTask task = AudioTaskFactory.audioTask(
                mockContext, "audiotaskid", "intendedUseDescription",
                "speech description", "short speech description", 8,
                AudioRecorderSettings.defaultSettings(), true,
                Arrays.asList(new TaskExcludeOption[] {}));

        TaskResult taskTooLoudResult = new TaskResult("audiotaskid");
        TaskResult taskNotTooLoudResult = new TaskResult("audiotaskid");

        List<String> stepIds = getAudioStepIdsOneTooLoudCycle();
        Step step = null;
        int i = 0;
        boolean firstCycleComplete = false;
        do {
            TaskResult taskResult = taskTooLoudResult;
            if (firstCycleComplete) {
                taskResult = taskNotTooLoudResult;
            }

            step = task.getStepAfterStep(step, taskResult);

            if (step != null) {

                if (step instanceof AudioTooLoudStep) {
                    firstCycleComplete = true;
                }

                if (step instanceof CountdownStep) {
                    {
                        StepResult<Result> tooLoudResult = new StepResult<>(step);
                        AudioResult audioResult = new AudioResult(AudioRecorderIdentifier);
                        audioResult.setRollingAverageOfVolume(AudioTaskFactory.LOUDNESS_THRESHOLD + .1);
                        tooLoudResult.getResults().put(audioResult.getIdentifier(), audioResult);
                        taskTooLoudResult.getResults().put(tooLoudResult.getIdentifier(), tooLoudResult);
                    }

                    {
                        StepResult<Result> notTooLoudResult = new StepResult<>(step);
                        AudioResult audioResult = new AudioResult(AudioRecorderIdentifier);
                        audioResult.setRollingAverageOfVolume(AudioTaskFactory.LOUDNESS_THRESHOLD - .1);
                        notTooLoudResult.getResults().put(audioResult.getIdentifier(), audioResult);
                        taskNotTooLoudResult.getResults().put(notTooLoudResult.getIdentifier(), notTooLoudResult);
                    }
                }

                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testAudioTaskWithNoPermission() {
        Mockito.when(mockPackageManager.checkPermission(
                Manifest.permission.RECORD_AUDIO, PACKAGE_NAME))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        NavigableOrderedTask task = AudioTaskFactory.audioTask(
                mockContext, "audiotaskid", "intendedUseDescription",
                "speech description", "short speech description", 8,
                AudioRecorderSettings.defaultSettings(), true,
                Arrays.asList(new TaskExcludeOption[] {}));

        List<String> stepIds = getAudioStepIdsWithNoPermission();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);

        Mockito.when(mockPackageManager.checkPermission(
                Manifest.permission.RECORD_AUDIO, PACKAGE_NAME))
                .thenReturn(PackageManager.PERMISSION_DENIED);
    }

    private List<String> getAudioStepIdsSkipTooLoudStep() {
        return new LinkedList<>(Arrays.asList(
                MicrophonePermissionsStepIdentifier,
                Instruction0StepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                AudioStepIdentifier,
                ConclusionStepIdentifier));
    }

    private List<String> getAudioStepIdsOneTooLoudCycle() {
        return new LinkedList<>(Arrays.asList(
                MicrophonePermissionsStepIdentifier,
                Instruction0StepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                AudioTooLoudStepIdentifier,
                CountdownStepIdentifier,
                AudioStepIdentifier,
                ConclusionStepIdentifier));
    }

    private List<String> getAudioStepIdsNoInstructions() {
        return new LinkedList<>(Arrays.asList(
                MicrophonePermissionsStepIdentifier,
                CountdownStepIdentifier,
                AudioStepIdentifier,
                ConclusionStepIdentifier));
    }

    private List<String> getAudioStepIdsNoConclusion() {
        return new LinkedList<>(Arrays.asList(
                MicrophonePermissionsStepIdentifier,
                Instruction0StepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                AudioStepIdentifier));
    }

    private List<String> getAudioStepIdsWithNoPermission() {
        return new LinkedList<>(Arrays.asList(
                Instruction0StepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                AudioStepIdentifier,
                ConclusionStepIdentifier));
    }

    // Cool trick method to change a static field's value
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
