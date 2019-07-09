package org.researchstack.backbone.interop

import org.researchstack.backbone.result.StepResult

interface ResultFactory {
    fun <E> create(result: org.researchstack.foundation.core.models.result.StepResult<E>): StepResult<E>
    fun <E> create(result: StepResult<E>): org.researchstack.foundation.core.models.result.StepResult<E>
}
