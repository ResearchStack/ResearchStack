package org.researchstack.foundation.components.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.researchstack.foundation.components.presentation.interfaces.ITaskNavigator
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import java.util.*

class TaskPresentationViewModelFactory<StepType : IStep, ResultType : IResult>
(val taskNavigator: ITaskNavigator<StepType, ResultType>) {

    fun create(taskIdentifier: String, taskRunUUID: UUID): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TaskPresentationViewModel::class.java)) {

                    @Suppress("UNCHECKED_CAST")
                    return TaskPresentationViewModel<StepType, ResultType>(taskNavigator) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
