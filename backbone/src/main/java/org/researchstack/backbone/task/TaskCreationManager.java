package org.researchstack.backbone.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.taskitem.TaskItemAdapter;
import org.researchstack.backbone.model.taskitem.factory.TaskItemFactory;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LogExt;

/**
 * Created by TheMDP on 3/24/17.
 *
 * The TaskCreationManager follows a similar architecture as the OnboardingManager,
 * Its job is to parse JSON resources and create Tasks from them
 * It also is designed in a way that makes creating custom tasks and steps easy
 * while still getting all the awesome features of the base Survey and TaskItem factories
 */

public class TaskCreationManager implements TaskItemFactory.CustomTaskCreator, SurveyFactory.CustomStepCreator {

    private TaskItemFactory taskItemFactory;

    public TaskCreationManager() {
        super();
        taskItemFactory = new TaskItemFactory();
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param resourceName needs to be a resource  that you define in the ResourceManager
     *                     it is as simple as defining a method returning a resource with this name
     *                     in your concreate implementation of ResourceManager
     * @return             a Task based on the contents of the resource
     */
    public Task createTask(Context context, String resourceName) {
        String taskItemJson = ResourceManager.getResourceAsString(context,
                ResourceManager.getInstance().generatePath(ResourcePathManager.Resource.TYPE_JSON, resourceName));

        if (taskItemJson == null) {
            LogExt.e(getClass(), "Error finding resource with resource name " + resourceName +
            ". Did you define a method that returns it in your concrete implementation " +
            "of ResourceManager?");
            return null;
        }

        Gson gson = buildGson(context);
        TaskItem taskItem = gson.fromJson(taskItemJson, TaskItem.class);
        if (taskItem == null) {
            LogExt.e(getClass(), "Error creating TaskItem from json");
            return null;
        }

        return getTaskItemFactory(taskItem).createTask(context, taskItem);
    }

    /**
     * This is a way for subclasses to inject their own task item factories per specific task items
     * @param item the task item being operated on
     * @return the task item factory that will be used to create the task from this task item
     */
    public TaskItemFactory getTaskItemFactory(TaskItem item) {
        return taskItemFactory;
    }

    /**
     * Override to register custom SurveyItemAdapters,
     * but make sure that the adapter extends from SurveyItemAdapter, and only overrides
     * the method getCustomClass()
     * @param builder the gson build to add the survey item adapter to
     */
    public void registerSurveyItemAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
    }

    /**
     * Override to register custom TaskItemAdapters,
     * but make sure that the adapter extends from TaskItemAdapter, and only overrides
     * the method getCustomClass()
     * @param builder the gson build to add the task item adapter to
     */
    public void registerTaskItemAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(SurveyItem.class, new TaskItemAdapter());
    }

    /**
     * @return a Gson to be used by the TaskCreationManager
     */
    private Gson buildGson(Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        registerSurveyItemAdapter(gsonBuilder);
        registerTaskItemAdapter(gsonBuilder);
        return gsonBuilder.create();
    }

    /**
     * Override this to implement your own functionality for CustomStep creation
     * You should also override registerSurveyItemAdapter to control the CustomSurveyItem data model
     * @param item the custom survey item to create a custom step from
     * @param factory the factory that created the custom survey item
     * @param isSubtaskStep true if this is within a subtask step already, false otherwise
     * @return a CustomStep that can be used in your app
     */
    @Override
    public Step createCustomStep(Context context, SurveyItem item, boolean isSubtaskStep, SurveyFactory factory) {
        return factory.createCustomStep(context, item, isSubtaskStep);
    }

    /**
     * Override this to implement your own functionality for Custom Task creation
     * You should also override registerTaskItemAdapter to control the CustomTaskItem data model
     * @param item the custom task item to create a custom Task from
     * @param factory the factory that created the custom task item
     * @return a Task that can be used in your app
     */
    @Override
    public Task createCustomTask(Context context, TaskItem item, TaskItemFactory factory) {
        return factory.createCustomTask(context, item);
    }

    /**
     * @return a basic task item factory that can be used to create tasks
     */
    public TaskItemFactory getDefaultTaskItemFactory() {
        return taskItemFactory;
    }
}
