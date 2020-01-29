package org.researchstack.backbone.ui.step.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.backbone.ui.task.ReviewStepFullScreenImageActivity

internal class ReviewStepFragment : BaseStepFragment(R.layout.rsb_fragment_review_step_layout),
                                    ReviewStepRowClickHandler {
    companion object {
        private const val REQUEST_FULL_SCREEN_VIEW = 101
        const val RESULT_FULL_SCREEN_EDIT = 201
        const val EXTRA_FULL_SCREEN_STEP = "full_screen_step"
    }

    private var stepToEdit: Step? = null

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

    override fun onImageCaptureStepTappedForFullScreen(step: Step, imageUrl: String) {
        val intent = ReviewStepFullScreenImageActivity.getCallingIntent(requireContext(), step, imageUrl)
        startActivityForResult(intent, REQUEST_FULL_SCREEN_VIEW)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FULL_SCREEN_VIEW) {
            if (resultCode == RESULT_FULL_SCREEN_EDIT) {
                // This is set to be handled in onResume() because we need to make sure the super.onResume() is
                // called before we edit this step.
                stepToEdit = data?.getSerializableExtra(EXTRA_FULL_SCREEN_STEP) as Step?
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepToEdit?.let {
            viewModel.edit(it)
            stepToEdit = null
        }
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

    fun onImageCaptureStepTappedForFullScreen(step: Step, imageUrl: String)
}

