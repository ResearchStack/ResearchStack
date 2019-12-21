package org.researchstack.backbone.task

import android.content.Context
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import java.io.Serializable

/**
 * A task to be carried out by a participant in a research study.
 *
 *
 * To present the ResearchStack framework UI in your app, instantiate an object that extends the
 * Task class (such as [OrderedTask]) and provide it to a .
 *
 *
 * Implement this protocol to enable dynamic selection of the steps for a given task. By default,
 * OrderedTask implements this protocol for simple sequential tasks.
 *
 *
 * Each [Step] in a task roughly corresponds to one screen, and represents the primary unit of
 * work in any task. For example, a [org.researchstack.backbone.step.QuestionStep] object
 * corresponds to a single question presented on screen, together with controls the participant uses
 * to answer the question. Another example is [org.researchstack.backbone.step.FormStep],
 * which corresponds to a single screen that displays multiple questions or items for which
 * participants provide information, such as first name, last name, and birth date.
 */
abstract class Task : Serializable {
    /**
     * Gets the unique identifier for this task.
     *
     *
     * The identifier should be a short string that identifies the task. The identifier is copied
     * into the [TaskResult] objects generated  for this task. You can use a human-readable
     * string for the task identifier or a UUID; the exact string you use depends on your app.
     *
     *
     * In the case of apps whose tasks come from a server, the unique identifier for the task may be
     * in an external database.
     *
     *
     * The task identifier is used when constructing the task result. The identifier can also be
     * used during UI state restoration to identify the task that needs to be restored.
     *
     * @return the task identifier
     */
    var identifier: String = ""

    var title: String = ""

    var allowsSaving = false

    /* Default constructor needed for serilization/deserialization of object */
    constructor() : super()

    /**
     * Class constructor specifying a unique identifier.
     *
     * @param identifier the task identifier, see [.getIdentifier]
     */
    constructor(identifier: String, title: String ) {
        this.identifier = identifier
        this.title = title
    }


    constructor(identifier: String, title: String , allowsSaving:Boolean) {
        this.identifier = identifier
        this.title = title
        this.allowsSaving = allowsSaving
    }

    /**
     * Gets the title to display in the toolbar for a given step.
     *
     *
     * Override this method to return a custom title, such a text representation of the current
     * progress instead of the step's title.
     *
     *
     * The default implementation gets the title from string resources if it exists, otherwise it
     * returns an empty string.
     *
     * @param context for fetching resources
     * @param step    the current step
     * @return the title to display
     */
    open fun getTitleForStep(context: Context, step: Step): String {
        return if (step.stepTitle != 0) context.getString(step.stepTitle) else ""
    }

    /**
     * Returns the step after the specified step, if there is one.
     *
     *
     * This method lets you use a result to determine the next step.
     *
     *
     * The   calls this method to determine
     * the step to display after the specified step. The ViewTaskActivity can also call this
     * method every time the result updates, to determine if the new result changes which steps are
     * available.
     *
     *
     * If you need to implement this method, take care to avoid creating a confusing sequence of
     * steps. As much as possible, use [OrderedTask] instead.
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return The step that comes after the specified step, or null if there isn't one.
     */
    abstract fun getStepAfterStep(step: Step?, result: TaskResult?): Step?

    /**
     * Returns the step that precedes the specified step, if there is one.
     *
     *
     * The  calls this method to determine the
     * step to display before the specified step. The ViewTaskActivity
     * can also call this method every time the result changes, to determine if the new result
     * changes which steps are available.
     *
     *
     * If you need to implement this method, take care to avoid creating a confusing sequence of
     * steps. As much as possible, use [OrderedTask] instead. Returning null prevents the user
     * from navigating back to a previous step.
     *
     * @param step   The reference step. Pass null to specify the last step.
     * @param result A snapshot of the current set of results.
     * @return The step that precedes the reference step, or null if there isn't one.
     */
    abstract fun getStepBeforeStep(step: Step?, result: TaskResult?): Step?

    /**
     * Returns the step that matches the specified identifier, if there is one.
     *
     * @param identifier The identifier of the step to retrieve.
     * @return The step that matches the specified identifier.
     */
    abstract fun getStepWithIdentifier(identifier: String?): Step?

    /**
     * Returns the progress of the current step.
     *
     *
     * During a task, the   can display the
     * progress (that is, the current step number out of the total number of steps) in the
     * navigation bar. Implement this method to control what is displayed; if you don't implement
     * this method, the progress label does not appear.
     *
     *
     * If the returned [TaskProgress] object has a count of 0, the progress is not displayed.
     *
     * @param step   The current step.
     * @param result A snapshot of the current set of results.
     * @return The current step's index and the total number of steps in the task, as an
     * TaskProgress object.
     */
    abstract fun getProgressOfCurrentStep(step: Step?, result: TaskResult?): TaskProgress?

    /**
     * Validates the task parameters.
     *
     *
     * The implementation of this method should check that all the task parameters are correct. An
     * invalid task is considered an unrecoverable error: the implementation should throw an
     * exception on parameter validation failure. For example, the [OrderedTask]
     * implementation makes sure that all its step identifiers are unique, throwing an exception
     * otherwise.
     *
     *
     * This method is usually called by  when
     * its task is set.
     *
     * @throws InvalidTaskException if the task is invalid
     */
    abstract fun validateParameters()

    open fun processTaskResult(step: Step?, result: TaskResult?) {}

    enum class ViewChangeType {
        ActivityCreate,
        ActivityPause,
        ActivityResume,
        ActivityStop,
        StepChanged
    }

    /**
     * A structure that represents how far a task has progressed.
     *
     *
     * Objects that extend Task return the task progress structure to indicate to the how far the task has progressed.
     *
     *
     * Note that the values in an [TaskProgress] structure are used only for display; you
     * don't use the values to access the steps in a task.
     */
    class TaskProgress(val current: Int, val total: Int)

    /**
     * Runtime exception that is thrown by [Task] subclasses in [.validateParameters].
     */
    class InvalidTaskException : RuntimeException {
        constructor() : super() {}

        constructor(detailMessage: String) : super(detailMessage) {}

        constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}

        constructor(throwable: Throwable) : super(throwable) {}
    }


}
