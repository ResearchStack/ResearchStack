package org.researchstack.backbone.ui.step

import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.Step

/**
 * Contract for the ReviewStep RecyclerView implementation to notify that the user tapped on a
 * particular [Step]; the [StepResult] is also included, although it can contain null responses if
 * the step was skipped for example.  *
 * */
@Deprecated(message = "This is no longer needed as ResearchStack's ReviewStepLayout class is deprecated, please use Axon's ReviewStepRowClickHandler() instead ")
interface ReviewStepRowClickHandler {
    fun onStepTappedForEdition(step: Step, stepResult: StepResult<Any>)

    fun onImageCaptureStepTappedForFullScreen(step: Step, imageUrl: String)
}