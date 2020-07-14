package org.researchstack.backbone.ui.task

import android.app.Application
import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.task.OrderedTask
import org.researchstack.backbone.task.Task
import org.researchstack.backbone.ui.SingleLiveEvent
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_ACTION_FAILED_COLOR
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_COLOR_PRIMARY
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_COLOR_PRIMARY_DARK
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_COLOR_SECONDARY
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_PRINCIPAL_TEXT_COLOR
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_SECONDARY_TEXT_COLOR
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_TASK
import org.researchstack.backbone.ui.task.TaskActivity.Companion.EXTRA_TASK_RESULT
import java.util.Date
import java.util.Stack
import java.util.UUID
import kotlin.properties.Delegates

@Deprecated("Deprecated as part of the new handling for the branching logic",
        ReplaceWith("com.medable.axon.ui.taskrunner.NRSTaskViewModel"))
internal class TaskViewModel(val context: Application, intent: Intent) : AndroidViewModel(context) {

    var editing: Boolean by Delegates.observable(false) { _, _, newValue ->
        hideMenuItemCancel.postValue(newValue)
    }
    var currentStep: Step? = null


    val firstStep: Step
        get() {
            return task.getStepAfterStep(null, taskResult)
        }

    val currentTaskResult: TaskResult
        get() {
            return clonedTaskResult ?: taskResult
        }

    val task: Task = intent.getSerializableExtra(EXTRA_TASK) as Task
    val colorPrimary = intent.getIntExtra(EXTRA_COLOR_PRIMARY, R.color.rsb_colorPrimary)
    val colorPrimaryDark = intent.getIntExtra(EXTRA_COLOR_PRIMARY_DARK, R.color.rsb_colorPrimaryDark)
    val colorSecondary = intent.getIntExtra(EXTRA_COLOR_SECONDARY, R.color.rsb_colorAccent)
    val principalTextColor = intent.getIntExtra(EXTRA_PRINCIPAL_TEXT_COLOR, R.color.rsb_cell_header_grey)
    val secondaryTextColor = intent.getIntExtra(EXTRA_SECONDARY_TEXT_COLOR, R.color.rsb_item_text_grey)
    val actionFailedColor = intent.getIntExtra(EXTRA_ACTION_FAILED_COLOR, R.color.rsb_error)

    val taskCompleted = SingleLiveEvent<Boolean>()
    val currentStepEvent = MutableLiveData<StepNavigationEvent>()
    val moveReviewStep = MutableLiveData<StepNavigationEvent>()
    val showCancelEditDialog = MutableLiveData<Boolean>()
    val updateCancelEditInLayout = MutableLiveData<Boolean>()
    val stepBackNavigationState = MutableLiveData<Boolean>()
    val hideMenuItemCancel = MutableLiveData<Boolean>()
    val showSaveEditDialog = MutableLiveData<Boolean>()
    val showSkipEditDialog = MutableLiveData<Pair<Boolean, StepResult<*>>>()
    val saveStepEvent = MutableLiveData<Pair<Step, StepResult<*>?>>()

    private var isSavedDialogAppeared = false
    @VisibleForTesting
    var taskResult: TaskResult
    private var clonedTaskResult: TaskResult? = null
    @VisibleForTesting
    var clonedTaskResultInCaseOfEdit: TaskResult? = null
    //only used for cancel edit
    private var hasBranching = false
    private val stack = Stack<Step>()

    init {
        taskResult = intent.extras?.get(EXTRA_TASK_RESULT) as TaskResult?
                ?: TaskResult(UUID.randomUUID().toString()).apply { startDate = Date() }

        task.validateParameters()
    }

    fun showCurrentStep() {
        if (currentStep == null) {
            nextStep()
        }
    }

