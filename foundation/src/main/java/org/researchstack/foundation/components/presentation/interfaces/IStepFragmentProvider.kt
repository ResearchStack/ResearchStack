package org.researchstack.foundation.components.presentation.interfaces

import androidx.fragment.app.Fragment
import org.researchstack.foundation.components.presentation.StepPresentationViewModelFactory
import org.researchstack.foundation.core.interfaces.IStep

interface IStepFragmentProvider<StepType : IStep> {
    fun stepFragment(step: StepType, stepPresentationViewModelFactory: StepPresentationViewModelFactory<StepType>): Fragment?
}