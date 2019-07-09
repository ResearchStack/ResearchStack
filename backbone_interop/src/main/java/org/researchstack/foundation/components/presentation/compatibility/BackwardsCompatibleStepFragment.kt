package org.researchstack.foundation.components.presentation.compatibility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.researchstack.backbone.interop.ResultFactory
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.callbacks.StepCallbacks
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.foundation.R
import org.researchstack.foundation.components.presentation.ActionType
import org.researchstack.foundation.components.presentation.StepPresentationFragment
import org.researchstack.foundation.components.presentation.StepPresentationViewModelFactory
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.UIStep

/**
 * Delegates between :backbone StepLayout, StepCallback classes and :foundation Fragments.
 */
class BackwardsCompatibleStepFragment : StepPresentationFragment<UIStep, IResult>(), StepCallbacks {

    companion object {
        /**
         * Returns an instance of this fragment that delegates for a given StepLayout.
         */
        @JvmStatic
        fun newInstance(stepLayout: StepLayout, stepPresentationViewModelFactory: StepPresentationViewModelFactory<UIStep>, resultFactory: ResultFactory): BackwardsCompatibleStepFragment {
            val fragment = BackwardsCompatibleStepFragment()
            fragment.stepLayout = stepLayout
            fragment.inject(stepPresentationViewModelFactory)
            fragment.inject(resultFactory)
            return fragment
        }
    }

    // inject
    private lateinit var resultFactory: ResultFactory

    fun inject(resultFactory: ResultFactory) {
        this.resultFactory = resultFactory
    }

    //this will implement the traditional step layout
    lateinit var stepLayout: StepLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutId(), container, false)
        val containerView: FrameLayout = view.findViewById(R.id.rsf_content_layout)


        val toolbar = view.findViewById(R.id.toolbar) as Toolbar?


        stepLayout.setCallbacks(this)
        val lp = getLayoutParams(stepLayout)
        containerView.addView(stepLayout.layout, 0, lp)


        val appCompatActivity: AppCompatActivity = this.activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(toolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return view
    }

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

    /**
     * Delegates StepCallbacks Step actions to :foundation equivalents.
     */
    override fun onSaveStep(action: Int, step: Step?, result: StepResult<*>?) {
        result?.let {
            taskPresentationFragment.taskPresentationViewModel.addStepResult(resultFactory.create(result))
        }
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