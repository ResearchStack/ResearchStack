package org.researchstack.backbone.ui.task

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
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

internal class TaskViewModel(val context: Application, intent: Intent) : AndroidViewModel(context) {

    var editing = false
    var currentStep: Step? = null


    var firstStep: Step

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
    val showEditDialog = MutableLiveData<Boolean>()
    val updateCancelEditInLayout = MutableLiveData<Boolean>()


    private var taskResult: TaskResult
    private var clonedTaskResult: TaskResult? = null
    private var clonedTaskResultInCaseOfCancel: TaskResult? = null
    //only used for cancel edit
    private var hasBranching = false
    private val stack = Stack<Step>()

    init {
        taskResult = intent.extras?.get(EXTRA_TASK_RESULT) as TaskResult?
                ?: TaskResult(task.identifier).apply { startDate = Date() }

        task.validateParameters()
        firstStep = task.getStepAfterStep(null, taskResult)
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
            hasBranching = clonedTaskResult?.getStepResult(nextStep.identifier) == null

            if (hasBranching) {
                Log.d(TAG, "Starting a new branch, show warning!")
            } else {
                Log.d(TAG, "Same branch")
            }

            if (hasBranching) {
                stack.push(currentStep)
                currentStep = nextStep

            } else {
                nextStep = getReviewStep()
                currentStep = nextStep
            }


            if (isReviewStep(nextStep)) {
                clonedTaskResult?.let {
                    taskResult = updateTaskResultsFrom(it)
                }
                clonedTaskResult = null
                clonedTaskResultInCaseOfCancel = null
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

    fun previousStep() {
        Log.d(TAG, "1. CURRENT STEP: $currentStep")
        if (editing) {
            val current = stack.pop()
            if (!stack.isEmpty().not()) {
                showCancelEditAlert()
            } else {
                currentStep = current
                currentStepEvent.value = StepNavigationEvent(step = currentStep!!, isMovingForward = false)
            }

        } else {
            val previousStep = task.getStepBeforeStep(currentStep, taskResult)
            if (previousStep == null) {
                close()
            } else {

                if (previousStep.isHidden) {
                    // The previous step was a hidden one so we go previousStep again
                    previousStep()
                } else {
                    currentStepEvent.value = StepNavigationEvent(popUpToStep = currentStep, step = previousStep, isMovingForward = false)
                }

                currentStep = previousStep

            }
        }
        Log.d(TAG, "2. CURRENT STEP: $currentStep")

    }

    val editStep = MutableLiveData<Step>()

    fun edit(step: Step) {
        stack.empty()
        stack.push(currentStep)
        currentStep = step
        editing = true
        if (clonedTaskResultInCaseOfCancel == null) {
            clonedTaskResultInCaseOfCancel = taskResult.clone() as TaskResult
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
           var isReviewStep =  isReviewStep(nextStep)
           while (!isReviewStep) {
               nextStep = task.getStepAfterStep(nextStep, currentTaskResult)
               isReviewStep = isReviewStep(nextStep)
           }
           return nextStep
        } ?: task.getStepAfterStep(null, currentTaskResult)
    }

    private fun isReviewStep(step: Step) = step::class.java.simpleName.contains("RSReviewStep", true)

    companion object {
        const val TAG = "TaskViewModel"
    }


    private fun goToReviewStep() {
        clonedTaskResult = null
        clonedTaskResultInCaseOfCancel?.let {
            taskResult = updateTaskResultsFrom(it)
        }
        clonedTaskResultInCaseOfCancel = null
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
        showEditDialog.postValue(true)
    }

    fun cancelEditDismiss() {
        stack.push(currentStep)
    }


    fun removeUpdatedLayout() {
        updateCancelEditInLayout.postValue(true)
        goToReviewStep()
        stack.clear()
        editing = false
    }


}