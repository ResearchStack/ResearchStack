package org.researchstack.foundation.components.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.researchstack.foundation.core.interfaces.IStep

class StepPresentationViewModelFactory<StepType : IStep>
(val taskPresentationViewModel: TaskPresentationViewModel<StepType>) : ViewModel() {
    fun create(): ViewModelProvider.Factory {
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
