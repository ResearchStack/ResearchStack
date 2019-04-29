package org.researchstack.foundation.components.presentation

import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.ITask

abstract class TaskPresentationCallback<ResultType: IResult, TaskType: ITask> {
    abstract fun onTaskPresentationFinished(task: TaskType, result: ResultType?)
}