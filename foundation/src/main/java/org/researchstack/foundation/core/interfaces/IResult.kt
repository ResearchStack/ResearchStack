package org.researchstack.foundation.core.interfaces

import org.threeten.bp.Instant

/**
 * The Result class defines the attributes of a result from one step or a group of steps. When you
 * use the ResearchStack framework APIs, you typically get a result from the Result property of
 * either {@link org.researchstack.foundation.ui.ViewTaskActivity} or {@link
 * org.researchstack.foundation.ui.step.layout.StepLayout}. Certain types of results can contain other
 * results, which together express a hierarchy; examples of these types of results are {@link
 * StepResult} and {@link TaskResult}.
 * <p>
 * Every object in the result hierarchy has an identifier that should correspond to the identifier
 * of an object in the original step hierarchy. Similarly, every object has a start date and an end
 * date that correspond to the range of times during which the result was collected. In an {@link
 * StepResult} object, for example, the start and end dates cover the range of time during which the
 * step view controller was visible on screen.
 * <p>
 * When you implement a new type of step, it is usually helpful to create a new Result subclass to
 * hold the type of result data the step can generate, unless it makes sense to use an existing
 * subclass.
 */
interface IResult {
    /**
     * Returns a meaningful identifier for the result.
     * <p>
     * The identifier can be used to identify the question that was asked or the task that was
     * completed to produce the result. Typically, the identifier is copied from the originating
     * object.
     * <p>
     * For example, a task result receives its identifier from a task, a step result receives its
     * identifier from a step, and a question result receives its identifier from a step or a form
     * step.
     *
     * @return the unique identifier
     */
    val identifier: String

    val type: String

    /**
     * Returns the time when the task, step, or data collection began.
     *
     * @return the start date of this result
     */
    val startTimestamp: Instant

    /**
     * Returns the time when the task, step, or data collection stopped.
     *
     * @return the end date of this result
     */
    val endTimestamp: Instant
}