package org.researchstack.backbone.ui.step.fragments

import android.os.Bundle
import android.view.View
import org.researchstack.backbone.R
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.ui.step.layout.StepLayout

internal class ReviewStepFragment : BaseStepFragment(R.layout.rsb_fragment_review_step_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Provide the latest version of the Steps and Responses to the Layout (which has no direct
        // access to the viewModel.
        (view as ReviewStepLayout).taskResult = viewModel.taskResult
    }
}

/**
 * Special Sub-class of [StepLayout] which exposes the [TaskResult].
 * Used and needed by [ReviewStepFragment].
 */
interface ReviewStepLayout : StepLayout {
    var taskResult: TaskResult?
}



