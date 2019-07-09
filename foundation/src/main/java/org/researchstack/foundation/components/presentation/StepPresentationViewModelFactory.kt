package org.researchstack.foundation.components.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import java.util.*

class StepPresentationViewModelFactory<StepType : IStep, ResultType : IResult>
(val taskPresentationViewModel: TaskPresentationViewModel<StepType, ResultType>) : ViewModel() {
    fun create(taskIdentifier: String, taskRunUUID: UUID): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(StepPresentationViewModel::class.java)) {

                    @Suppress("UNCHECKED_CAST")
                    return StepPresentationViewModel(taskPresentationViewModel) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
