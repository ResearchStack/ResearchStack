package org.researchstack.backbone.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.rsb_activity_step_switcher.*
import kotlinx.coroutines.*
import org.researchstack.backbone.R
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.task.Task
import org.researchstack.backbone.ui.callbacks.StepCallbacks
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.backbone.ui.views.StepSwitcher
import org.researchstack.backbone.utils.StepLayoutHelper
import java.util.*
import kotlin.coroutines.CoroutineContext


open abstract class ActiveTaskFragment : Fragment(), StepCallbacks, ConfirmExitListener,
    BackButtonObserver, CoroutineScope {

    companion object {
    }

    private lateinit var uiJob: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + uiJob

    private var currentStepLayout: StepLayout? = null
    var task: org.researchstack.backbone.task.Task? = null

    private var taskResult: TaskResult? = null

    private var currentStep: Step? = null

    private var currentStepAction: Int = 0

    private lateinit var eventId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiJob = Job()
        return inflater.inflate(R.layout.rsb_activity_step_switcher, container, false)
    }

    abstract fun getActiveTask(): Task

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        launch {
            withContext(Dispatchers.IO) {
                task = getActiveTask()
            }

            if (task == null) {
                return@launch
            }

            (activity as AppCompatActivity).supportActionBar?.title = task?.title

            currentStep = task?.getStepAfterStep(null, taskResult)

            showStep(currentStep)

        }
    }

    override fun onBackButtonPressed(): Boolean {
        showPreviousStep()
        return true
    }

    private fun showNextStep() {
        hideKeyboard()
        val nextStep = task?.getStepAfterStep(currentStep, taskResult)

        if (nextStep == null) {
            saveAndFinish(true)
        } else {
            showStep(nextStep)
        }
    }

    private fun hideKeyboard() {
        val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = activity!!.currentFocus
        focus?.windowToken?.let {
            imm.hideSoftInputFromWindow(focus.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiJob.cancel()
        hideKeyboard()
        //reset the livedata value
    }

    private fun showPreviousStep() {
        val previousStep = task?.getStepBeforeStep(currentStep, taskResult)
        if (previousStep == null) {
            showConfirmExitDialog()
        } else {
            showStep(previousStep)
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun saveAndFinish(completed: Boolean) {
        taskResult!!.endDate = Date()
        /*      if (completed) {
                  taskResult!!.completed = taskResult!!.checkIfAllStepsAnswered()
              }

              when (eventType) {
                  ViewTaskType.CalendarEvent, ViewTaskType.PromisTask -> {
                      task?.processTaskResult(null, taskResult)
                      taskSharedViewModel.convertTaskResultToResult(taskResult!!, eventId, currentStep?.identifier!!)
                  }
                  ViewTaskType.ZeroDate -> {
                      taskSharedViewModel.markZeroDateCompleted()
                      taskSharedViewModel.processZeroDateResult(taskResult!!)
                  }
              }.let { }*/

        //activity?.findNavController(R.id.navHostFragment)?.navigateUp()
    }

    override fun discardResultsAndFinish() {
        if (taskResult != null) {  // taskResult can be null in a bad state
            taskResult?.results?.clear()
        }
        taskResult = null
        //activity?.findNavController(R.id.navHostFragment)?.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> {
                showPreviousStep()
            }
            /*  R.id.cancel_action -> {
                  showConfirmExitDialog()
              }*/
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showConfirmExitDialog() {
        val isTaskResultEmpty = taskResult?.results?.isEmpty() ?: true
        //  val disableSaveResult = eventType == ViewTaskType.PromisTask
        // val newFragment = ConfirmExitDialog.newInstance(isTaskResultEmpty, disableSaveResult)
        // newFragment.setTargetFragment(this, 0)
        //newFragment.show(fragmentManager, "ConfirmExit")
    }

    private fun showStep(step: Step?, alwaysReplaceView: Boolean = false) {
        val currentStepPosition = task?.getProgressOfCurrentStep(currentStep, taskResult)?.current
        val newStepPosition = task?.getProgressOfCurrentStep(step, taskResult)?.current

        val stepLayout = getLayoutForStep(step)

        if (stepLayout == null) {
            return
        }

        stepLayout.layout.setTag(org.researchstack.backbone.R.id.rsb_step_layout_id, step!!.identifier)
        container?.show(
            stepLayout,
            if (newStepPosition!! >= currentStepPosition!!)
                StepSwitcher.SHIFT_LEFT
            else
                StepSwitcher.SHIFT_RIGHT, alwaysReplaceView
        )
        currentStep = step
        // taskResult?.currentStepIdentifier = step.identifier
        currentStepLayout = stepLayout
    }

    private fun refreshCurrentStep() {
        showStep(currentStep, true)
    }

    private fun getLayoutForStep(step: Step?): StepLayout? {
        // Change the title on the activity

        if (taskResult == null) {
            /* Timber.e(
                 "Trying to add a step layout with a null task result"
             )*/
            return null
        }

        // Get result from the TaskResult, can be null
        val result = taskResult!!.getStepResult(step!!.identifier)

        // Return the Class & constructor
        val stepLayout = StepLayoutHelper.createLayoutFromStep(step, context!!)
        setupStepLayoutBeforeInitializeIsCalled(stepLayout)
        stepLayout.initialize(step, result)

        // Some step layouts need to know about the task result

        return stepLayout
    }

    private fun setupStepLayoutBeforeInitializeIsCalled(stepLayout: StepLayout) {
        // can be implemented by sub-classes to set up the step layout before it's initialized
        stepLayout.setCallbacks(this)

    }


    override fun onSaveStep(action: Int, step: Step?, result: StepResult<*>?) {
        onSaveStepResult(step!!.identifier, result)
        onExecuteStepAction(action)
    }

    private fun onSaveStepResult(id: String, result: StepResult<*>?) {
        if (taskResult == null) {
            //Timber.e("In bad state, " + "task result should never be null, skipping onSaveStepResult")
            return
        }
        if (result != null) {
            taskResult!!.setStepResultForStepIdentifier(id, result)
        } else if (taskResult!!.results != null) {
            // result is null, make sure that is reflected in the results
            taskResult!!.results.remove(id)
        }
    }

    private fun onExecuteStepAction(action: Int) {
        currentStepAction = action
        when (action) {
            StepCallbacks.ACTION_NEXT -> showNextStep()
            StepCallbacks.ACTION_PREV -> showPreviousStep()
            StepCallbacks.ACTION_END -> showConfirmExitDialog()
            StepCallbacks.ACTION_NONE -> {
                // Used when onSaveInstanceState is called of a view. No action is taken.
            }
            StepCallbacks.ACTION_REFRESH -> refreshCurrentStep()
            else -> throw IllegalArgumentException(
                "Action with value " + action + " is invalid. " +
                        "See StepCallbacks for allowable arguments"
            )
        }
    }

    override fun onCancelStep() {
    }

}

