package org.researchstack.foundation.components.presentation.compatibility

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import org.researchstack.foundation.components.presentation.TaskPresentationFragment
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.models.result.StepResult
import org.researchstack.foundation.core.models.result.TaskResult
import org.researchstack.foundation.core.models.step.Step
import org.researchstack.foundation.core.models.task.Task

open class BackwardsCompatibleTaskPresentationFragment : TaskPresentationFragment<Step, TaskResult, Task>() {

    companion object {
        @JvmField
        val EXTRA_TASK_IDENTIFIER = "BackwardsCompatibleTaskPresentationFragment.ExtraTaskIdentifier"
        @JvmField
        val EXTRA_TASK_RESULT = "BackwardsCompatibleTaskPresentationFragment.ExtraTaskResult"
        @JvmField
        val EXTRA_STEP = "BackwardsCompatibleTaskPresentationFragment.ExtraStep"

        fun newInstance(
                taskIdentifier: String,
                stepFragmentProvider: IStepFragmentProvider
        ): BackwardsCompatibleTaskPresentationFragment {
            val fragment = BackwardsCompatibleTaskPresentationFragment()
            val args = Bundle()
            args.putString(EXTRA_TASK_IDENTIFIER, taskIdentifier)
            fragment.setArguments(args)
            fragment.stepFragmentProvider = stepFragmentProvider
            return fragment
        }

    }

    override fun initialize(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            val taskIdentifier: String = this.arguments!!.getString(BackwardsCompatibleTaskPresentationFragment.EXTRA_TASK_IDENTIFIER)!!
            this._task = this.taskProvider.task(taskIdentifier) as Task
            this._taskNavigator = this.task
//            this._result = TaskResult(this.task.identifier)
//            this.result.setStartDate(Date())
        } else {
            val taskIdentifier: String = savedInstanceState.getString(BackwardsCompatibleTaskPresentationFragment.EXTRA_TASK_IDENTIFIER)!!
            this._task = this.taskProvider.task(taskIdentifier) as Task
            this._taskNavigator = this.task
//            this._result = savedInstanceState.getSerializable(BackwardsCompatibleTaskPresentationFragment.EXTRA_TASK_RESULT) as TaskResult
            this._currentStep = savedInstanceState.getSerializable(BackwardsCompatibleTaskPresentationFragment.EXTRA_STEP) as Step
        }

        this.task.validateParameters()
    }

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && imm.isAcceptingText) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

    override fun getStepResult(taskResult: TaskResult, stepIdentifier: String): IResult? {
        return taskResult.getStepResult(stepIdentifier)
    }

    override fun setStepResult(taskResult: TaskResult, stepIdentifier: String, stepResult: IResult) {
        taskResult.setStepResultForStepIdentifier(stepIdentifier, stepResult as StepResult<*>)
    }

    fun notifyStepOfBackPressed() {
        (_currentStepFragment as BackwardsCompatibleStepFragment).notifyStepOfBackPressed()
    }
}