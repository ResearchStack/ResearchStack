package org.researchstack.backbone.ui.step.fragments

import android.os.Bundle
import android.view.View
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.step.layout.StepLayout

internal class ReviewStepFragment : BaseStepFragment(R.layout.rsb_fragment_review_step_layout),
        ReviewStepRowClickHandler {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view as ReviewStepLayout) {
            // Wire the Layout dependencies.
            this.clickHandler = this@ReviewStepFragment
            this.taskSteps = viewModel.task.steps
            this.taskResult = viewModel.currentTaskResult
        }
    }

    override fun onStepTappedForEdition(step: Step, stepResult: StepResult<Any>) {
        viewModel.edit(step)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (view as ReviewStepLayout).clickHandler = null
    }
}

/**
 * Special Sub-class of [StepLayout] which exposes the [TaskResult].
 * Used and needed by [ReviewStepFragment].
 */
interface ReviewStepLayout : StepLayout {
    var clickHandler: ReviewStepRowClickHandler?
    var taskResult: TaskResult?
    var taskSteps: List<Step>?
}

/**
 * Contract for the ReviewStep RecyclerView implementation to notify that the user tapped on a
 * particular [Step]; the [StepResult] is also included, although it can contain null responses if
 * the step was skipped for example.  *
 * */
interface ReviewStepRowClickHandler {
    fun onStepTappedForEdition(step: Step, stepResult: StepResult<Any>)
}

