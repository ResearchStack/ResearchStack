package org.researchstack.backbone.task.factory;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.researchstack.backbone.R;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.recorder.LocationRecorderConfig;
import org.researchstack.backbone.step.active.recorder.PedometerRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.step.active.WalkingTaskStep;
import org.researchstack.backbone.task.OrderedTask;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;
import static org.researchstack.backbone.task.factory.WalkingTaskFactory.*;

/**
 * Created by TheMDP on 2/16/17.
 */

public class WalkingTaskTests {

    private static final String PACKAGE_NAME = "org.researchstack.backbone";

    @Mock private Context         mockContext;
    @Mock private LocationManager mockLocationManager;
    @Mock private PackageManager  mockPackageManager;
    @Mock private Resources       mockResources;

    @Before
    public void setUp() throws Exception {

        // Mocks the static variable SDK_INT to be Android.M so that Location permission checks work
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.M);

        mockContext = Mockito.mock(Context.class);
        mockResources = Mockito.mock(Resources.class);

        Mockito.when(mockContext.getPackageName()).thenReturn(PACKAGE_NAME);

        mockLocationManager = Mockito.mock(LocationManager.class);
        Mockito.when(mockContext.getSystemService(Context.LOCATION_SERVICE)).thenReturn(mockLocationManager);
        Mockito.when(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);

