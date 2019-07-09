package org.researchstack.foundation.components.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import org.researchstack.foundation.components.presentation.interfaces.ITaskNavigator
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.models.task.Task

class TaskPresentationViewModel<StepType : IStep, ResultType : IResult>(val taskNavigator: ITaskNavigator<StepType, ResultType>) : ViewModel() {

    private val taskNavigatorStateMutableLiveData: MutableLiveData<TaskNavigatorState<StepType>> = MutableLiveData()

    fun addStepResult(result: IResult?) {

    }

    fun goForward() {

    }

    fun goBack() {

    }

    fun getTaskNavigatorStateLiveData(): LiveData<TaskNavigatorState<StepType>> {
        return taskNavigatorStateMutableLiveData
    }

    data class TaskNavigatorState<StepType>(@NavDirection val navDirection: Int, val currentStep: StepType?, val taskProgress: Task.TaskProgress)
}
