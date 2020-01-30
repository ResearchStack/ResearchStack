package org.researchstack.foundation.components.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.researchstack.foundation.components.presentation.interfaces.ITaskNavigator
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.models.result.TaskResult
import java.util.*

/**
 * Factory for the TaskPresentationViewModelFactory.
 *
 * Providing ViewModelProvider.Factory allows us to inject dependencies and pass parameters
 * to an instance since the Android framework controls the instantiation of ViewModels.
 */
class TaskPresentationViewModelFactory<StepType : IStep>
(val taskNavigator: ITaskNavigator<StepType, TaskResult>,
 val taskProvider: ITaskProvider) {

    fun create(taskIdentifier: String, taskRunUUID: UUID): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TaskPresentationViewModel::class.java)) {

                    @Suppress("UNCHECKED_CAST")
                    return TaskPresentationViewModel(taskNavigator, taskProvider, taskIdentifier, taskRunUUID) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
