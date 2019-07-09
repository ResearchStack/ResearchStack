package org.researchstack.foundation.components.presentation.compatibility

import org.researchstack.backbone.interop.StepAdapterFactory
import org.researchstack.foundation.core.interfaces.IStep

class BackwardsCompatibleStepLayoutProvider(val stepAdapterFactory: StepAdapterFactory): IStepLayoutProvider {
    override fun stepLayout(step: IStep): Class<*>? {
        return stepAdapterFactory.create(step).stepLayoutClass
    }
}