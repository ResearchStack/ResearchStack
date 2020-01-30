package org.researchstack.foundation.components.presentation.interfaces

import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.models.task.Task

interface ITaskNavigator<StepType : IStep, ResultType : IResult> {
    fun getStepWithIdentifier(identifier: String): StepType?
    fun getStepAfterStep(step: StepType?, result: ResultType): StepType?
    fun getStepBeforeStep(step: StepType?, result: ResultType): StepType?
    fun getProgressOfCurrentStep(step: StepType, result: ResultType): Task.TaskProgress
    fun validateParameters()
}