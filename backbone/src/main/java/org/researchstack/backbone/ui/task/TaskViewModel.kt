package org.researchstack.backbone.ui.task

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.task.Task
import org.researchstack.backbone.ui.SingleLiveEvent
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_TASK_RESULT
import java.util.*

internal class TaskViewModel(context: Application, intent: Intent) : AndroidViewModel(context) {

    var taskResult: TaskResult
    var currentStep: Step? = null

    val task: Task
    val colorPrimary = intent.getIntExtra(EXTRA_COLOR_PRIMARY, R.color.rsb_colorPrimary)
    val colorPrimaryDark = intent.getIntExtra(EXTRA_COLOR_PRIMARY_DARK, R.color.rsb_colorPrimaryDark)
    val colorSecondary = intent.getIntExtra(EXTRA_COLOR_SECONDARY, R.color.rsb_colorAccent)
    val principalTextColor = intent.getIntExtra(EXTRA_PRINCIPAL_TEXT_COLOR, R.color.rsb_cell_header_grey)
    val secondaryTextColor = intent.getIntExtra(EXTRA_SECONDARY_TEXT_COLOR, R.color.rsb_item_text_grey)
    val actionFailedColor = intent.getIntExtra(EXTRA_ACTION_FAILED_COLOR, R.color.rsb_error)

    val taskCompleted = SingleLiveEvent<Boolean>()
    val currentStepEvent = MutableLiveData<StepNavigationEvent>()

    init {
        task = intent.getSerializableExtra(EXTRA_TASK) as Task
        taskResult = intent.extras?.get(EXTRA_TASK_RESULT) as TaskResult?
                ?: TaskResult(task.identifier).apply { startDate = Date() }

        task.validateParameters()
    }

    fun nextStep() {
        val nextStep = task.getStepAfterStep(currentStep, taskResult)

        if (nextStep == null) {
            close(true)
        } else {
            currentStep = nextStep

            if (nextStep.isHidden) {
                // We will do the save for this step and then go to the nextStep step
                setHiddenStepResult(nextStep)
                nextStep()
            } else {
                currentStepEvent.value = StepNavigationEvent(nextStep)
            }
        }
    }

    fun previousStep() {
        val previousStep = task.getStepBeforeStep(currentStep, taskResult)

        if (previousStep == null) {
            close()
        } else {
            currentStep = previousStep

            if (previousStep.isHidden) {
                // The previous step was a hidden one so we go previousStep again
                previousStep()
            } else {
                currentStepEvent.value = StepNavigationEvent(previousStep, false)
            }
        }
    }

    private fun close(completed: Boolean = false) {
        if (completed) {
            taskResult.endDate = Date()
        }

        taskCompleted.value = completed
    }

    private fun setHiddenStepResult(step: Step) {
        val result: StepResult<Any> = taskResult.getStepResult(step.identifier) ?: StepResult(step)

        result.result = step.hiddenDefaultValue
        taskResult.setStepResultForStep(step, result)
    }

    companion object {
        const val EXTRA_TASK = "TaskActivity.ExtraTask"
        const val EXTRA_COLOR_PRIMARY = "TaskActivity.ExtraColorPrimary"
        const val EXTRA_COLOR_PRIMARY_DARK = "TaskActivity.ExtraColorPrimaryDark"
        const val EXTRA_COLOR_SECONDARY = "TaskActivity.ExtraColorSecondary"
        const val EXTRA_PRINCIPAL_TEXT_COLOR = "TaskActivity.ExtraPrincipalTextColor"
        const val EXTRA_SECONDARY_TEXT_COLOR = "TaskActivity.ExtraSecondaryTextColor"
        const val EXTRA_ACTION_FAILED_COLOR = "TaskActivity.ExtraActionFailedColor"
    }
}