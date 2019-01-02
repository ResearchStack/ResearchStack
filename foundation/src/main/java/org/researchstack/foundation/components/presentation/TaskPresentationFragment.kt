package org.researchstack.foundation.components.presentation

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.researchstack.foundation.R
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks
import org.researchstack.foundation.components.presentation.interfaces.IStepFragment
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider
import org.researchstack.foundation.components.presentation.interfaces.ITaskNavigator
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.interfaces.ITask
import java.lang.RuntimeException
import java.util.*

abstract class TaskPresentationFragment<StepType: IStep, ResultType: IResult, TaskType: ITask>(): Fragment(), StepCallbacks {

    public var taskProvider: ITaskProvider? = null
    public var stepFragmentProvider: IStepFragmentProvider? = null

    protected var _task: TaskType? = null
    public val task: TaskType
        get() = this._task!!

    protected var _taskNavigator: ITaskNavigator<StepType, ResultType>? = null
    public val taskNavigator: ITaskNavigator<StepType, ResultType>
        get() = this._taskNavigator!!

    public var callback: TaskPresentationCallback<ResultType, TaskType>? = null

    var _currentStep: StepType? = null
    var _currentFragment: IStepFragment? = null

    val currentStep: StepType?
        get() = this._currentStep


//    var _task: ITask? = null
//    val task: ITask
//        get() = this._task!!

    var _result: ResultType? = null
    val result: ResultType
        get() = this._result!!

//    var taskPresentaterDelegate: ITaskPresenterDelegate? = null

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

    override fun onResume() {

        val currentStep = this.currentStep

        if (currentStep == null) {
            this.taskNavigator.getStepAfterStep(null, this.result)?.let { firstStep ->
                showStep(firstStep)
            }
        }
        else {
            showStep(currentStep)
        }

        super.onResume()
    }

    protected fun showNextStep() {
        val nextStep = this.taskNavigator.getStepAfterStep(this.currentStep, this.result)
        if (nextStep == null) {
            this.saveAndFinish(false)
        } else {
            showStep(nextStep)
        }
    }

    protected fun showPreviousStep() {
        val previousStep = this.taskNavigator.getStepBeforeStep(this.currentStep, this.result)
        if (previousStep == null) {
            saveAndFinish(true)
        } else {
            showStep(previousStep)
        }
    }

    private fun showStep(step: StepType) {
        val currentStepPosition = this.currentStep?.let {
            this.taskNavigator.getProgressOfCurrentStep(it, this.result).current
        }

        val newStepPosition = this.taskNavigator.getProgressOfCurrentStep(step, this.result).current

        val stepFragment = this.getFragmentForStep(step)
        this._currentFragment = stepFragment

        val transaction = childFragmentManager.beginTransaction()

        if (currentStepPosition != null) {
            if (newStepPosition > currentStepPosition) {
                transaction.setCustomAnimations(R.anim.rsf_slide_in_right, R.anim.rsf_slide_out_left)
            }
            else {
                transaction.setCustomAnimations(R.anim.rsf_slide_in_left, R.anim.rsf_slide_out_right)
            }

            transaction
                    .replace(R.id.rsf_content_step, stepFragment.fragment)
                    .commit()
        }
        else {
            transaction
                    .add(R.id.rsf_content_step, stepFragment.fragment)
                    .commit()
        }

        childFragmentManager.executePendingTransactions()

        Log.d("DEBUG", ""+childFragmentManager.fragments.size)

        this._currentStep = step
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
            }
            else {
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