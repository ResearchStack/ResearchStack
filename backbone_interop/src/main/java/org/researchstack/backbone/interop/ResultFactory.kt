package org.researchstack.backbone.interop

import org.researchstack.backbone.result.StepResult
import org.researchstack.foundation.core.interfaces.IResult

interface ResultFactory {
    fun create(result: org.researchstack.foundation.core.models.result.StepResult<*>): StepResult<*>
    fun create(result: StepResult<*>): org.researchstack.foundation.core.models.result.StepResult<*>
}
