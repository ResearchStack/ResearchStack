package org.researchstack.backbone.ui.step.fragments

import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.step.FormStep
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.callbacks.StepCallbacks
import org.researchstack.backbone.ui.step.layout.ConsentVisualStepLayout
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.backbone.ui.step.layout.SurveyStepLayout
import org.researchstack.backbone.ui.task.TaskViewModel

internal open class BaseStepFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId), StepCallbacks {

    override fun onSkipStep(step: Step?, originalStepResult: StepResult<*>?, modifiedStepResult: StepResult<*>?) {
        viewModel.checkForSkipDialog(modifiedStepResult)
    }

    override fun onEditCancelStep() {
        viewModel.showCancelEditAlert()
    }

    protected val viewModel: TaskViewModel by sharedViewModel()

    override fun onResume() {
        super.onResume()

        val currentStep = viewModel.currentStep
        val stepResult = viewModel.currentTaskResult.getStepResult(currentStep?.identifier)

        currentStep?.setStepTheme(viewModel.colorPrimary, viewModel.colorPrimaryDark, viewModel.colorSecondary,
                viewModel.principalTextColor, viewModel.secondaryTextColor, viewModel.actionFailedColor)

        if (currentStep is FormStep) {
            currentStep.getFormSteps()?.let { questions ->
                questions.forEach {
                    with(viewModel) {
                        it.setStepTheme(colorPrimary, colorPrimaryDark, colorSecondary, principalTextColor,
                                secondaryTextColor, actionFailedColor)
                    }
                }
            }

        }
        when (val stepView = view?.findViewById<View>(R.id.stepView)) {
            is SurveyStepLayout -> {
                stepView.initialize(currentStep, stepResult,
                        viewModel.colorPrimary, viewModel.colorSecondary, viewModel.principalTextColor,
                        viewModel.secondaryTextColor)
                stepView.isStepEmpty.observe(this, Observer { })
                setupStepCallbacks(stepView)

            }
            is StepLayout -> {
                stepView.initialize(currentStep, stepResult)
                setupStepCallbacks(stepView)
            }
            is ConsentVisualStepLayout -> stepView.initialize(currentStep, stepResult,
                    viewModel.colorPrimary, viewModel.colorSecondary, viewModel.principalTextColor,
                    viewModel.secondaryTextColor)
            else -> {
                Log.d("BaseStepFragment", "WARNING: Unknown stepView type, cannot initialize layout")
            }
        }

    }

    private fun setupStepCallbacks(stepView: StepLayout) {
        stepView.setCallbacks(this)
        stepView.isEditView(viewModel.editing)
        viewModel.updateCancelEditInLayout.observe(this, Observer {
            stepView.setCancelEditMode(it)
        })

        viewModel.stepBackNavigationState.observe(this, Observer {
            stepView.setRemoveFromBackStack(it)
        })
    }

    override fun onSaveStep(action: Int, step: Step, result: StepResult<*>?) {
        viewModel.currentTaskResult.setStepResultForStep(step, result)
        when (action) {
            StepCallbacks.ACTION_NEXT -> viewModel.nextStep()
            StepCallbacks.ACTION_PREV -> viewModel.previousStep()
            StepCallbacks.ACTION_END -> viewModel.taskCompleted.value = true
            StepCallbacks.ACTION_SAVE -> {
                viewModel.checkForSaveDialog()
            }
            StepCallbacks.ACTION_NONE -> {
                // Used when onSaveInstanceState is called of a view. No action is taken.
            }
            else -> throw IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See StepCallbacks for allowable arguments")
        }
    }

    override fun onCancelStep() {
        viewModel.taskCompleted.value = true
    }

    override fun setActionbarVisible(setVisible: Boolean) {
        requireActivity().actionBar?.let {
            it.setHomeButtonEnabled(setVisible)
            it.setDisplayShowHomeEnabled(setVisible)
            it.setDisplayHomeAsUpEnabled(setVisible)
        }
    }

    fun saveCurrentStepResult() {
        val stepView = view?.findViewById<View>(R.id.stepView)
        if (viewModel.currentStep != null && stepView is StepLayout) {
            onSaveStep(StepCallbacks.ACTION_NONE, viewModel.currentStep!!, stepView.stepResult)
        }
    }
}