package org.researchstack.backbone.ui.task

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.researchstack.backbone.BuildConfig
import org.researchstack.backbone.R
import org.researchstack.backbone.task.Task
import org.researchstack.backbone.ui.permissions.PermissionListener
import org.researchstack.backbone.ui.permissions.PermissionMediator
import org.researchstack.backbone.ui.permissions.PermissionResult
import org.researchstack.backbone.ui.step.fragments.BaseStepFragment
import org.researchstack.backbone.ui.step.layout.StepLayout
import org.researchstack.backbone.ui.step.layout.SurveyStepLayout
import org.researchstack.backbone.utils.LocalizationUtils
import org.researchstack.backbone.utils.ViewUtils

@Deprecated("Deprecated as part of the new handling for the branching logic",
        ReplaceWith("com.medable.axon.ui.taskrunner.NRSTaskActivity"))
open class TaskActivity : AppCompatActivity(), PermissionMediator {

    private val viewModel: TaskViewModel by viewModel { parametersOf(intent) }
    private val navController by lazy { Navigation.findNavController(this, R.id.nav_host_fragment) }
    private var currentStepLayout: StepLayout? = null
    private var stepPermissionListener: PermissionListener? = null
    private var actionBarCancelMenuItem: MenuItem? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocalizationUtils.wrapLocaleContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.USE_SECURE_FLAG) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.rsb_activity_task)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        observe(viewModel.currentStepEvent) { showStep(it) }
        observe(viewModel.taskCompleted) { close(it) }
        observe(viewModel.moveReviewStep) {
            navController.navigate(it.step.destinationId, null,
                    NavOptions.Builder().setPopUpTo(
                            viewModel.firstStep.destinationId,
                            true
                    ).build())

        }

        observe(viewModel.showCancelEditDialog) {
            if (it) {
                showAlertDialog(R.string.rsb_edit_step_alert_cancel_title,
                        R.string.rsb_edit_step_alert_cancel_content,
                        R.string.rsb_edit_step_alert_cancel_discard,
                        R.string.rsb_edit_step_alert_cancel_positive,
                        {
                            it.dismiss()
                            viewModel.cancelEditDismiss()
                        },
                        { viewModel.removeUpdatedLayout() })
            } else {
                viewModel.removeUpdatedLayout()
            }
        }

        observe(viewModel.editStep) {
            navController.navigate(it.destinationId)
            supportActionBar?.title = ""
        }

        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
                Log.d("TaskActivity", "current fragment ${destination.label}")
            }
        })

        observe(viewModel.hideMenuItemCancel) { inEditMode ->
            actionBarCancelMenuItem?.let {
                it.isVisible = !inEditMode
            }
        }

        observe(viewModel.showSaveEditDialog) {
            showAlertDialog(
                    R.string.rsb_edit_step_alert_step_save_title,
                    R.string.rsb_edit_step_alert_step_save_content,
                    R.string.rsb_edit_step_alert_step_save_discard,
                    R.string.rsb_edit_step_alert_step_save_positive,
                    {
                        it.dismiss()
                        viewModel.saveEditDialogDismiss()
                    }, {
                viewModel.clearBranchingResults()
                viewModel.nextStep()
            })
        }


        observe(viewModel.showSkipEditDialog) {
            if (it.first) {
                showAlertDialog(
                        R.string.rsb_edit_step_alert_step_skip_title,
                        R.string.rsb_edit_step_alert_step_skip_content,
                        R.string.rsb_edit_step_alert_step_skip_discard,
                        R.string.rsb_edit_step_alert_step_skip_positive,
                        { dialog ->
                            dialog.dismiss()
                            viewModel.revertToOriginalStepResult(it.second)
                        }, { viewModel.nextStep() })
            }
        }


    }

    override fun onPause() {
        hideKeyboard()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.showCurrentStep()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rsb_activity_view_task_menu, menu)
        actionBarCancelMenuItem = menu.findItem(R.id.rsb_action_cancel)
        actionBarCancelMenuItem?.title = LocalizationUtils.getLocalizedString(this, R.string.rsb_cancel)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.rsb_action_cancel) {
            showAlertDialog(R.string.rsb_task_cancel_title,
                    R.string.rsb_task_cancel_text,
                    R.string.rsb_cancel,
                    R.string.rsb_task_cancel_positive, {
                it.dismiss()
            }, {
                finish()
            })
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        viewModel.previousStep()
    }

    override fun onSupportNavigateUp(): Boolean {
        viewModel.previousStep()
        return false
    }

    override fun requestPermissions(permissionListener: PermissionListener, vararg permissions:
    String?) {
        saveCurrentStepResult()
        stepPermissionListener = permissionListener
        requestPermissions(permissions, STEP_PERMISSION_LISTENER_REQUEST)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun requestPermissions(vararg permissions: String?) {
        saveCurrentStepResult()
        requestPermissions(permissions, STEP_PERMISSION_REQUEST)
    }

    private fun saveCurrentStepResult() {
        val fragment = getCurrentFragment()
        if (fragment is BaseStepFragment) {
            fragment.saveCurrentStepResult()
        }
    }

    // TODO: this should be handled by each fragment/step/type that needs any sort of permission
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STEP_PERMISSION_REQUEST) {
            // Save the fact that we requested this permission
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            permissions.forEach { preferences.edit().putBoolean(it, true).apply() }

            val result = PermissionResult(permissions, grantResults)
            val permissionListeners = ViewUtils.findViewsOf(findViewById(android.R.id.content), PermissionListener::class.java, true)

            for (listener in permissionListeners) {
                listener.onPermissionGranted(result)
            }

            // This was designed so the step's layout is some form of View/ViewGroup that implements the PermissionListener interface.
            // As it turns out, not all steps are created equal, and not all the implementations follow this structure.
            // RSLocationPermission doesn't extend any View/ViewGroup; it acts more like a custom View that inflates its own layout.
            // For this reason, we cannot simply search the view Hierarchy and obtain the Layout because it will not implement
            // the contract; we have to check if the current Layout reference (saved when created) does.
            if (currentStepLayout !is SurveyStepLayout) {
                return
            }

            val stepBody = (currentStepLayout as SurveyStepLayout).stepBody
            if (stepBody is PermissionListener) {
                (stepBody as PermissionListener).onPermissionGranted(result)
            }
        } else if (requestCode == STEP_PERMISSION_LISTENER_REQUEST) {
            val result = PermissionResult(permissions, grantResults)
            stepPermissionListener?.onPermissionGranted(result)
            stepPermissionListener = null
        }
    }

    override fun checkIfShouldShowRequestPermissionRationale(permission: String): Boolean {
        // ShouldShowRequestPermissionRationale() will return false in these cases:
        // * You've never asked for the permission before
        // * The user has checked the 'never again' checkbox
        // * The permission has been disabled by policy (usually enterprise)
        // Therefore a flag must be stored once we requested it.
        // Source: https://stackoverflow.com/questions/33224432/android-m-anyway-to-know-if-a-user-has-chosen-never-to-show-the-grant-permissi?rq=1
        // Note: ianhanniballake is a Google employee working on Android (September 2019)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val wasRequestedInThePast = preferences.getBoolean(permission, false)

        // If the user requested this permission in the past, we can rely on the rationale flag.
        return if (!wasRequestedInThePast) {
            // the user never requested this permission (or we don't have records of it).
            true
        } else Build.VERSION.SDK_INT < Build.VERSION_CODES.M || shouldShowRequestPermissionRationale(permission)
    }

    private fun showStep(navigationEvent: StepNavigationEvent) {
        navigationEvent.popUpToStep?.let {
            navController.navigate(navigationEvent.step.destinationId, null,
                    NavOptions.Builder().setPopUpTo(
                            it.destinationId,
                            true
                    ).build())
        }


        if (navigationEvent.popUpToStep == null) {
            navController.navigate(navigationEvent.step.destinationId)
        }
        setActivityTheme(viewModel.colorPrimary, viewModel.colorPrimaryDark)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && imm.isAcceptingText) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

    protected open fun close(completed: Boolean) {

        if (completed) {
            val result = Intent().apply {
                putExtra(EXTRA_TASK_RESULT, viewModel.currentTaskResult)
            }

            setResult(Activity.RESULT_OK, result)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }

        finish()
    }

    private fun setActivityTheme(primaryColor: Int, primaryColorDark: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (primaryColorDark == Color.BLACK && window.navigationBarColor == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }

            window.statusBarColor = primaryColorDark
        }

        supportActionBar?.setBackgroundDrawable(ColorDrawable(primaryColor))
    }

    private fun <T> observe(liveData: LiveData<T?>, lambda: (T) -> Unit) {
        liveData.observe(this, Observer { if (it != null) lambda(it) })
    }

    fun getCurrentFragment(): Fragment? {
        val navHostFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment);
        val fragments = navHostFragment?.childFragmentManager?.fragments
        return if (fragments != null && fragments.size > 0) {
            fragments[fragments.size - 1]
        } else null
    }

    companion object {
        const val EXTRA_TASK_RESULT = "TaskActivity.ExtraTaskResult"
        const val EXTRA_TASK = "TaskActivity.ExtraTask"
        const val EXTRA_STEP = "TaskActivity.ExtraStep"
        const val EXTRA_COLOR_PRIMARY = "TaskActivity.ExtraColorPrimary"
        const val EXTRA_COLOR_PRIMARY_DARK = "TaskActivity.ExtraColorPrimaryDark"
        const val EXTRA_COLOR_SECONDARY = "TaskActivity.ExtraColorSecondary"
        const val EXTRA_PRINCIPAL_TEXT_COLOR = "TaskActivity.ExtraPrincipalTextColor"
        const val EXTRA_SECONDARY_TEXT_COLOR = "TaskActivity.ExtraSecondaryTextColor"
        const val EXTRA_ACTION_FAILED_COLOR = "TaskActivity.ExtraActionFailedColor"

        private const val STEP_PERMISSION_REQUEST = 44
        private const val STEP_PERMISSION_LISTENER_REQUEST = 45

        fun newIntent(context: Context, task: Task): Intent {
            return Intent(context, TaskActivity::class.java).apply {
                putExtra(EXTRA_TASK, task)
            }
        }

        fun themeIntent(
                intent: Intent,
                colorPrimary: Int,
                colorPrimaryDark: Int,
                colorSecondary: Int,
                principalTextColor: Int,
                secondaryTextColor: Int,
                actionFailedColor: Int
        ) {
            with(intent) {
                putExtra(EXTRA_COLOR_PRIMARY, colorPrimary)
                putExtra(EXTRA_COLOR_PRIMARY_DARK, colorPrimaryDark)
                putExtra(EXTRA_COLOR_SECONDARY, colorSecondary)
                putExtra(EXTRA_PRINCIPAL_TEXT_COLOR, principalTextColor)
                putExtra(EXTRA_SECONDARY_TEXT_COLOR, secondaryTextColor)
                putExtra(EXTRA_ACTION_FAILED_COLOR, actionFailedColor)
            }
        }
    }


    private fun showAlertDialog(title: Int, content: Int, negativeText: Int, positiveText: Int,
                                onNegative: (dialog: MaterialDialog) -> (Unit),
                                onPositive: () -> (Unit)) {
        MaterialDialog.Builder(this)
                .cancelable(false)
                .title(LocalizationUtils.getLocalizedString(this, title))
                .content(LocalizationUtils.getLocalizedString(this, content))
                .theme(Theme.LIGHT)
                .positiveColor(viewModel.colorPrimary)
                .negativeColor(viewModel.colorPrimary)
                .negativeText(LocalizationUtils.getLocalizedString(this, negativeText))
                .positiveText(LocalizationUtils.getLocalizedString(this, positiveText))
                .onPositive { _, _ -> onPositive() }
                .onNegative { dialog, _ -> onNegative(dialog) }
                .show()
    }

    protected fun getCurrentTaskId(): String? = viewModel.task.identifier
}