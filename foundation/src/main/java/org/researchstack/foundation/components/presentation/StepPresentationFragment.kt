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

    protected lateinit var stepPresentationViewModel: StepPresentationViewModel<StepType>

    protected lateinit var taskPresentationFragment: TaskPresentationFragment<StepType, ResultType, ITask>

    fun inject(stepPresentationViewModelFactory: StepPresentationViewModelFactory<StepType>) {
        this.stepPresentationViewModelFactory = stepPresentationViewModelFactory
    }

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

//    /**
//     * Returns the ActionType corresponding to the given ActionButton or null if the ActionType cannot be found.
//     * Default mapping of button id to ActionType is: rs2_step_navigation_action_forward -> ActionType.Forward
//     * rs2_step_navigation_action_backward -> ActionType.Backward rs2_step_navigation_action_skip -> ActionType.Skip
//     * rs2_step_header_cancel_button -> ActionType.CANCEL rs2_step_header_info_button -> ActionType.INFO
//     *
//     * @param actionButton
//     * The ActionButton to get the ActionType for.
//     * @return the Actiontype corresponding to the given ActionButton or null if the ActionType cannot be found.
//     */
//    @ActionType
//    protected fun getActionTypeFromActionButton(actionButton: ActionButton): String? {
//        val actionButtonId = actionButton.getId()
//
//        if (R.id.rs2_step_navigation_action_forward === actionButtonId) {
//            return ActionType.FORWARD
//        } else if (R.id.rs2_step_navigation_action_backward === actionButtonId) {
//            return ActionType.BACKWARD
//        } else if (R.id.rs2_step_navigation_action_skip === actionButtonId) {
//            return ActionType.SKIP
//        } else if (R.id.rs2_step_navigation_action_cancel === actionButtonId) {
//            return ActionType.CANCEL
//        } else if (R.id.rs2_step_navigation_action_info === actionButtonId) {
//            return ActionType.INFO
//        }
//
//        return null
//    }

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
            this.taskPresentationFragment.showConfirmExitDialog()
        } else {
            this.stepPresentationViewModel.handleAction(actionType)
        }
    }
}
