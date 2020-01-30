package org.researchstack.foundation.components.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.models.result.StepResult
import org.threeten.bp.Instant
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ViewModel for presenting an IStep.
 *
 * TODO: this model should manage Step states such as IStep's fields and any temporary states (those
 * that have yet to be saved as a StepResult).
 */
open class StepPresentationViewModel<StepType : IStep>
(val taskPresentationViewModel: TaskPresentationViewModel<StepType>, val step: StepType) : ViewModel() {
    private val addedResult: AtomicBoolean = AtomicBoolean()
    private val startTime: Instant = Instant.now()

    fun stepResult() : LiveData<Any> {
        return Transformations.map(taskPresentationViewModel.getTaskNavigatorStateLiveData()) {
            state -> state.taskResult.getStepResult(step.identifier)
        }
    }

    fun handleAction(actionType: String) {
        when (actionType) {
            ActionType.FORWARD -> {
                if (!addedResult.get()) {
                    // If for whatever reason the step didn't create a result matching it's identifier we create a
                    // ResultBase to mark that the step completed.
                    val currentStep =
                            taskPresentationViewModel.getTaskNavigatorStateLiveData().value!!.currentStep!!
                    addStepResult(StepResult<Any>(identifier = currentStep.identifier, startTimestamp = startTime, endTimestamp = Instant.now()))
                }
                taskPresentationViewModel.goForward()
            }
            ActionType.BACKWARD -> taskPresentationViewModel.goBack()
            else -> throw UnsupportedOperationException("Unsupported actionType: $actionType")
        }
    }

    fun addStepResult(result: StepResult<*>) {
        addedResult.set(true)
        taskPresentationViewModel.addStepResult(result)
    }
}
