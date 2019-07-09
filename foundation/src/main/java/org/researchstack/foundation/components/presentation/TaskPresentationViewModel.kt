package org.researchstack.foundation.components.presentation

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
        goForward()
    }

    fun addStepResult(result: StepResult<*>) {
        taskResult.setStepResultForStepIdentifier(result.identifier, result)
        updateStep(currentStep, taskResult, mostRecentNavDirection)
    }

    fun goForward() {
        updateStep(taskNavigator.getStepAfterStep(currentStep, taskResult), taskResult, mostRecentNavDirection)
    }

    fun goBack() {
        updateStep(taskNavigator.getStepBeforeStep(currentStep, taskResult), taskResult, mostRecentNavDirection)
    }

    fun updateStep(step: StepType?, taskResult: TaskResult, @NavDirection navDirection: Int) {
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

    fun getTaskNavigatorStateLiveData(): LiveData<TaskNavigatorState<StepType>> {
        return taskNavigatorStateMutableLiveData
    }

    data class TaskNavigatorState<StepType>(@NavDirection val navDirection: Int, val currentStep: StepType?, val previousStep: StepType?, val nextStep: StepType?, val taskProgress: Task.TaskProgress?, val taskResult: TaskResult)
}
