package org.researchstack.foundation.components.presentation.compatibility

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import org.researchstack.foundation.components.presentation.TaskPresentationFragment
import org.researchstack.foundation.components.presentation.TaskPresentationViewModelFactory
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider
import org.researchstack.foundation.core.models.result.TaskResult
import org.researchstack.foundation.core.models.step.Step
import org.researchstack.foundation.core.models.task.Task
import java.util.*

/**
 * Delegates some actions for :backbone ViewTaskActivity, hosts BackwardsCompatibleTaskPresentationFragments
 * that replicate :backbone Step/StepLayout/StepCallbacks functionality, while running on :foundation.
 */
open class BackwardsCompatibleTaskPresentationFragment : TaskPresentationFragment<Step, TaskResult, Task>() {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun createInstance(taskIdentifier: String, taskRunUUID: UUID = UUID.randomUUID(),
                           taskViewModelFactory: TaskPresentationViewModelFactory<Step>,
                           stepFragmentProvider: IStepFragmentProvider<Step>): BackwardsCompatibleTaskPresentationFragment {
            val bundle = createBundle(taskIdentifier, taskRunUUID)

            val fragment = BackwardsCompatibleTaskPresentationFragment()
            fragment.arguments = bundle
            fragment.inject(taskViewModelFactory, stepFragmentProvider)

            return fragment
        }
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
}