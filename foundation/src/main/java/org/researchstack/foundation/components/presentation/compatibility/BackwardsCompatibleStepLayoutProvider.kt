package org.researchstack.foundation.components.presentation.compatibility

import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.models.step.Step

public class BackwardsCompatibleStepLayoutProvider(): IStepLayoutProvider {
    override fun stepLayout(step: IStep): Class<*>? {
        return (step as? Step)?.stepLayoutClass
    }
}