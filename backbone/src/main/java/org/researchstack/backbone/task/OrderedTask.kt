package org.researchstack.backbone.task

import android.annotation.SuppressLint
import android.content.Context
import org.researchstack.backbone.R
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.utils.TextUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


/**
 * The OrderedTask class implements all the methods in the [Task] protocol and represents a
 * task that assumes a fixed order for its steps.
 *
 *
 * In the ResearchStack framework, any simple sequential task, such as a survey or an active task,
 * can be represented as an ordered task.
 *
 *
 * If you want further custom conditional behaviors in a task, it can be easier to subclass
 * OrderedTask or NavigableOrderedTask and override particular [Task] methods than it
 * is to implement a new Task subclass directly. Override the methods [.getStepAfterStep] and
 * [.getStepBeforeStep], and call super for all other methods.
 */
@SuppressLint("ParcelCreator")
open class OrderedTask : Task, Serializable {
    var steps: MutableList<Step> = ArrayList()

    /* Default constructor needed for serilization/deserialization of object */
    constructor() : super() {}

    /**
     * Returns an initialized ordered task using the specified identifier and array of steps.
     *
     * @param identifier The unique identifier for the task
     * @param steps      The [Step] objects in the order in which they should be presented.
     */
    constructor(identifier: String, vararg steps: Step) : this(identifier, Arrays.asList<Step>(*steps)) {}

    /**
     * Returns an initialized ordered task using the specified identifier and array of steps.
     *
     * @param identifier The unique identifier for the task.
     * @param steps      An array of [Step] objects in the order in which they should be
     * presented.
     */
    constructor(identifier: String, steps: List<Step>) : super(identifier, "") {
        this.steps = ArrayList(steps)
    }

    /**
     * Returns the next step immediately after the passed in step in the list of steps, or null
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return the next step in `steps` after the passed in step, or null if at the end
     */
    override fun getStepAfterStep(step: Step?, result: TaskResult?): Step? {
        if (step == null) {
            return steps[0]
        }

        val nextIndex = steps.indexOf(step) + 1

        return if (nextIndex < steps.size) {
            steps[nextIndex]
        } else null

    }

    /**
     * Returns the next step immediately before the passed in step in the list of steps, or null
     *
     * @param step   The reference step.
     * @param result A snapshot of the current set of results.
     * @return the next step in `steps` before the passed in step, or null if at the
     * start
     */
    override fun getStepBeforeStep(step: Step?, result: TaskResult?): Step? {
        val nextIndex = steps.indexOf(step) - 1

        return if (nextIndex >= 0) {
            steps[nextIndex]
        } else null

    }

    override fun getStepWithIdentifier(identifier: String?): Step? {
        for (step in steps) {
            if (identifier == step.identifier) {
                return step
            }
        }
        return null
    }

    override fun getProgressOfCurrentStep(step: Step?, result: TaskResult?): Task.TaskProgress? {
        val current = if (step == null) -1 else steps.indexOf(step)
        return Task.TaskProgress(current, steps.size)
    }

    /**
     * Returns [Step.getStepTitle] if it exists, otherwise returns string showing progress.
     *
     * @param context for fetching resources
     * @param step    the current step
     * @return the step title, or a progress string if it doesn't have one
     */
    override fun getTitleForStep(context: Context, step: Step): String {
        var title = super.getTitleForStep(context, step)
        if (TextUtils.isEmpty(title)) {
            val currentIndex = steps.indexOf(step)
            title = context.getString(
                R.string.rsb_format_step_title,
                currentIndex + 1,
                steps.size
            )
        }
        return title
    }

    /**
     * Validates that there are no duplicate identifiers in the list of steps
     *
     * @throws org.researchstack.backbone.task.Task.InvalidTaskException if the task is invalid
     */
    override fun validateParameters() {
        val uniqueIds = HashSet<String>()
        for (step in steps) {
            uniqueIds.add(step.identifier)
        }

        if (uniqueIds.size != steps.size) {
            throw Task.InvalidTaskException("OrderedTask has steps with duplicate ids")
        }
    }

    /**
     * Convenience method to replace a Step at a specific index
     * This can be used to change the contents of a Step by calling getSteps(),
     * changing the step, and then calling this method to make sure the changes stick
     *
     * @param index index of step to replace
     * @param step to replace at index
     */
    fun replaceStep(index: Int, step: Step) {
        steps[index] = step
    }

    /**
     * Convenience method to remove a Step from the Task
     * @param index index of step to remove
     */
    fun removeStep(index: Int) {
        steps.removeAt(index)
    }

    /**
     * * Convenience method to add a Step to the Task
     * @param index to add the step at
     * @param step the step to add
     */
    fun addStep(index: Int, step: Step) {
        steps.add(index, step)
    }
}
