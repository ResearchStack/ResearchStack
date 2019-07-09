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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.researchstack.foundation.R
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep
import org.researchstack.foundation.core.interfaces.ITask
import org.researchstack.foundation.core.interfaces.UIStep
import org.researchstack.foundation.core.models.result.StepResult
import org.researchstack.foundation.core.models.result.TaskResult
import java.util.*

abstract class TaskPresentationFragment<StepType : UIStep, ResultType : IResult, TaskType : ITask> : androidx.fragment.app.Fragment() {

    companion object {
        @JvmField
        val ARGUMENT_TASK_IDENTIFIER = "TASK_IDENTIFIER"

        @JvmField
        val ARGUMENT_TASK_RUN_UUID = "TASK_RUN_UUID"

        @JvmStatic
        @JvmOverloads
        fun createBundle(taskIdentifier: String, taskRunUUID: UUID = UUID.randomUUID()): Bundle {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_TASK_IDENTIFIER, taskIdentifier)
            bundle.putParcelable(ARGUMENT_TASK_RUN_UUID, ParcelUuid.fromString(taskRunUUID.toString()))
            return bundle
        }
    }

    interface OnPerformTaskExitListener {
        enum class Status {
            CANCELLED, FINISHED
        }

        fun onTaskExit(status: Status, taskResult: TaskResult)
    }

    fun inject(taskViewModelFactory: TaskPresentationViewModelFactory<StepType>,
               stepFragmentProvider: IStepFragmentProvider<StepType>) {
        this.taskViewModelFactory = taskViewModelFactory
        this.stepFragmentProvider = stepFragmentProvider
    }

    // inject
    lateinit var taskViewModelFactory: TaskPresentationViewModelFactory<StepType>
    // inject
    lateinit var stepFragmentProvider: IStepFragmentProvider<StepType>


    lateinit var taskPresentationViewModel: TaskPresentationViewModel<StepType>

//    lateinit var task: TaskType


    var _currentStep: StepType? = null
    var _currentStepFragment: Fragment? = null

    val currentStep: StepType?
        get() = this._currentStep


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var taskId: String
        var taskRunParcelableUuid: ParcelUuid? = null

        if (savedInstanceState == null) {
            arguments?.let {
                taskId = it.getString(ARGUMENT_TASK_IDENTIFIER)!!
                taskRunParcelableUuid = it.getParcelable(ARGUMENT_TASK_RUN_UUID)
            }

        } else {
            taskId = savedInstanceState.getString(ARGUMENT_TASK_IDENTIFIER)!!
            taskRunParcelableUuid = savedInstanceState.getParcelable<ParcelUuid>(ARGUMENT_TASK_RUN_UUID)
        }

        @Suppress("UNCHECKED_CAST")
        taskPresentationViewModel = ViewModelProviders
                .of(this, taskViewModelFactory
                        .create(taskId, taskRunParcelableUuid?.uuid ?: UUID.randomUUID()))
                .get(TaskPresentationViewModel::class.java) as TaskPresentationViewModel<StepType>

        taskPresentationViewModel.getTaskNavigatorStateLiveData()
                .observe(this, Observer<TaskPresentationViewModel.TaskNavigatorState<StepType>>
                { taskNavigatorState -> this.showStep(taskNavigatorState) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.rsf_fragment_task_presentation, container, false)
//
//        val toolbar = view.findViewById(R.id.toolbar) as Toolbar?
//
//        val appCompatActivity: AppCompatActivity = this.activity as AppCompatActivity
//        appCompatActivity.setSupportActionBar(toolbar)
//        appCompatActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        return view
    }

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
            val exitStatus: OnPerformTaskExitListener.Status =
                    if (taskNavigatorState.navDirection == NavDirection.SHIFT_RIGHT) {
                        OnPerformTaskExitListener.Status.CANCELLED
                    } else {
                        OnPerformTaskExitListener.Status.FINISHED
                    }
            checkExitListener(exitStatus)
            return
        }

        val stepFragment = this.getFragmentForStep(taskNavigatorState.currentStep)
        this._currentStepFragment = stepFragment

        val transaction = childFragmentManager.beginTransaction()
        if (taskNavigatorState.navDirection == NavDirection.SHIFT_LEFT) {
            transaction.setCustomAnimations(R.anim.rsf_slide_in_right, R.anim.rsf_slide_out_left)
        } else if (taskNavigatorState.navDirection == NavDirection.SHIFT_RIGHT) {
            transaction.setCustomAnimations(R.anim.rsf_slide_in_left, R.anim.rsf_slide_out_right)

        }
        transaction
                .replace(R.id.rsf_content_step, stepFragment)
                .commit()
        childFragmentManager.executePendingTransactions()
    }

    abstract fun getStepResult(taskResult: TaskResult, stepIdentifier: String): IResult?
    abstract fun setStepResult(taskResult: TaskResult, stepIdentifier: String, stepResult: IResult)

    protected fun getFragmentForStep(step: StepType): Fragment {
        // Get result from the TaskResult, can be null

        val fragment = createFragmentFromStep(step)
                ?: throw RuntimeException("Cannot create fragment for step ${step.identifier}")
        // Change the title on the activity
        setActionBarTitle(step.stepTitle)

        return fragment
    }

    private fun createFragmentFromStep(step: StepType): Fragment? {
        return this.stepFragmentProvider.stepFragment(step, StepPresentationViewModelFactory(taskPresentationViewModel))
    }

    fun checkExitListener(finishStatus: OnPerformTaskExitListener.Status) {
        var onPerformTaskExitListener: OnPerformTaskExitListener? = null
        if (parentFragment is OnPerformTaskExitListener) {
            onPerformTaskExitListener = parentFragment as OnPerformTaskExitListener
        }
        if (onPerformTaskExitListener == null && activity is OnPerformTaskExitListener) {
            onPerformTaskExitListener = activity as OnPerformTaskExitListener
        }
        onPerformTaskExitListener?.onTaskExit(finishStatus,
                taskPresentationViewModel.getTaskNavigatorStateLiveData().value!!.taskResult)
    }

    fun setActionBarTitle(title: String?) {

        val appCompatActivity: AppCompatActivity = this.activity as AppCompatActivity

        val actionBar = appCompatActivity.supportActionBar
        if (actionBar != null) {
            actionBar.run { setTitle(title) }
        }
    }

    fun showConfirmExitDialog() {
        val alertDialog = AlertDialog.Builder(this.activity!!).setTitle(
                "Are you sure you want to exit?")
                .setMessage(R.string.lorem_medium)
                .setPositiveButton("End Task") { dialog, which -> checkExitListener(OnPerformTaskExitListener.Status.CANCELLED) }
                .setNegativeButton("Cancel", null)
                .create()
        alertDialog.show()
    }
}

