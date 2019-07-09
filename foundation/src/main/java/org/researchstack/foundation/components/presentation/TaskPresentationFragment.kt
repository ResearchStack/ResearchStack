package org.researchstack.foundation.components.presentation

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
import org.researchstack.foundation.core.interfaces.ITask
import org.researchstack.foundation.core.interfaces.UIStep
import org.researchstack.foundation.core.models.result.TaskResult
import java.util.*

/**
 * Base Fragment that presents a Task for the user to complete.
 */
abstract class TaskPresentationFragment<StepType : UIStep, ResultType : IResult, TaskType : ITask>
    : androidx.fragment.app.Fragment() {

    companion object {
        @JvmField
        val ARGUMENT_TASK_IDENTIFIER = "TASK_IDENTIFIER"

        @JvmField
        val ARGUMENT_TASK_RUN_UUID = "TASK_RUN_UUID"

        /**
         * Creates the Bundle needed to configure TaskPresentationFragment. Pass in a taskRunUUID to
         * continue a previous (unfinished) task run.
         */
        @JvmStatic
        @JvmOverloads
        fun createBundle(taskIdentifier: String, taskRunUUID: UUID = UUID.randomUUID()): Bundle {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_TASK_IDENTIFIER, taskIdentifier)
            bundle.putParcelable(ARGUMENT_TASK_RUN_UUID, ParcelUuid.fromString(taskRunUUID.toString()))
            return bundle
        }
    }

    interface OnTaskExitListener {
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
    private lateinit var taskViewModelFactory: TaskPresentationViewModelFactory<StepType>
    // inject
    private lateinit var stepFragmentProvider: IStepFragmentProvider<StepType>


    lateinit var taskPresentationViewModel: TaskPresentationViewModel<StepType>

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
        return inflater.inflate(R.layout.rsf_fragment_task_presentation, container, false)
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
            val exitStatus: OnTaskExitListener.Status =
                    if (taskNavigatorState.navDirection == NavDirection.SHIFT_RIGHT) {
                        OnTaskExitListener.Status.CANCELLED
                    } else {
                        OnTaskExitListener.Status.FINISHED
                    }
            checkExitListener(exitStatus)
            return
        }

        val stepFragment = this.getFragmentForStep(taskNavigatorState.currentStep)

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

    protected fun getFragmentForStep(step: StepType): Fragment {
        // Get result from the TaskResult, can be null

        val fragment = stepFragmentProvider.stepFragment(step, StepPresentationViewModelFactory(taskPresentationViewModel))
                ?: throw RuntimeException("Cannot create fragment for step ${step.identifier}")
        // Change the title on the activity
        setActionBarTitle(step.stepTitle)

        return fragment
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
                .setPositiveButton("End Task") { dialog, which -> checkExitListener(OnTaskExitListener.Status.CANCELLED) }
                .setNegativeButton("Cancel", null)
                .create()
        alertDialog.show()
    }


    fun checkExitListener(finishStatus: OnTaskExitListener.Status) {
        var onTaskExitListener: OnTaskExitListener? = null
        if (parentFragment is OnTaskExitListener) {
            onTaskExitListener = parentFragment as OnTaskExitListener
        }
        if (onTaskExitListener == null && activity is OnTaskExitListener) {
            onTaskExitListener = activity as OnTaskExitListener
        }
        onTaskExitListener?.onTaskExit(finishStatus,
                taskPresentationViewModel.getTaskNavigatorStateLiveData().value!!.taskResult)
    }
}

