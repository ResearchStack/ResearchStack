package org.researchstack.foundation.components.presentation.compatibility

import android.os.Bundle
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks
import org.researchstack.foundation.components.presentation.TaskPresentationCallback
import org.researchstack.foundation.components.presentation.TaskPresentationFragment
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.models.result.StepResult
import org.researchstack.foundation.core.models.result.TaskResult
import org.researchstack.foundation.core.models.step.Step
import org.researchstack.foundation.core.models.task.Task
import java.util.*

open class BackwardsCompatibleTaskPresentationFragment: TaskPresentationFragment<Step, TaskResult, Task>(), StepCallbacks {

    companion object {
        val EXTRA_TASK_IDENTIFIER = "BackwardsCompatibleTaskPresentationFragment.ExtraTaskIdentifier"
        val EXTRA_TASK_RESULT = "BackwardsCompatibleTaskPresentationFragment.ExtraTaskResult"
        val EXTRA_STEP = "BackwardsCompatibleTaskPresentationFragment.ExtraStep"

        fun newInstance(
                taskIdentifier: String,
                stepFragmentProvider: IStepFragmentProvider,
                callback: TaskPresentationCallback<TaskResult, Task>
        ): BackwardsCompatibleTaskPresentationFragment {
            val fragment = BackwardsCompatibleTaskPresentationFragment()
            val args = Bundle()
            args.putString(EXTRA_TASK_IDENTIFIER, taskIdentifier)
            fragment.setArguments(args)
            fragment.stepFragmentProvider = stepFragmentProvider
            fragment.callback = callback
            return fragment
        }

    }

    override fun initialize(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            val taskIdentifier = this.arguments!!.getString(BackwardsCompatibleTaskPresentationFragment.EXTRA_TASK_IDENTIFIER)
            this._task = this.taskProvider!!.task(taskIdentifier) as Task
            this._taskNavigator = this.task
            this._result = TaskResult(this.task.identifier)
            this.result.setStartDate(Date())
        } else {
            val taskIdentifier = savedInstanceState.getString(BackwardsCompatibleTaskPresentationFragment.EXTRA_TASK_IDENTIFIER)
            this._task = this.taskProvider!!.task(taskIdentifier) as Task
            this._taskNavigator = this.task
            this._result = savedInstanceState.getSerializable(BackwardsCompatibleTaskPresentationFragment.EXTRA_TASK_RESULT) as TaskResult
            this._currentStep = savedInstanceState.getSerializable(BackwardsCompatibleTaskPresentationFragment.EXTRA_STEP) as Step
        }

        this.task.validateParameters()
    }

    override fun getStepResult(taskResult: TaskResult, stepIdentifier: String): IResult? {
        return taskResult.getStepResult(stepIdentifier)
    }

    override fun setStepResult(taskResult: TaskResult, stepIdentifier: String, stepResult: IResult) {
        taskResult.setStepResultForStepIdentifier(stepIdentifier, stepResult as StepResult<*>)
    }

    override fun saveAndFinish(clearResult: Boolean) {
        if (clearResult) {
            this.callback!!.onTaskPresentationFinished(this.task, null)
        }
        else {
            this.result.setEndDate(Date())
            this.callback!!.onTaskPresentationFinished(this.task, this.result)
        }
    }
}