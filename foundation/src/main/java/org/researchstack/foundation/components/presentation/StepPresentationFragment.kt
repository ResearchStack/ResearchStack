package org.researchstack.foundation.components.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.researchstack.foundation.R
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.ITask
import org.researchstack.foundation.core.interfaces.UIStep

abstract class StepPresentationFragment<StepType : UIStep, ResultType : IResult> : Fragment() {

    //inject
    lateinit var stepPresentationViewModelFactory: StepPresentationViewModelFactory<StepType>

    fun inject(stepPresentationViewModelFactory: StepPresentationViewModelFactory<StepType>) {
        this.stepPresentationViewModelFactory = stepPresentationViewModelFactory
    }

    protected lateinit var stepPresentationViewModel: StepPresentationViewModel<StepType>

    protected lateinit var taskPresentationFragment: TaskPresentationFragment<StepType, ResultType, ITask>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.rsf_step_presentation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        taskPresentationFragment = parentFragment as TaskPresentationFragment<StepType, ResultType, ITask>
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        stepPresentationViewModel = ViewModelProviders.of(this, stepPresentationViewModelFactory.create())
                .get(StepPresentationViewModel::class.java) as StepPresentationViewModel<StepType>
    }

    /**
     * Returns the layout resource that corresponds to the layout for this fragment.
     *
     * @return the layout resource that corresponds to the layout for this fragment.
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     * Called whenever one of this fragment's ActionButton's is clicked. Subclasses should override to correctly
     * handle their ActionButtons.
     *
     * @param actionButton
     * the ActionButton that was clicked by the user.
     */
    protected fun handleActionButtonClick(@ActionType actionType: String) {
        if (actionType == ActionType.CANCEL) {
            // handled in the Fragment to present UI
            this.taskPresentationFragment.showConfirmExitDialog()
        } else {
            // handled by the ViewModel to modify the Task/Step state
            this.stepPresentationViewModel.handleAction(actionType)
        }
    }
}
