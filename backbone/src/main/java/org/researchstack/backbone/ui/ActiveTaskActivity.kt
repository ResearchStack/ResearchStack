package org.researchstack.backbone.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import org.researchstack.backbone.PermissionRequestManager
import org.researchstack.backbone.R
import org.researchstack.backbone.ServiceLocator
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.step.active.ActiveStep
import org.researchstack.backbone.step.active.ActiveTaskAndResultListener
import org.researchstack.backbone.step.active.CountdownStep
import org.researchstack.backbone.task.Task
import org.researchstack.backbone.ui.callbacks.ActivityCallback
import org.researchstack.backbone.ui.callbacks.StepCallbacks
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.backbone.ui.step.layout.StepPermissionRequest
import org.researchstack.backbone.utils.LogExt
import java.util.*

/**
 * *
 * The ActiveTaskActivity is responsible for displaying any task with an ActiveStepLayout
 * It will manage the DataLogger files that are created, make sure none of the dirty ones leak,
 * and make sure that they are correctly bundled and uploaded at the end
 */

open class ActiveTaskActivity : ViewTaskActivity(), ActivityCallback, ActiveTaskAndResultListener {

    protected var isBackButtonEnabled: Boolean = false
    private var isPaused = false

    private val activeTaskResultHandler = ServiceLocator.activeTaskResultHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    public override fun onResume() {
        super.onResume()
        isPaused = false
    }

    public override fun onPause() {
        super.onPause()
        this.isPaused = true
        if (currentStepLayout != null && currentStepLayout is ActiveStepLayout) {
            (currentStepLayout as ActiveStepLayout).pauseActiveStepLayout()
        }
    }

    override fun discardResultsAndFinish() {
        if (currentStepLayout != null && currentStepLayout is ActiveStepLayout) {
            (currentStepLayout as ActiveStepLayout).pauseActiveStepLayout()
            // Pause may cause the currentStepLayout to change, so check again
            if (currentStepLayout != null && currentStepLayout is ActiveStepLayout) {
                (currentStepLayout as ActiveStepLayout).forceStop()
            }
        }
        currentStepLayout = null
        // super.discardResultsAndFinish();
    }

    public override fun showStep(step: Step, alwaysReplaceView: Boolean) {

        LogExt.d(
            ActiveTaskActivity::class.java,
            "showStep(" + step.identifier + ", " + alwaysReplaceView + ")"
        )

        // compute back button status while currentStep is actually the previousStep at this point
        isBackButtonEnabled =
            (step !is ActiveStep || step is CountdownStep) && currentStep !is ActiveStep // currentStep is one previously showing at this point
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
        }

        if (isStepAndLayoutStillValid(step)) {
            // The step was not killed, it is probably running with the RecorderService
            // Instead of re-creating it, just signal to it that it should resume
            (currentStepLayout as ActiveStepLayout).resumeActiveStepLayout()
        } else {
            super.showStep(step, alwaysReplaceView)
        }

        // Active steps lock screen on and orientation so that the view is not unnecessarily
        // destroyed and recreated while the data logger is recording
        if (step is ActiveStep) {
            lockScreenOn()
            lockOrientation()
        } else {
            unlockScreenOn()
            unlockOrientation()
        }
        LogExt.d(
            ActiveTaskActivity::class.java,
            "showStep(" + step.identifier + ", " + alwaysReplaceView + ")"
        )


    }

    override fun setupStepLayoutBeforeInitializeIsCalled(stepLayout: StepLayout) {
        super.setupStepLayoutBeforeInitializeIsCalled(stepLayout)
        if (stepLayout is ActiveStepLayout) {
            stepLayout.setTaskAndResultListener(this)
        }
    }

    public override fun notifyStepOfBackPress() {
        // intercept and block any back buttons
        if (isBackButtonEnabled) {
            //super.notifyStepOfBackPress();
        }
    }

    private fun isStepAndLayoutStillValid(step: Step): Boolean {
        return step is ActiveStep && step == currentStep &&
                currentStepLayout != null && currentStepLayout is ActiveStepLayout
    }

    override fun saveAndFinish() {

        // just in case we were locking these
        unlockScreenOn()
        unlockOrientation()

        taskResult.taskDetails[ACTIVITY_TASK_RESULT_KEY] = true
        taskResult.endDate = Date()

        // Loop through and find all the FileResult files

        // Since we are now about to try and upload the files to the server, let's update their status
        // These files will now stick around until we have successfully uploaded them
        activeTaskResultHandler?.handleActiveTaskResult(taskResult)
        super.saveAndFinish()
    }

    /**
     * Active Steps lock screen to on so it can avoid any interruptions during data logging
     */
    private fun lockScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun unlockScreenOn() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * Active Steps lock orientation so it can avoid any interruptions during data logging
     */
    protected fun lockOrientation() {
        val orientation: Int
        val rotation = (getSystemService(
            Context.WINDOW_SERVICE
        ) as WindowManager).defaultDisplay.rotation
        when (rotation) {
            Surface.ROTATION_0 -> orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Surface.ROTATION_90 -> orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Surface.ROTATION_180 -> orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            Surface.ROTATION_270 -> orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            else -> orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        requestedOrientation = orientation
    }

    protected fun unlockOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onExecuteStepAction(action: Int) {
        // In this case, we cannot complete the Active Task, since one of the ActiveSteps
        // Requested we end the task, because it couldn't complete for some reason
        if (action == StepCallbacks.ACTION_END && currentStep is ActiveStep) {
            discardResultsAndFinish()
        }

        super.onExecuteStepAction(action)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermission(id: String) {
        if (PermissionRequestManager.getInstance().isNonSystemPermission(id)) {
            PermissionRequestManager.getInstance().onRequestNonSystemPermission(this, id)
        } else {
            requestPermissions(arrayOf(id), PermissionRequestManager.PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionRequestManager.PERMISSION_REQUEST_CODE) {
            updateStepLayoutForPermission()
        }
    }

    protected fun updateStepLayoutForPermission() {
        val stepLayout = findViewById<View>(R.id.rsb_current_step) as StepLayout
        if (stepLayout is StepPermissionRequest) {
            (stepLayout as StepPermissionRequest).onUpdateForPermissionResult()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val action = intent.action
            val data = intent.dataString
            Log.d("Mycap", "New Intent data ------ $data")
            Log.d("Mycap", "New Intent action ------- $action")
        }
    }

    override fun startConsentTask() {
        // deprecated
    }

    // Only called for ActiveStepLayouts
    override fun activeTaskActivityResult(): TaskResult {
        return taskResult
    }

    // Only called for ActiveStepLayouts
    override fun activeTaskActivityGetTask(): Task {
        return task
    }

    companion object {

        const val ACTIVITY_TASK_RESULT_KEY = "ACTIVITY_TASK_RESULT_KEY"

        fun newIntent(context: Context, task: Task): Intent {
            val intent = Intent(context, ActiveTaskActivity::class.java)
            intent.putExtra(ViewTaskActivity.EXTRA_TASK, task)
            return intent
        }
    }
}