    fun nextStep() {
        if (editing) {
            if (clonedTaskResult == null) {
                clonedTaskResult = TaskResult(taskResult.identifier)

                var step = task.getStepAfterStep(null, clonedTaskResult)

                while (step != null) {
                    val result = taskResult.getStepAndResult(step.identifier).second

                    if (result != null) {
                        clonedTaskResult?.setStepResultForStep(step, result)
                    }

                    step = task.getStepAfterStep(step, clonedTaskResult)
                }
            }
            var nextStep = task.getStepAfterStep(currentStep, currentTaskResult)
            // Current step with branches?
            hasBranching = clonedTaskResult?.getStepResult(nextStep.identifier) == null && isGFitStep(nextStep).not()


            if (hasBranching) {
                clearBranchingResults()
                stack.push(currentStep)
                currentStep = nextStep

            } else {
                nextStep = getReviewStep()
                currentStep = nextStep
            }


            if (isReviewStep(nextStep) || isCompletionStep(nextStep)) {
                clonedTaskResult?.let {
                    taskResult = updateTaskResultsFrom(it)
                }
                clonedTaskResult = null
                clonedTaskResultInCaseOfEdit = null
                editing = false
                stack.clear()
                moveReviewStep.postValue(StepNavigationEvent(step = nextStep))
            } else {
                currentStepEvent.value = StepNavigationEvent(step = nextStep)
            }


        } else {
            val nextStep = task.getStepAfterStep(currentStep, taskResult)

            if (nextStep == null) {
                close(true)
            } else {
                // This checks if the nextStep doesn't have a result and clears all results starting from that step
                // This is useful in a branching task in case we go back to a certain step, and then go forward again,
                // this will ensure that if another branch is taken, all next step results will be cleared.
                // Note: The first check is a workaround that checks if the task is not an OrderedTask, and so it's a
                // BranchManagedTask (which can't be accessed from ResearchStack so we can't check it directly).
                if (task !is OrderedTask && taskResult.getStepResult(nextStep.identifier) == null) {
                    clearBranchingResults()
                }

                currentStep = nextStep

                if (nextStep.isHidden) {
                    // We will do the save for this step and then go to the nextStep step
                    setHiddenStepResult(nextStep)
                    nextStep()
                } else {
                    currentStepEvent.value = StepNavigationEvent(step = nextStep)
                }
            }
        }


    }

    /**
     * Goes to previous step, and in case of editing, it pops from the stack where previous steps were stored
     * @param popUpToStep Used to keep track of the last visible step before encountering any hidden steps, as we might
     * have several consecutive hidden steps
     */
    fun previousStep(popUpToStep: Step? = currentStep) {
        if (editing) {
            val tempCurrent = stack.pop()
            if (stack.isEmpty()) {
                showCancelEditAlert()
            } else {
                currentStep = tempCurrent
                currentStepEvent.value = StepNavigationEvent(step = currentStep!!, isMovingForward = false)
            }

        } else {
            val previousStep = task.getStepBeforeStep(currentStep, taskResult)
            stepBackNavigationState.postValue(true)
            if (previousStep == null) {
                close()
            } else {
                if (previousStep.isHidden) {
                    val lastVisibleStep = if (currentStep?.isHidden!!) popUpToStep else currentStep
                    currentStep = previousStep
                    // The previous step was a hidden one so we go previousStep again
                    previousStep(lastVisibleStep)
                } else {
                    currentStep = previousStep
                    currentStepEvent.value = StepNavigationEvent(popUpToStep = popUpToStep, step = previousStep, isMovingForward = false)
                }


            }
        }
    }

    val editStep = MutableLiveData<Step>()

