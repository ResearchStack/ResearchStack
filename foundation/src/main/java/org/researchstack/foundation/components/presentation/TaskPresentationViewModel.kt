package org.researchstack.foundation.components.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import org.researchstack.foundation.components.presentation.interfaces.ITaskNavigator
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.interfaces.ITask
import org.researchstack.foundation.core.models.result.StepResult
import org.researchstack.foundation.core.models.result.TaskResult
import org.researchstack.foundation.core.models.task.Task
import java.util.*

/**
 * ViewModel for presenting an ITask.
 *
 * Retrieves a Task for a taskIdentifier and maintains navigation state.
 *
 * TODO: Retrieve task AND THEN construct a TaskNavigator (which should take the Task as parameter)
 * TODO: Retrieve a TaskResult for a taskRunUUID from a previous task run, if applicable
 * TODO: Support AsyncResults (e.g. recorders), will require a service to subscribe to AsyncResults,
 *      manage TaskResult, and notify when the final result is ready (Task UI is complete, and all
 *      AsyncResults are finished, errored, canceled, or timed-out)
 */
class TaskPresentationViewModel<StepType : IStep>(val taskNavigator: ITaskNavigator<StepType, TaskResult>,
                                                  taskProvider: ITaskProvider,
                                                  val taskIdentifier: String,
                                                  val taskRunUUID: UUID) : ViewModel() {

    private val task: ITask? = taskProvider.task(taskIdentifier)
    private val taskNavigatorStateMutableLiveData: MutableLiveData<TaskNavigatorState<StepType>> = MutableLiveData()


    private var currentStep: StepType? = null
    private val taskResult: TaskResult = TaskResult(taskIdentifier, taskRunUUID)
    private var mostRecentNavDirection = NavDirection.SHIFT_LEFT

    init {
        // TODO: consider making this an action that the Fragment must invoke to "start" the ViewModel
        goForward()
    }

    /**
     * Add a StepResult to the TaskResult.
     */
    @VisibleForTesting
    fun addStepResult(result: StepResult<*>) {
        taskResult.setStepResultForStepIdentifier(result.identifier, result)
        updateStep(currentStep, taskResult, mostRecentNavDirection)
    }

    /**
     * Request forward navigation.
     */
    fun goForward() {
        updateStep(taskNavigator.getStepAfterStep(currentStep, taskResult), taskResult, mostRecentNavDirection)
    }

    /**
     * Request backward navigation.
     */
    fun goBack() {
        updateStep(taskNavigator.getStepBeforeStep(currentStep, taskResult), taskResult, mostRecentNavDirection)
    }

    /**
     *
     */
    fun getTaskNavigatorStateLiveData(): LiveData<TaskNavigatorState<StepType>> {
        return taskNavigatorStateMutableLiveData
    }

    /**
     * Update the TaskNavigatorState in response to a stap navigation event or a change to the TaskResult.
     */
    @VisibleForTesting
    internal fun updateStep(step: StepType?, taskResult: TaskResult, @NavDirection navDirection: Int) {
        currentStep = step
        mostRecentNavDirection = navDirection

        if (step == null) {
            taskNavigatorStateMutableLiveData.value =
                    TaskNavigatorState(navDirection, null, null, null, null, taskResult)
            return
        }
        taskNavigatorStateMutableLiveData.value =
                TaskNavigatorState(navDirection, step,
                        taskNavigator.getStepBeforeStep(step, taskResult),
                        taskNavigator.getStepAfterStep(step, taskResult),
                        taskNavigator.getProgressOfCurrentStep(step, taskResult), taskResult)
    }

    /**
     * Data class the represents the full navigation state.
     */
    data class TaskNavigatorState<StepType: IStep>(
            @NavDirection val navDirection: Int, val currentStep: StepType?, val previousStep: StepType?,
            val nextStep: StepType?, val taskProgress: Task.TaskProgress?, val taskResult: TaskResult)
}
