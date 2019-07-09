package org.researchstack.foundation.components.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.researchstack.foundation.R
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep

class StepPresentationFragment<StepType : IStep, ResultType : IResult> : Fragment() {

    private lateinit var viewModel: StepPresentationViewModel<StepType, ResultType>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.step_presentation_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProviders.of(this)
                .get(StepPresentationViewModel::class.java) as StepPresentationViewModel<StepType, ResultType>
    }

}
