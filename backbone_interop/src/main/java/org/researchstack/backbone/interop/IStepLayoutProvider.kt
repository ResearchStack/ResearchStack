package org.researchstack.backbone.interop

import org.researchstack.foundation.core.interfaces.IStep

interface IStepLayoutProvider {
    fun stepLayout(step: IStep): Class<*>?
}