    fun edit(step: Step) {
        stack.empty()
        stack.push(currentStep)
        currentStep = step
        editing = true
        isSavedDialogAppeared = false
        if (clonedTaskResultInCaseOfEdit == null) {
            clonedTaskResultInCaseOfEdit = taskResult.clone() as TaskResult
        }
        editStep.postValue(step)
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

    private fun getReviewStep(): Step {
        return currentStep?.let {
            var nextStep = it
            var isReviewStep = isReviewStep(nextStep)
            while (!isReviewStep) {
                nextStep = task.getStepAfterStep(nextStep, currentTaskResult)
                isReviewStep = isReviewStep(nextStep)
            }
            return nextStep
        } ?: task.getStepAfterStep(null, currentTaskResult)
    }

    @VisibleForTesting
    fun isReviewStep(step: Step) = step::class.java.simpleName.contains("RSReviewStep", true)
    private fun isCompletionStep(step: Step) = step::class.java.simpleName.contains("RSCompletionStep", true)
    private fun isGFitStep(step: Step) = step::class.java.simpleName.contains("RSGoogleFitPermissionsStep", true)

    companion object {
        const val TAG = "TaskViewModel"
    }


    private fun goToReviewStep() {
        clonedTaskResult = null
        clonedTaskResultInCaseOfEdit?.let {
            taskResult = updateTaskResultsFrom(it)
        }
        clonedTaskResultInCaseOfEdit = null
        val nextStep = getReviewStep()
        currentStep = nextStep
        moveReviewStep.postValue(StepNavigationEvent(step = nextStep))
    }


    private fun updateTaskResultsFrom(clonedResults: TaskResult): TaskResult {
        val stepIds: MutableList<String> = mutableListOf()
        val results = TaskResult(clonedResults.identifier)

        task.steps.forEach {
            val result = clonedResults.getStepAndResult(it.identifier).second
            if (result != null) {
                results.setStepResultForStep(it, result)
                stepIds.add(it.identifier)
            }
        }

        task.resetCompletedTask(stepIds)
        return results
    }

    fun showCancelEditAlert() {
        showCancelEditDialog.postValue(checkIfAnswersAreTheSame())
    }

    fun cancelEditDismiss() {
        if (stack.empty()) {
            stack.push(currentStep)
        }
    }

    fun removeUpdatedLayout() {
        updateCancelEditInLayout.postValue(true)
        goToReviewStep()
        stack.clear()
        editing = false
    }

    fun checkForSaveDialog() {
        when {
            isSavedDialogAppeared.not() && checkIfAnswersAreTheSame() && checkIfCurrentStepIsBranchDecisionStep() -> {
                isSavedDialogAppeared = true
                showSaveEditDialog.postValue(true)
            }
            else -> {
                nextStep()
            }
        }
    }

    /**
     * Clears step results starting from the step after the current step until it reaches the end. This is ensuring
     * that if we have branches in our task, all step results that are after the branch triggering step will be removed
     */
    fun clearBranchingResults() {
        var removeStepResult = false
        for (step in task.steps) {
            if (removeStepResult) {
                taskResult.removeStepResultForStep(step)
            }
            if (step == currentStep) {
                removeStepResult = true
            }
        }
    }

    fun checkForSkipDialog(modifiedStepResult: StepResult<*>?) {
        val originalStepResult = clonedTaskResultInCaseOfEdit?.getStepResult(currentStep?.identifier)
        when {
            currentStep!!.isOptional && checkIfNewAnswerIsSkipWhilePreviousIsNot(originalStepResult, modifiedStepResult) -> {
                showSkipEditDialog.postValue(Pair(true, originalStepResult!!))
            }
            else -> {
                nextStep()
            }
        }
    }

    fun saveEditDialogDismiss() {
        isSavedDialogAppeared = false
    }

    @VisibleForTesting
    fun checkIfAnswersAreTheSame(): Boolean {
        val originalStepResult = clonedTaskResultInCaseOfEdit?.getStepResult(currentStep?.identifier)
        val modifiedStepResult = currentTaskResult.getStepResult(currentStep?.identifier)
        return originalStepResult?.equals(modifiedStepResult)?.not() ?: false
    }

    @VisibleForTesting
    fun checkIfNewAnswerIsSkipWhilePreviousIsNot(originalStepResult: StepResult<*>?,
                                                 modifiedStepResult: StepResult<*>?): Boolean {
        currentTaskResult.setStepResultForStep(currentStep!!, modifiedStepResult)
        return if (originalStepResult == null || originalStepResult.results.isEmpty()) {
            false
        } else {
            originalStepResult.allValuesAreNull()!!.not() && modifiedStepResult!!.allValuesAreNull()
        }
    }

    @VisibleForTesting
    fun checkIfCurrentStepIsBranchDecisionStep(): Boolean {
        val nextStep = task.getStepAfterStep(currentStep, currentTaskResult)
        return currentTaskResult.getStepResult(nextStep.identifier) == null
                && isReviewStep(nextStep).not()
                && isGFitStep(nextStep).not()
    }

    fun revertToOriginalStepResult(originalStepResult: StepResult<*>) {
        currentTaskResult.getStepResult(currentStep?.identifier).result = originalStepResult.result
    }
}