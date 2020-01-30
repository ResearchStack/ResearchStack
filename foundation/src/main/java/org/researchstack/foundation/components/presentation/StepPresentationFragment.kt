package org.researchstack.foundation.components.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import org.researchstack.foundation.R
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.ITask
import org.researchstack.foundation.core.interfaces.UIStep

abstract class StepPresentationFragment<StepType : UIStep, ResultType : IResult> : Fragment() {

    //inject
    lateinit var stepPresentationViewModelProviderFactory: ViewModelProvider.Factory

    fun inject(stepPresentationViewModelFactory: ViewModelProvider.Factory) {
        this.stepPresentationViewModelProviderFactory = stepPresentationViewModelFactory
    }

    protected lateinit var stepPresentationViewModel: StepPresentationViewModel<StepType>

    protected lateinit var taskPresentationFragment: TaskPresentationFragment<StepType, ResultType, ITask>

    protected lateinit var taskPresentationViewModel: TaskPresentationViewModel<StepType>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        @Suppress("UNCHECKED_CAST")
        taskPresentationFragment = parentFragment as TaskPresentationFragment<StepType, ResultType, ITask>
        @Suppress("UNCHECKED_CAST")
        taskPresentationViewModel = ViewModelProviders.of(taskPresentationFragment).get(TaskPresentationViewModel::class.java) as TaskPresentationViewModel<StepType>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        stepPresentationViewModel = ViewModelProviders.of(this, stepPresentationViewModelProviderFactory)
                .get(StepPresentationViewModel::class.java) as StepPresentationViewModel<StepType>
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.rsf_step_presentation_fragment, container, false)
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
