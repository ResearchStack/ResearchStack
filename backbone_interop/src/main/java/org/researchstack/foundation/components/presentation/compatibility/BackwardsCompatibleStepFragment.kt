package org.researchstack.foundation.components.presentation.compatibility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.researchstack.backbone.interop.ResultFactory
import org.researchstack.backbone.interop.StepAdapterFactory
import org.researchstack.backbone.interop.StepCallbackAdapter
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.callbacks.StepCallbacks
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.foundation.R
import org.researchstack.foundation.components.presentation.ActionType
import org.researchstack.foundation.components.presentation.StepPresentationFragment
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep

class BackwardsCompatibleStepFragment() : StepPresentationFragment<IStep, IResult>(), StepCallbacks {
    override fun onSaveStep(action: Int, step: Step?, result: StepResult<*>?) {
        taskPresentationFragment.taskPresentationViewModel.addStepResult(resultFactory.create(result))

        if (action == StepCallbacks.ACTION_NEXT) {
            stepPresentationViewModel.handleAction(ActionType.FORWARD)
        } else if (action == StepCallbacks.ACTION_PREV) {
            stepPresentationViewModel.handleAction(ActionType.BACKWARD)
        } else if (action == StepCallbacks.ACTION_END) {
            stepPresentationViewModel.handleAction(ActionType.CANCEL)
        } else if (action == StepCallbacks.ACTION_NONE) {
            // Used when onSaveInstanceState is called of a view. No action is taken.
        } else {
            throw IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See StepCallbacks for allowable arguments")
        }
    }

    override fun onCancelStep() {
        taskPresentationFragment.showConfirmExitDialog()
    }

    override fun getLayoutId(): Int {
        return R.layout.rsf_fragment_step_compat
    }

    companion object {
        fun newInstance(stepLayout: StepLayout): BackwardsCompatibleStepFragment {
            val fragment = BackwardsCompatibleStepFragment()
            fragment.stepLayout = stepLayout
            return fragment
        }
    }

    lateinit var resultFactory: ResultFactory
    lateinit var stepFactory: StepAdapterFactory
    lateinit var stepCallbackAdapter: StepCallbackAdapter

    //this will implement the traditional step layout
    lateinit var stepLayout: StepLayout

    private fun getLayoutParams(stepLayout: StepLayout): FrameLayout.LayoutParams {
        var lp: FrameLayout.LayoutParams? = stepLayout.layout.layoutParams?.let {
            it as? FrameLayout.LayoutParams
        }
        if (lp == null) {
            lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)
        }
        return lp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutId(), container, false)
        val containerView: FrameLayout = view.findViewById(R.id.rsf_content_layout)

        val layout = this.stepLayout

        layout.setCallbacks(this)
        val lp = getLayoutParams(layout)
        containerView.addView(layout.layout, 0, lp)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            notifyStepOfBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun notifyStepOfBackPressed() {
        stepLayout.isBackEventConsumed
    }
}