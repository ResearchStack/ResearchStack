package org.researchstack.backbone.task;

import android.content.Context;
import android.support.annotation.NonNull;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.callbacks.StepViewCallback;

import java.io.Serializable;

/**
 * A task to be carried out by a participant in a research study.
 * <p>
 * To present the ResearchStack framework UI in your app, instantiate an object that extends the
 * Task class (such as {@link OrderedTask}) and provide it to a {@link
 * org.researchstack.backbone.ui.ViewTaskActivity}.
 * <p>
 * Implement this protocol to enable dynamic selection of the steps for a given task. By default,
 * OrderedTask implements this protocol for simple sequential tasks.
 * <p>
 * Each {@link Step} in a task roughly corresponds to one screen, and represents the primary unit of
 * work in any task. For example, a {@link org.researchstack.backbone.step.QuestionStep} object
 * corresponds to a single question presented on screen, together with controls the participant uses
 * to answer the question. Another example is {@link org.researchstack.backbone.step.FormStep},
 * which corresponds to a single screen that displays multiple questions or items for which
 * participants provide information, such as first name, last name, and birth date.
 */
public abstract class Task implements Serializable
{
    private String identifier;

    private transient ViewTaskActivity activity;

    /**
     * Class constructor specifying a unique identifier.
     *
     * @param identifier the task identifier, see {@link #getIdentifier()}
     */
    public Task(String identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Sets the current @{@link ViewTaskActivity} for this task.
     * This function should be called only by the {@link ViewTaskActivity} instance running
     * at its creation time.
     * @param activity the @{@link ViewTaskActivity} instance
     */
    public void setActivity(ViewTaskActivity activity){
        this.activity = activity;
        activity.setStepViewCallback(new StepViewCallback() {
            @Override
            public void onStepShown(ViewTaskActivity activity, Step step) {
                onStepShown(activity, step);
            }
        });
    }

    /**
     * Retrieves the @{@link ViewTaskActivity} that is actually running this task.
     * Tasks can use this to have access to the current activity and its context.
     */
    public ViewTaskActivity getActivity(){
        return this.activity;
    }

    /**
     * Forces the task to proceed to a given @{@link Step}.
     * Results, if any, should be computed before.
     * @param step the @{@link Step}
     */
    public void forceStep(@NonNull Step step){
        activity.showStep(step);
    }

    /**
     * Forces the task to be finished.
     */
    public void forceFinish(){
        this.activity.saveAndFinish();
    }

    /**
     * Function that is called whenever a step is visualised in the activity.
     */
    protected void onStepChanged(ViewTaskActivity activity, Step step){

    }

    /**
     * Gets the unique identifier for this task.
     * <p>
     * The identifier should be a short string that identifies the task. The identifier is copied
     * into the {@link TaskResult} objects generated  for this task. You can use a human-readable
     * string for the task identifier or a UUID; the exact string you use depends on your app.
     * <p>
     * In the case of apps whose tasks come from a server, the unique identifier for the task may be
     * in an external database.
     * <p>
     * The task identifier is used when constructing the task result. The identifier can also be
     * used during UI state restoration to identify the task that needs to be restored.
     *
     * @return the task identifier
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Gets the title to display in the toolbar for a given step.
     * <p>
     * Override this method to return a custom title, such a text representation of the current
     * progress instead of the step's title.
     * <p>
     * The default implementation gets the title from string resources if it exists, otherwise it
     * returns an empty string.
     *
     * @param context for fetching resources
     * @param step    the current step
     * @return the title to display
     */
    public String getTitleForStep(Context context, Step step)
    {
        return step.getStepTitle() != 0 ? context.getString(step.getStepTitle()) : "";
    }

    /**
     * Returns the step after the specified step, if there is one.
     * <p>
     * This method lets you use a result to determine the next step.
     * <p>
     * The {@link org.researchstack.backbone.ui.ViewTaskActivity}  calls this method to determine
     * the step to display after the specified step. The ViewTaskActivity can also call this
     * method every time the result updates, to determine if the new result changes which steps are
     * available.
     * <p>
     * If you need to implement this method, take care to avoid creating a confusing sequence of
     * steps. As much as possible, use {@link OrderedTask} instead.
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return The step that comes after the specified step, or null if there isn't one.
     */
    public abstract Step getStepAfterStep(Step step, TaskResult result);

    /**
     * Returns the step that precedes the specified step, if there is one.
     * <p>
     * The {@link org.researchstack.backbone.ui.ViewTaskActivity} calls this method to determine the
     * step to display before the specified step. The ViewTaskActivity
     * can also call this method every time the result changes, to determine if the new result
     * changes which steps are available.
     * <p>
     * If you need to implement this method, take care to avoid creating a confusing sequence of
     * steps. As much as possible, use {@link OrderedTask} instead. Returning null prevents the user
     * from navigating back to a previous step.
     *
     * @param step   The reference step. Pass null to specify the last step.
     * @param result A snapshot of the current set of results.
     * @return The step that precedes the reference step, or null if there isn't one.
     */
    public abstract Step getStepBeforeStep(Step step, TaskResult result);

    /**
     * Returns the step that matches the specified identifier, if there is one.
     *
     * @param identifier The identifier of the step to retrieve.
     * @return The step that matches the specified identifier.
     */
    public abstract Step getStepWithIdentifier(String identifier);

    /**
     * Returns the progress of the current step.
     * <p>
     * During a task, the {@link org.researchstack.backbone.ui.ViewTaskActivity}  can display the
     * progress (that is, the current step number out of the total number of steps) in the
     * navigation bar. Implement this method to control what is displayed; if you don't implement
     * this method, the progress label does not appear.
     * <p>
     * If the returned {@link TaskProgress} object has a count of 0, the progress is not displayed.
     *
     * @param step   The current step.
     * @param result A snapshot of the current set of results.
     * @return The current step's index and the total number of steps in the task, as an
     * TaskProgress object.
     */
    public abstract TaskProgress getProgressOfCurrentStep(Step step, TaskResult result);

    /**
     * Validates the task parameters.
     * <p>
     * The implementation of this method should check that all the task parameters are correct. An
     * invalid task is considered an unrecoverable error: the implementation should throw an
     * exception on parameter validation failure. For example, the {@link OrderedTask}
     * implementation makes sure that all its step identifiers are unique, throwing an exception
     * otherwise.
     * <p>
     * This method is usually called by {@link org.researchstack.backbone.ui.ViewTaskActivity} when
     * its task is set.
     *
     * @throws InvalidTaskException
     */
    public abstract void validateParameters();

    /**
     * A structure that represents how far a task has progressed.
     * <p>
     * Objects that extend Task return the task progress structure to indicate to the {@link
     * org.researchstack.backbone.ui.ViewTaskActivity} how far the task has progressed.
     * <p>
     * Note that the values in an {@link TaskProgress} structure are used only for display; you
     * don't use the values to access the steps in a task.
     */
    public static class TaskProgress
    {

        private final int current;

        private final int total;

        /**
         * Constructor specifying current index and total number of steps in the task.
         *
         * @param current current step number, zero-based
         * @param total   number of steps in the task
         */
        public TaskProgress(int current, int total)
        {
            this.current = current;
            this.total = total;
        }

        /**
         * Gets the current step number in the task, zero-based.
         *
         * @return current step number
         */
        public int getCurrent()
        {
            return current;
        }

        /**
         * Gets the current total number of steps in the task.
         *
         * @return the total number of steps
         */
        public int getTotal()
        {
            return total;
        }
    }

    /**
     * Runtime exception that is thrown by {@link Task} subclasses in {@link #validateParameters()}.
     */
    public static class InvalidTaskException extends RuntimeException
    {
        public InvalidTaskException()
        {
            super();
        }

        public InvalidTaskException(String detailMessage)
        {
            super(detailMessage);
        }

        public InvalidTaskException(String detailMessage, Throwable throwable)
        {
            super(detailMessage, throwable);
        }

        public InvalidTaskException(Throwable throwable)
        {
            super(throwable);
        }
    }

}
