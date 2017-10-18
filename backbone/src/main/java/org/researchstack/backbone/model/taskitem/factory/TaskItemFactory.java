package org.researchstack.backbone.model.taskitem.factory;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import org.researchstack.backbone.R;
import org.researchstack.backbone.model.survey.ActiveStepSurveyItem;
import org.researchstack.backbone.model.survey.InstructionSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.taskitem.ActiveTaskItem;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.recorder.AudioRecorderSettings;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.task.factory.AudioTaskFactory;
import org.researchstack.backbone.task.factory.HandTaskOptions;
import org.researchstack.backbone.task.factory.TappingTaskFactory;
import org.researchstack.backbone.task.factory.TremorTaskFactory;
import org.researchstack.backbone.task.factory.WalkingTaskFactory;
import org.researchstack.backbone.utils.LogExt;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 3/7/17.
 *
 * The TaskItemFactory controls converting TaskItem objects to Task objects
 * It accounts for all the variations specified in TaskItemType when looping through each
 * TaskItem and storing the result in a field you can access using the getTaskList method
 *
 * Note that TaskItem objects should be create from JSON using the GSON library,
 * and a TaskItemAdapter class to do special de-serialization for the TaskItems
 */

public class TaskItemFactory extends SurveyFactory {

    public static final int DEFAULT_DURATION                = 10; // in seconds
    public static final int DEFAULT_WALKING_DURATION        = 30; // in seconds
    public static final int DEFAULT_WALKING_REST_DURATION   = 30; // in seconds
    public static final int DEFAULT_STEPS_PER_LEG           = 100;

    private static final String DURATION_KEY                    = "duration";
    private static final String WALK_DURATION_KEY               = "walkDuration";
    private static final String REST_DURATION_KEY               = "restDuration";
    private static final String HAND_OPTIONS_KEY                = "handOptions";
    private static final String EXCLUDE_POSITIONS_KEY           = "excludePositions";
    private static final String SPEECH_INSTRUCTIONS_KEY         = "speechInstruction";
    private static final String SHORT_SPEECH_INSTRUCTIONS_KEY   = "shortSpeechInstruction";
    private static final String RECORDING_SETTINGS_KEY          = "recordingSettings";
    private static final String NUMBER_OF_STEPS_PER_LEG_KEY     = "numberOfStepsPerLeg";

    // When set, this will be used
    private CustomTaskCreator customTaskCreator;