        mockPackageManager = Mockito.mock(PackageManager.class);
        Mockito.when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
        Mockito.when(mockPackageManager.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION, PACKAGE_NAME))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        Mockito.when(mockContext.getResources()).thenReturn(mockResources);

        // All the strings that the TremorTask uses
        Mockito.when(mockContext.getString(R.string.rsb_WALK_TASK_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_walk_intro_2_text_ld)).thenReturn("Find a place where you can safely walk unassisted for about %1$d steps in a straight line.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_2_DETAIL)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_OUTBOUND_INSTRUCTION_FORMAT)).thenReturn("Walk up to %1$d steps in a straight line.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_RETURN_INSTRUCTION_FORMAT)).thenReturn("Walk up to %1$d steps in a straight line.");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT)).thenReturn("Now stand still for %1$s.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_STAND_INSTRUCTION_FORMAT)).thenReturn("Stand still for %1$s.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT)).thenReturn("Stand still for %1$s.");

        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TEXT)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_time_minutes)).thenReturn("minutes");
        Mockito.when(mockContext.getString(R.string.rsb_time_seconds)).thenReturn("seconds");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_BACK_AND_FORTH_FINISHED_VOICE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_BACK_AND_FORTH_STAND_INSTRUCTION_FORMAT)).thenReturn("Turn in a full circle and then stand still for %1$s.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_BACK_AND_FORTH_INSTRUCTION_FORMAT)).thenReturn("Walk back and forth in a straight line for %1$s. Walk as you would normally.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_2_TEXT_BACK_AND_FORTH_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_2_DETAIL_BACK_AND_FORTH_INSTRUCTION)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_distance_meters)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_distance_feet)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_BOOL_YES)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_BOOL_NO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_permission_location_title)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_permission_location_desc)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_system_feature_gps_title)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_system_feature_gps_text)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INTRO_DETAIL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_2)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_3)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_4)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_5)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_6)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_FORM_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_FORM_TEXT)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_timed_walk_intro_2_text)).thenReturn("walk for about %1$s");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INTRO_2_DETAIL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_timed_walk_instruction)).thenReturn("Walk up to %1$s");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TURN)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_2)).thenReturn("");

        Mockito.when(mockResources.getInteger(R.integer.rsb_sensor_frequency_default)).thenReturn(100);
    }

    @Test
    public void testShortWalkTask() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Arrays.asList(new TaskExcludeOption[] {}));

        List<String> stepIds = getShortWalkStepIds();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {

                if (step.getIdentifier().equals(ShortWalkOutboundStepIdentifier) ||
                    step.getIdentifier().equals(ShortWalkReturnStepIdentifier))
                {
                    WalkingTaskStep walkingStep = (WalkingTaskStep)step;
                    boolean hasPedometer = false;
                    for (RecorderConfig config : walkingStep.getRecorderConfigurationList()) {
                        if (config instanceof PedometerRecorderConfig) {
                            hasPedometer = true;
                        }
                    }
                    assertTrue(hasPedometer);
                }

                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testShortWalkTaskNoInstructions() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.INSTRUCTIONS));

        List<String> stepIds = getShortWalkStepIds();
        stepIds.remove(Instruction0StepIdentifier);
        stepIds.remove(Instruction1StepIdentifier);
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
    public void testShortWalkTaskNoConclusion() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.CONCLUSION));

        List<String> stepIds = getShortWalkStepIds();
        stepIds.remove(ConclusionStepIdentifier);
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
    public void testShortWalkTaskNoPedometer() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.PEDOMETER));

        List<String> stepIds = getShortWalkStepIds();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                if (step.getIdentifier().equals(ShortWalkOutboundStepIdentifier) ||
                    step.getIdentifier().equals(ShortWalkReturnStepIdentifier))
                {
                    WalkingTaskStep walkingStep = (WalkingTaskStep)step;
                    for (RecorderConfig config : walkingStep.getRecorderConfigurationList()) {
                        assertFalse(config instanceof PedometerRecorderConfig);
                    }
                }
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testWalkBackAndForthTask() {
        OrderedTask task = WalkingTaskFactory.walkBackAndForthTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Arrays.asList(new TaskExcludeOption[] {}));

        List<String> stepIds = getWalkBackAndForthStepIds();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {

                if (step.getIdentifier().equals(ShortWalkOutboundStepIdentifier)) {
                    WalkingTaskStep walkingStep = (WalkingTaskStep)step;
                    boolean hasPedometer = false;
                    for (RecorderConfig config : walkingStep.getRecorderConfigurationList()) {
                        if (config instanceof PedometerRecorderConfig) {
                            hasPedometer = true;
                        }
                    }
                    assertTrue(hasPedometer);
                }

                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testWalkBackAndForthTaskNoPedometer() {
        OrderedTask task = WalkingTaskFactory.walkBackAndForthTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.PEDOMETER));

        List<String> stepIds = getWalkBackAndForthStepIds();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                if (step.getIdentifier().equals(ShortWalkOutboundStepIdentifier)) {
                    WalkingTaskStep walkingStep = (WalkingTaskStep)step;
                    for (RecorderConfig config : walkingStep.getRecorderConfigurationList()) {
                        assertFalse(config instanceof PedometerRecorderConfig);
                    }
                }
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testWalkBackAndForthTaskNoInstructions() {
        OrderedTask task = WalkingTaskFactory.walkBackAndForthTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.INSTRUCTIONS));

        List<String> stepIds = getWalkBackAndForthStepIds();
        stepIds.remove(Instruction0StepIdentifier);
        stepIds.remove(Instruction1StepIdentifier);
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
    public void testWalkBackAndForthTaskNoConclusion() {
        OrderedTask task = WalkingTaskFactory.walkBackAndForthTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.CONCLUSION));

        List<String> stepIds = getWalkBackAndForthStepIds();
        stepIds.remove(ConclusionStepIdentifier);
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
    public void testTimedWalk() {
        OrderedTask task = WalkingTaskFactory.timedWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50.0, 60, 5, true, new ArrayList<TaskExcludeOption>());

        List<String> stepIds = getTimedWalkStepIds();
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
    public void testTimedWalkWithoutLocationPermissionStep() {

        Mockito.when(mockPackageManager.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION, PACKAGE_NAME))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        OrderedTask task = WalkingTaskFactory.timedWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50.0, 60, 5, true, new ArrayList<TaskExcludeOption>());

        List<String> stepIds = getTimedWalkStepIds();
        stepIds.remove(LocationPermissionsStepIdentifier);
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
                Manifest.permission.ACCESS_FINE_LOCATION, PACKAGE_NAME))
                .thenReturn(PackageManager.PERMISSION_DENIED);
    }

    @Test
    public void testTimedWalkGpsOn() {
        Mockito.when(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);

        OrderedTask task = WalkingTaskFactory.timedWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50.0, 60, 5, true, new ArrayList<TaskExcludeOption>());

        List<String> stepIds = getTimedWalkStepIds();
        stepIds.remove(GpsFeatureStepIdentifier);
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);

        Mockito.when(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
    }

    @Test
    public void testTimedWalkNoAssistiveDeviceFormStep() {
        OrderedTask task = WalkingTaskFactory.timedWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50.0, 60, 5, false, new ArrayList<TaskExcludeOption>());

        List<String> stepIds = getTimedWalkStepIds();
        stepIds.remove(TimedWalkFormStepIdentifier);
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
    public void testTimedWalkNoInstructionSteps() {
        OrderedTask task = WalkingTaskFactory.timedWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50.0, 60, 5, true, Collections.singletonList(TaskExcludeOption.INSTRUCTIONS));

        List<String> stepIds = getTimedWalkStepIds();
        stepIds.remove(Instruction0StepIdentifier);
        stepIds.remove(Instruction1StepIdentifier);
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
    public void testTimedWalkNoLocationRecorder() {
        OrderedTask task = WalkingTaskFactory.timedWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50.0, 60, 5, true, Collections.singletonList(TaskExcludeOption.LOCATION));

        List<String> stepIds = getTimedWalkStepIds();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {

                if (step.getIdentifier().equals(TimedWalkTrial1StepIdentifier) ||
                    step.getIdentifier().equals(TimedWalkTrial1StepIdentifier))
                {
                    ActiveStep activeStep = (ActiveStep)step;
                    for (RecorderConfig config : activeStep.getRecorderConfigurationList()) {
                        assertFalse(config instanceof LocationRecorderConfig);
                    }
                }

                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    private List<String> getWalkBackAndForthStepIds() {
        return new LinkedList<>(Arrays.asList(
                Instruction0StepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                ShortWalkOutboundStepIdentifier,
                ShortWalkRestStepIdentifier,
                ConclusionStepIdentifier));
    }

    private List<String> getShortWalkStepIds() {
        return new LinkedList<>(Arrays.asList(
                Instruction0StepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                ShortWalkOutboundStepIdentifier,
                ShortWalkReturnStepIdentifier,
                ShortWalkRestStepIdentifier,
                ConclusionStepIdentifier));
    }

    private List<String> getTimedWalkStepIds() {
        return new LinkedList<>(Arrays.asList(
                LocationPermissionsStepIdentifier,
                GpsFeatureStepIdentifier,
                Instruction0StepIdentifier,
                TimedWalkFormStepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                TimedWalkTrial1StepIdentifier,
                TimedWalkTurnAroundStepIdentifier,
                TimedWalkTrial2StepIdentifier,
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
