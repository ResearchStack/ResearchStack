package org.researchstack.foundation.components.presentation.compatibility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import org.researchstack.backbone.interop.ResultFactory
import org.researchstack.backbone.interop.StepAdapterFactory
import org.researchstack.backbone.interop.StepCallbackAdapter
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.foundation.R
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks
import org.researchstack.foundation.components.presentation.interfaces.IStepFragment
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep

public class BackwardsCompatibleStepFragment() : androidx.fragment.app.Fragment(), IStepFragment {

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

    override val fragment: Fragment
        get() = this

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

        val layout = this.stepLayout
        val view = inflater.inflate(R.layout.rsf_fragment_step_compat, container, false)
        val containerView: FrameLayout = view.findViewById(R.id.rsf_content_layout) as FrameLayout

        val lp = getLayoutParams(layout)
        containerView.addView(layout.layout, 0, lp)

        return view
    }

    override fun onBackPressed() {
        this.stepLayout.isBackEventConsumed
    }

    override fun initialize(step: IStep, result: IResult?) {
        this.stepLayout.initialize(stepFactory.create(step), resultFactory.create(result))
    }

    override fun setCallbacks(callbacks: StepCallbacks) {
        this.stepLayout.setCallbacks(stepCallbackAdapter.create(callbacks))
    }
}