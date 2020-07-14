package org.researchstack.backbone.ui.task

import org.researchstack.backbone.step.Step

@Deprecated("Deprecated as part of the new handling for the branching logic",
        ReplaceWith("com.medable.axon.ui.taskrunner.NRSStepNavigationEvent"))
internal class StepNavigationEvent(val popUpToStep: Step? = null, val step: Step, val isMovingForward: Boolean = true)