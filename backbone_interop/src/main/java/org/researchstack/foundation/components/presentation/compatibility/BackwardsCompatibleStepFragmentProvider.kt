package org.researchstack.foundation.components.presentation.compatibility

import android.content.Context
import androidx.fragment.app.Fragment
import org.researchstack.backbone.interop.ResultFactory
import org.researchstack.backbone.interop.StepAdapterFactory
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.foundation.components.presentation.StepPresentationViewModelFactory
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider
import org.researchstack.foundation.core.interfaces.UIStep


class BackwardsCompatibleStepFragmentProvider(val context: Context, val stepAdapterFactory: StepAdapterFactory, val resultFactory: ResultFactory) : IStepFragmentProvider<UIStep> {

    override fun stepFragment(step: UIStep, stepPresentationViewModelFactory: StepPresentationViewModelFactory<UIStep>): Fragment? {

        try {
            val backboneStep = stepAdapterFactory.create(step)
            val stepLayout = backboneStep.stepLayoutClass?.let {
                val constructor = it.getConstructor(Context::class.java)
                constructor.newInstance(context)
            } as StepLayout

            if (stepLayout != null) {
                stepLayout.initialize(backboneStep, null)
                return BackwardsCompatibleStepFragment.newInstance(stepLayout, stepPresentationViewModelFactory, resultFactory)
            } else {
                throw RuntimeException("Could not instantiate Step Layout")
            }

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}