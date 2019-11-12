package org.researchstack.backbone.ui.step.fragments

import android.os.Bundle
import android.view.View
import org.researchstack.backbone.R

internal class ReviewStepFragment : BaseStepFragment(R.layout.rsb_fragment_review_step_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskResult = viewModel.taskResult
        // TODO: How do I get access to RSReviewLayout that is in Axon to pass it the data?
    }
}



