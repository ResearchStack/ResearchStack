package org.researchstack.foundation.components.presentation

import androidx.lifecycle.ViewModel
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import org.threeten.bp.Instant
import java.util.concurrent.atomic.AtomicBoolean

open class StepPresentationViewModel<StepType : IStep, ResultType : IResult>
(val taskPresentationViewModel: TaskPresentationViewModel<in StepType, in ResultType>) : ViewModel() {
    private val addedResult: AtomicBoolean = AtomicBoolean()
    private val startTime: Instant = Instant.now()

    fun handleAction(actionType: String) {
        when (actionType) {
            ActionType.FORWARD -> {
                if (!addedResult.get()) {
                    // If for whatever reason the step didn't create a result matching it's identifier we create a
                    // ResultBase to mark that the step completed.
                    // todo joliu add step result
//                    addStepResult(ResultBase(stepView.getIdentifier(), startTimestamp, Instant.now()))
                }
                taskPresentationViewModel.goForward()
            }
            ActionType.BACKWARD -> taskPresentationViewModel.goBack()
            else -> throw UnsupportedOperationException("Unsupported actionType: $actionType")
        }
    }

    protected fun addStepResult(result: ResultType) {
        addedResult.set(true)
        taskPresentationViewModel.addStepResult(result)
    }
}
