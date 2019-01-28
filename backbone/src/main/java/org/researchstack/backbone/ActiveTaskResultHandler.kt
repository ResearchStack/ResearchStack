package org.researchstack.backbone

import org.researchstack.backbone.result.TaskResult

interface ActiveTaskResultHandler {
    fun handleActiveTaskResult(result: TaskResult)
}