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
    private var previousNavDirection = NavDirection.SHIFT_LEFT

    init {
        goForward()
    }

    fun addStepResult(result: StepResult<*>) {
        taskResult.setStepResultForStepIdentifier(result.identifier, result)
        updateStep(currentStep, taskResult, previousNavDirection)
    }

    fun goForward() {
        currentStep = taskNavigator.getStepAfterStep(currentStep, taskResult)
        previousNavDirection = NavDirection.SHIFT_LEFT
        updateStep(currentStep, taskResult, previousNavDirection)
    }

    fun goBack() {
        currentStep = taskNavigator.getStepBeforeStep(currentStep, taskResult)
        previousNavDirection = NavDirection.SHIFT_RIGHT
        updateStep(currentStep, taskResult, previousNavDirection)
    }

    protected fun updateStep(step: StepType?, taskResult: TaskResult, @NavDirection navDirection: Int) {
        if (step == null) {
            taskNavigatorStateMutableLiveData.value =
                    TaskNavigatorState(navDirection, null, null, taskResult)
            return
        }
        taskNavigatorStateMutableLiveData.value =
                TaskNavigatorState(navDirection, step, taskNavigator.getProgressOfCurrentStep(step, taskResult), taskResult)
    }

    fun getTaskNavigatorStateLiveData(): LiveData<TaskNavigatorState<StepType>> {
        return taskNavigatorStateMutableLiveData
    }

    data class TaskNavigatorState<StepType>(@NavDirection val navDirection: Int, val currentStep: StepType?, val taskProgress: Task.TaskProgress?, val taskResult: TaskResult)
}
