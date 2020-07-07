package org.researchstack.backbone.ui.step.layout

import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.step.ReviewStepRowClickHandler

/**
 * Special Sub-class of [StepLayout] which exposes the [TaskResult].
 * Used and needed by [ReviewStepFragment].
 */
interface ReviewStepLayout : StepLayout {
    var clickHandler: ReviewStepRowClickHandler?
    var taskResult: TaskResult?
}