    /*
     * Default constructor
     */
    public TaskItemFactory() {
        super();
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item TaskItem to transform into
     * @return a Task created from the TaskItem object
     */
    public Task createTask(Context context, TaskItem item) {

        Task task = null;

        switch (item.getTaskType()) {
            case VOICE:
                if (!(item instanceof ActiveTaskItem)) {
                    throw new IllegalStateException("Error in json parsing, VOICE type must be ActiveTaskItem");
                }
                task = createVoiceTask(context, (ActiveTaskItem)item);
                break;
            case TAPPING:
                if (!(item instanceof ActiveTaskItem)) {
                    throw new IllegalStateException("Error in json parsing, VOICE type must be ActiveTaskItem");
                }
                task = createTappingTask(context, (ActiveTaskItem)item);
                break;
            case TREMOR:
                if (!(item instanceof ActiveTaskItem)) {
                    throw new IllegalStateException("Error in json parsing, TREMOR type must be ActiveTaskItem");
                }
                task = createTremorTask(context, (ActiveTaskItem)item);
                break;
            case WALKING:
                if (!(item instanceof ActiveTaskItem)) {
                    throw new IllegalStateException("Error in json parsing, WALKING type must be ActiveTaskItem");
                }
                task = createWalkingTask(context, (ActiveTaskItem)item);
                break;
            case SHORT_WALK:
                if (!(item instanceof ActiveTaskItem)) {
                    throw new IllegalStateException("Error in json parsing, SHORT_WALK type must be ActiveTaskItem");
                }
                task = createShortWalkTask(context, (ActiveTaskItem)item);
                break;
            case MOOD_SURVEY:
                LogExt.e(getClass(), "Mood survey not implemented yet");
                task = null;
                break;
            case MEMORY:
                LogExt.e(getClass(), "Memory task not implemented yet");
                task = null;
                break;
            case CUSTOM:
                task = createCustomTask(context, item);
                break;
        }

        fillTaskWithDefaultTaskItemAdditions(context, task, item);

        return task;
    }

    public void fillTaskWithDefaultTaskItemAdditions(Context context, Task task, TaskItem item) {
        // Add submit bar negative action on first step to skip task if possible
        if (item.isTaskIsOptional()) {
            task = addSkipActionToTask(context, task);
        }

        if (item.getInsertSteps() != null && !item.getInsertSteps().isEmpty()) {
            if (task instanceof OrderedTask) {
                // Insert steps are only supported for OrderedTask
                OrderedTask orderedTask = (OrderedTask)task;
                for (SurveyItem surveyItem : item.getInsertSteps()) {
                    // TODO: do insert steps, that can also be tasks
                }
            } else {
                throw new IllegalStateException("insertSteps functionality only works with OrderedTasks");
            }
        }

        // Special cases for active tasks that are also OrderedTasks
        if (item instanceof ActiveTaskItem) {
            ActiveTaskItem activeTaskItem = (ActiveTaskItem)item;
            mapLocalizedSteps(activeTaskItem, task);
            removeSteps(activeTaskItem, task);
        }
    }

    protected Task addSkipActionToTask(Context context, Task task) {
        if (!(task instanceof OrderedTask)) {
            LogExt.e(getClass(), "Map Localized Steps only available for OrderedTasks");
            return task;
        }

        OrderedTask orderedTask = (OrderedTask)task;

        if (orderedTask.getSteps() == null || orderedTask.getSteps().isEmpty()) {
            LogExt.e(getClass(), "Empty task will not have skip action applied");
            return task;
        }

        Step introStep = orderedTask.getSteps().get(0);
        if (!(introStep instanceof InstructionStep)) {
            LogExt.e(getClass(), "Handling of an optional task is not implemented " +
                    "for tasks that do not start with IntructionStep");
            return task;
        }

        Step lastStep = orderedTask.getSteps().get(orderedTask.getSteps().size());
        if (orderedTask.getSteps().size() <= 1 || !(lastStep instanceof InstructionStep)) {
            LogExt.e(getClass(), "Handling of an optional task is not implemented " +
                    "for tasks that do not end with IntructionStep");
            return task;
        }

        InstructionStep introInstructionStep = (InstructionStep)introStep;

        // Replace fields in the intro step with a direct navigation step that has a skip button
        // to skip to the conclusion
        String skipExplanation = context.getString(R.string.rsb_skip_activity_instruction);
        String skipTitle = context.getString(R.string.rsb_skip_activity);

        if (introInstructionStep.getMoreDetailText() == null) {
            introInstructionStep.setMoreDetailText(skipExplanation);
        } else {
            introInstructionStep.setMoreDetailText(String.format("%s\n%s\n",
                    introInstructionStep.getMoreDetailText(), skipExplanation));
        }

        introInstructionStep.setSubmitBarNegativeActionSkipRule(
                task.getIdentifier(), skipTitle, lastStep.getIdentifier());

        return new NavigableOrderedTask(task.getIdentifier(), orderedTask.getSteps());
    }

    /**
     * This method maps some fields from a SurveyItem list stored in localizedSteps to edit
     * the corresponding Steps in the Task
     * @param activeTaskItem An ActiveTaskItem that contains localized steps
     * @param task the task to edit
     */
    protected void mapLocalizedSteps(ActiveTaskItem activeTaskItem, Task task) {

        if (!(task instanceof OrderedTask)) {
            LogExt.e(getClass(), "Map Localized Steps only available for OrderedTasks");
            return;
        }

        OrderedTask orderedTask = (OrderedTask)task;

        if (orderedTask.getSteps() == null || orderedTask.getSteps().isEmpty() ||
            activeTaskItem.getLocalizedSteps() == null || activeTaskItem.getLocalizedSteps().isEmpty())
        {
            return;
        }

        // Loop through all steps in a task, and see if any of them match the localized step
        for (Step step : orderedTask.getSteps()) {
            SurveyItem surveyItem = findSurveyItemIdentifierMatchToStep(step, activeTaskItem.getLocalizedSteps());
            if (surveyItem != null) {

                int indexOfStep = orderedTask.getSteps().indexOf(step);

                if (surveyItem.title != null) {
                    step.setTitle(surveyItem.title);
                }
                if (surveyItem.text != null) {
                    step.setText(surveyItem.text);
                }

                if (step instanceof InstructionStep && surveyItem instanceof InstructionSurveyItem) {
                    InstructionStep substep = (InstructionStep)step;
                    InstructionSurveyItem subsurveyItem = (InstructionSurveyItem)surveyItem;
                    if (subsurveyItem.detailText != null) {
                        substep.setMoreDetailText(subsurveyItem.detailText);
                    }
                }

                if (step instanceof ActiveStep && surveyItem instanceof ActiveStepSurveyItem) {
                    ActiveStep activeStep = (ActiveStep)step;
                    ActiveStepSurveyItem activeStepSurveyItem = (ActiveStepSurveyItem)surveyItem;
                    if (activeStepSurveyItem.getStepDuration() > 0) {
                        activeStep.setStepDuration(activeStepSurveyItem.getStepDuration());
                    }
                    if (activeStepSurveyItem.getStepSpokenInstruction() != null) {
                        activeStep.setSpokenInstruction(activeStepSurveyItem.getStepSpokenInstruction());
                    }
                    if (activeStepSurveyItem.getStepFinishedSpokenInstruction() != null) {
                        activeStep.setFinishedSpokenInstruction(activeStepSurveyItem.getStepFinishedSpokenInstruction());
                    }
                }

                // Must call this to make sure the changes stick
                orderedTask.replaceStep(indexOfStep, step);
            }
        }
    }

    /**
     * Removes the corresponding steps from the Task
     * @param activeTaskItem An ActiveTaskItem that contains removeSteps
     * @param task the task to edit
     */
    public void removeSteps(ActiveTaskItem activeTaskItem, Task task) {
        if (!(task instanceof OrderedTask)) {
            LogExt.e(getClass(), "Map Localized Steps only available for OrderedTasks");
            return;
        }

        OrderedTask orderedTask = (OrderedTask)task;

        if (orderedTask.getSteps() == null || orderedTask.getSteps().isEmpty() ||
            activeTaskItem.getRemoveSteps() == null || activeTaskItem.getRemoveSteps().isEmpty())
        {
            return;
        }

        // Loop through all steps in a task, and see if any of them match the localized step
        for (Step step : orderedTask.getSteps()) {
            if (activeTaskItem.getRemoveSteps().contains(step.getIdentifier())) {
                orderedTask.removeStep(orderedTask.getSteps().indexOf(step));
            }
        }
    }

    private SurveyItem findSurveyItemIdentifierMatchToStep(Step step, List<SurveyItem> surveyItemList) {
        for (SurveyItem surveyItem : surveyItemList) {
            if (step.getIdentifier().equals(surveyItem.getIdentifier())) {
                return surveyItem;
            }
        }
        return null;
    }

    public Task createTappingTask(Context context, ActiveTaskItem item) {

        int duration = extractInt(DURATION_KEY, DEFAULT_DURATION, item.getTaskOptions());

        String handOptionName = extractString(HAND_OPTIONS_KEY, HandTaskOptions.SERIALIZED_NAME_HAND_BOTH, item.getTaskOptions());
        HandTaskOptions.Hand handOption = HandTaskOptions.toHandOption(handOptionName);

        return TappingTaskFactory.twoFingerTappingIntervalTask(
                context,
                item.getSchemaIdentifier(),
                item.getIntendedUseDescription(),
                duration,
                handOption,
                item.createPredefinedExclusions());
    }

    public Task createVoiceTask(Context context, ActiveTaskItem item) {

        String speechInstruction = extractString(SPEECH_INSTRUCTIONS_KEY, null, item.getTaskOptions());
        String shortSpeechInstruction = extractString(SHORT_SPEECH_INSTRUCTIONS_KEY, null, item.getTaskOptions());
        int duration = extractInt(DURATION_KEY, DEFAULT_DURATION, item.getTaskOptions());

        // TODO: implement RECORDING_SETTINGS_KEY specific to Android, for now use the default
        if (item.getTaskOptions() != null && !item.getTaskOptions().isEmpty()) {
            // Attempt to read rest duration from JSON
            if (item.getTaskOptions().get(RECORDING_SETTINGS_KEY) != null) {
                // Currently, all these String constants are specifically mapped to iOS AVAudioSettings
                // so we would need to either change the key values completely, or make a map to Android
                LogExt.e(getClass(), "TODO: Voice task recorder settings not implemented yet on Android");
            }
        }

        return AudioTaskFactory.audioTask(
                context,
                item.getSchemaIdentifier(),
                item.getIntendedUseDescription(),
                speechInstruction,
                shortSpeechInstruction,
                duration,
                AudioRecorderSettings.defaultSettings(),
                true,
                item.createPredefinedExclusions());
    }

    public Task createWalkingTask(Context context, ActiveTaskItem item) {

        // The walking activity is assumed to be walking back and forth rather than trying to walk down a long hallway.
        int walkDuration = extractInt(WALK_DURATION_KEY, DEFAULT_WALKING_DURATION, item.getTaskOptions());
        int restDuration = extractInt(REST_DURATION_KEY, DEFAULT_WALKING_REST_DURATION, item.getTaskOptions());

        return WalkingTaskFactory.walkBackAndForthTask(
                context,
                item.getSchemaIdentifier(),
                item.getIntendedUseDescription(),
                walkDuration,
                restDuration,
                item.createPredefinedExclusions());  // TODO: may need to be the same as iOS
    }

    public Task createShortWalkTask(Context context, ActiveTaskItem item) {

        // The walking activity is assumed to be walking back and forth rather than trying to walk down a long hallway.
        int restDuration = extractInt(REST_DURATION_KEY, DEFAULT_WALKING_REST_DURATION, item.getTaskOptions());
        int numberOfSteps = extractInt(NUMBER_OF_STEPS_PER_LEG_KEY, DEFAULT_STEPS_PER_LEG, item.getTaskOptions());

        return WalkingTaskFactory.shortWalkTask(
                context,
                item.getSchemaIdentifier(),
                item.getIntendedUseDescription(),
                numberOfSteps,
                restDuration,
                item.createPredefinedExclusions());
    }

    public Task createTremorTask(Context context, ActiveTaskItem item) {
        int duration = extractInt(DURATION_KEY, DEFAULT_DURATION, item.getTaskOptions());

        String handOptionName = extractString(HAND_OPTIONS_KEY, HandTaskOptions.SERIALIZED_NAME_HAND_BOTH, item.getTaskOptions());
        HandTaskOptions.Hand handOption = HandTaskOptions.toHandOption(handOptionName);

        List<TremorTaskFactory.TremorTaskExcludeOption> excludeOptionList = new ArrayList<>();
        List<String> serializedExcludeList = extractStringList(EXCLUDE_POSITIONS_KEY, new ArrayList<>(), item.getTaskOptions());
        for (String serialized : serializedExcludeList) {
            excludeOptionList.add(TremorTaskFactory.toTremorExcludeOption(serialized));
        }

        return TremorTaskFactory.tremorTask(
                context,
                item.getSchemaIdentifier(),
                item.getIntendedUseDescription(),
                duration,
                excludeOptionList,
                handOption,
                item.createPredefinedExclusions());
    }

    private int extractInt(String key, int defaultValue, Map<String, Object> options) {
        if (options != null && !options.isEmpty()) {
            if (options.get(key) != null &&
                options.get(key) instanceof Number)
            {
                return ((Number)options.get(key)).intValue();
            }
        }
        return defaultValue;
    }

    private String extractString(String key, String defaultValue, Map<String, Object> options) {
        if (options != null && !options.isEmpty()) {
            Type listType = new TypeToken<List<SurveyItem>>() {}.getType();
            // Attempt to read key from JSON
            if (options.get(key) != null &&
                options.get(key) instanceof String)
            {
                return (String)options.get(key);
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")  // needed for unchecked String List generic type casting
    private List<String> extractStringList(String key, List<String> defaultValue, Map<String, Object> options) {
        if (options != null && !options.isEmpty()) {
            // Attempt to read key from JSON, GSON stores any Lists as a Map
            if (options.get(key) != null &&
                options.get(key) instanceof ArrayList)
            {
                ArrayList arrayList = (ArrayList) options.get(key);
                List<String> stringList = new ArrayList<>();
                for (Object listItem : arrayList) {
                    if (listItem instanceof String) {
                        stringList.add((String)listItem);
                    }
                }
                return stringList;
            }
        }
        return defaultValue;
    }

    /**
     * Override to create your own custom Tasks
     * @param context can be app or activity, used for resources
     * @param item the task item to convert into a task
     * @return a Task object made from the TaskItem
     */
    public Task createCustomTask(Context context, TaskItem item) {
        List<Step> steps = super.createSurveySteps(context, item.getTaskSteps());

        if (steps.size() == 1 && steps.get(0) instanceof SubtaskStep) {
            return ((SubtaskStep)steps.get(0)).getSubtask();
        } else {
            return new NavigableOrderedTask(item.getSchemaIdentifier(), steps);
        }
    }

    public CustomTaskCreator getCustomTaskCreator() {
        return customTaskCreator;
    }

    public void setCustomTaskCreator(CustomTaskCreator customTaskCreator) {
        this.customTaskCreator = customTaskCreator;
    }

    /**
     * This can be used by another class to implement custom conversion from a TaskItem to a Task
     */
    public interface CustomTaskCreator {
        Task createCustomTask(Context context, TaskItem item, TaskItemFactory factory);
    }
}
