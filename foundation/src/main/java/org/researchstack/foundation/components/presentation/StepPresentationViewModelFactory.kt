package org.researchstack.foundation.components.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.researchstack.foundation.core.interfaces.IStep

/**
 * Factory for the StepPresentationViewModel.
 *
 * Providing ViewModelProvider.Factory allows us to inject dependencies and pass parameters
 * to an instance since the Android framework controls the instantiation of ViewModels.
 */
open class StepPresentationViewModelFactory<StepType : IStep>
(val taskPresentationViewModel: TaskPresentationViewModel<StepType>) : ViewModel() {
    open fun create(step: StepType): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(StepPresentationViewModel::class.java)) {

                    @Suppress("UNCHECKED_CAST")
                    return StepPresentationViewModel(taskPresentationViewModel, step) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
