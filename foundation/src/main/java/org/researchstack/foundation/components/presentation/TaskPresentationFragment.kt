package org.researchstack.foundation.components.presentation

import android.content.Context
import android.os.Bundle
import android.os.ParcelUuid
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.researchstack.foundation.R
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks
import org.researchstack.foundation.components.presentation.interfaces.*
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.interfaces.ITask
import java.util.*

abstract class TaskPresentationFragment<StepType : IStep, ResultType : IResult, TaskType : ITask>() : androidx.fragment.app.Fragment(), StepCallbacks {

    companion object {
        @JvmField
        val ARGUMENT_TASK_VIEW = "TASK_VIEW"

        @JvmField
        val ARGUMENT_TASK_RUN_UUID = "TASK_RUN_UUID"
    }

    // inject
    lateinit var taskViewModelFactory: TaskPresentationViewModelFactory<StepType, ResultType>

    lateinit var taskProvider: ITaskProvider
    lateinit var stepFragmentProvider: IStepFragmentProvider

    lateinit var taskPresentationViewModel: TaskPresentationViewModel<StepType, ResultType>

    protected var _task: TaskType? = null
    lateinit var task: TaskType


    protected var _taskNavigator: ITaskNavigator<StepType, ResultType>? = null
    public val taskNavigator: ITaskNavigator<StepType, ResultType>
        get() = this._taskNavigator!!

    public var callback: TaskPresentationCallback<ResultType, TaskType>? = null

    var _currentStep: StepType? = null
    var _currentFragment: IStepFragment? = null

    val currentStep: StepType?
        get() = this._currentStep

    var _result: ResultType? = null
    val result: ResultType
        get() = this._result!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lateinit var taskId: String
        var taskRunParcelableUuid: ParcelUuid? = null

        if (savedInstanceState == null) {
            arguments?.let {
                taskId = it.getString(ARGUMENT_TASK_VIEW)!!
                taskRunParcelableUuid = it.getParcelable(ARGUMENT_TASK_RUN_UUID)
            }

        } else {
            taskId = savedInstanceState.getString(ARGUMENT_TASK_VIEW)!!
            taskRunParcelableUuid = savedInstanceState.getParcelable<ParcelUuid>(ARGUMENT_TASK_RUN_UUID)
        }

        @Suppress("UNCHECKED_CAST")
        taskPresentationViewModel = ViewModelProviders
                .of(this, taskViewModelFactory
                        .create(taskId, taskRunParcelableUuid?.uuid ?: UUID.randomUUID()))
                .get(TaskPresentationViewModel::class.java) as TaskPresentationViewModel<StepType, ResultType>

        taskPresentationViewModel.getTaskNavigatorStateLiveData()
                .observe(this, Observer<TaskPresentationViewModel.TaskNavigatorState<StepType>>
                { taskNavigatorState -> this.showStep(taskNavigatorState) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.rsf_fragment_task_presentation, container, false);

        val toolbar = view.findViewById(R.id.toolbar) as Toolbar?

        val appCompatActivity: AppCompatActivity = this.activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(toolbar)
        appCompatActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        this.initialize(savedInstanceState)

        return view
    }

    abstract fun initialize(savedInstanceState: Bundle?)

    @VisibleForTesting
    protected fun showNextStep() {
        taskPresentationViewModel.goForward()

    }

    @VisibleForTesting
    protected fun showPreviousStep() {
        taskPresentationViewModel.goBack()
    }

    private fun showStep(taskNavigatorState: TaskPresentationViewModel.TaskNavigatorState<StepType>) {
        if (taskNavigatorState.currentStep == null) {
            saveAndFinish(taskNavigatorState.navDirection == NavDirection.SHIFT_RIGHT)
            return
        }

        val stepFragment = this.getFragmentForStep(taskNavigatorState.currentStep)
        this._currentFragment = stepFragment

        val transaction = childFragmentManager.beginTransaction()
        if (taskNavigatorState.navDirection == NavDirection.SHIFT_LEFT) {
            transaction.setCustomAnimations(R.anim.rsf_slide_in_right, R.anim.rsf_slide_out_left)
        } else if (taskNavigatorState.navDirection == NavDirection.SHIFT_RIGHT) {
            transaction.setCustomAnimations(R.anim.rsf_slide_in_left, R.anim.rsf_slide_out_right)

        }
        transaction
                .replace(R.id.rsf_content_step, stepFragment.fragment)
                .commit()
        childFragmentManager.executePendingTransactions()
    }

    abstract fun getStepResult(taskResult: ResultType, stepIdentifier: String): IResult?
    abstract fun setStepResult(taskResult: ResultType, stepIdentifier: String, stepResult: IResult)

    protected fun getFragmentForStep(step: StepType): IStepFragment {

        val hostFragment = this

        // Change the title on the activity
        val title: String = {
            val title = this.taskNavigator.getTitleForStep(step)
            if (title != "") {
                title
            } else {
                this.taskNavigator.getTitleForStep(this.activity!!, step)
            }

        }()

        setActionBarTitle(title)

        // Get result from the TaskResult, can be null

        val stepResult: IResult? = this.getStepResult(this.result, step.identifier)

        val fragment = createFragmentFromStep(step)
        if (fragment == null) {
            throw RuntimeException("Cannot create fragment for step ${step.identifier}")
        }

        val stepFragment: IStepFragment = fragment.apply {
            this.initialize(step, stepResult)
            this.setCallbacks(hostFragment)
        }

        return stepFragment
    }

    private fun createFragmentFromStep(step: IStep): IStepFragment? {
        return this.stepFragmentProvider?.stepFragment(this.activity!! as Context, step)
    }

    protected abstract fun saveAndFinish(clearResult: Boolean)

    fun setActionBarTitle(title: String) {

        val appCompatActivity: AppCompatActivity = this.activity as AppCompatActivity

        val actionBar = appCompatActivity.supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(title)
        }
    }

    //JDK - 4/16/19 - as part of future work, we will be removing conformance to StepCallbacks
    //This means that onSaveStep, onCancelStep, onExecuteStepAction and showConfirmExitDialog will
    //probably all go away
    protected fun onExecuteStepAction(action: Int) {
        if (action == StepCallbacks.ACTION_NEXT) {
            showNextStep()
        } else if (action == StepCallbacks.ACTION_PREV) {
            showPreviousStep()
        } else if (action == StepCallbacks.ACTION_END) {
            showConfirmExitDialog()
        } else if (action == StepCallbacks.ACTION_NONE) {
            // Used when onSaveInstanceState is called of a view. No action is taken.
        } else {
            throw IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See StepCallbacks for allowable arguments")
        }
    }

    private fun showConfirmExitDialog() {
        val alertDialog = AlertDialog.Builder(this.activity!!).setTitle(
                "Are you sure you want to exit?")
                .setMessage(R.string.lorem_medium)
                .setPositiveButton("End Task") { dialog, which -> saveAndFinish(true) }
                .setNegativeButton("Cancel", null)
                .create()
        alertDialog.show()
    }

    public fun onBackPressed() {
        notifyStepOfBackPress()
    }

    private fun notifyStepOfBackPress() {
        this._currentFragment?.onBackPressed()
    }


    override fun onSaveStep(action: Int, step: IStep, result: IResult?) {
        result?.let { setStepResult(this.result, step.identifier, it) }
        onExecuteStepAction(action)
    }

    override fun onCancelStep() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

