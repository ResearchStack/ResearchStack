package org.researchstack.foundation.components.presentation.compatibility

import org.researchstack.foundation.core.interfaces.IStep

interface IStepLayoutProvider {
    fun stepLayout(step: IStep): Class<*>?
}