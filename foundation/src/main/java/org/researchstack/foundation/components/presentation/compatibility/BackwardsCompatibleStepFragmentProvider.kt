package org.researchstack.foundation.components.presentation.compatibility

import android.content.Context
import org.researchstack.foundation.components.common.ui.layout.StepLayout
import org.researchstack.foundation.components.presentation.interfaces.IStepFragment
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider
import org.researchstack.foundation.core.interfaces.IStep


public class BackwardsCompatibleStepFragmentProvider(val stepLayoutProvider: IStepLayoutProvider): IStepFragmentProvider {

    override fun stepFragment(context: Context, step: IStep): IStepFragment? {

        try {

            val stepLayout = this.stepLayoutProvider.stepLayout(step)?.let {
                val constructor = it.getConstructor(Context::class.java)
                constructor.newInstance(context)
            } as? StepLayout

            if (stepLayout != null) {
                val fragment = BackwardsCompatibleStepFragment.newInstance(stepLayout)
                return fragment
            }
            else {
                throw RuntimeException("Could not instantiate Step Layout")
            }

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}