package org.researchstack.backbone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.permissions.PermissionListener;
import org.researchstack.backbone.ui.permissions.PermissionMediator;
import org.researchstack.backbone.ui.permissions.PermissionResult;
import org.researchstack.backbone.ui.step.layout.ConsentVisualStepLayout;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.step.layout.SurveyStepLayout;
import org.researchstack.backbone.ui.views.StepSwitcher;
import org.researchstack.backbone.utils.ViewUtils;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;

public class ViewTaskActivity extends PinCodeActivity implements StepCallbacks, PermissionMediator
{
    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
    public static final String EXTRA_COLOR_PRIMARY = "ViewTaskActivity.ExtraColorPrimary";
    public static final String EXTRA_COLOR_PRIMARY_DARK = "ViewTaskActivity.ExtraColorPrimaryDark";
    public static final String EXTRA_COLOR_SECONDARY = "ViewTaskActivity.ExtraColorSecondary";
    public static final String EXTRA_PRINCIPAL_TEXT_COLOR = "ViewTaskActivity.ExtraPrincipalTextColor";
    public static final String EXTRA_SECONDARY_TEXT_COLOR = "ViewTaskActivity.ExtraSecondaryTextColor";
    public static final String EXTRA_ACTION_FAILED_COLOR = "ViewTaskActivity.ExtraActionFailedColor";

    private static final int STEP_PERMISSION_REQUEST = 44;

    private StepSwitcher root;

    private Step currentStep;
    private Task task;
    private TaskResult taskResult;
    private int colorPrimary;
    private int colorPrimaryDark;
    private int colorSecondary;
    private int principalTextColor;
    private int secondaryTextColor;
    private int actionFailedColor;
    private boolean showBackArrow = true;
    private ActionBar actionBar;

    private int stepCount = 0;


    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    public static void themeIntent(Intent intent,
                                   int colorPrimary,
                                   int colorPrimaryDark,
                                   int colorSecondary,
                                   int principalTextColor,
                                   int secondaryTextColor,
                                   int actionFailedColor)
    {
        intent.putExtra(EXTRA_COLOR_PRIMARY, colorPrimary);
        intent.putExtra(EXTRA_COLOR_PRIMARY_DARK, colorPrimaryDark);
        intent.putExtra(EXTRA_COLOR_SECONDARY, colorSecondary);
        intent.putExtra(EXTRA_PRINCIPAL_TEXT_COLOR, principalTextColor);
        intent.putExtra(EXTRA_SECONDARY_TEXT_COLOR, secondaryTextColor);
        intent.putExtra(EXTRA_ACTION_FAILED_COLOR, actionFailedColor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setResult(RESULT_CANCELED);
        super.setContentView(R.layout.rsb_activity_step_switcher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try
        {
            setSupportActionBar(toolbar);
        } catch (Exception e)
        {
            //there is already an action bar
            toolbar.setVisibility(View.GONE);
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        root = findViewById(R.id.container);

        if (savedInstanceState == null) {
            task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
            colorPrimary = getIntent().getIntExtra(EXTRA_COLOR_PRIMARY, R.color.rsb_colorPrimary);
            colorPrimaryDark = getIntent().getIntExtra(EXTRA_COLOR_PRIMARY_DARK, R.color.rsb_colorPrimaryDark);
            colorSecondary = getIntent().getIntExtra(EXTRA_COLOR_SECONDARY, R.color.rsb_colorAccent);
            principalTextColor = getIntent().getIntExtra(EXTRA_PRINCIPAL_TEXT_COLOR, R.color.rsb_cell_header_grey);
            secondaryTextColor = getIntent().getIntExtra(EXTRA_SECONDARY_TEXT_COLOR, R.color.rsb_item_text_grey);
            actionFailedColor = getIntent().getIntExtra(EXTRA_ACTION_FAILED_COLOR, R.color.rsb_error);
            taskResult = (TaskResult) getIntent().getExtras().get(EXTRA_TASK_RESULT);
            if (taskResult == null)
            {
                taskResult = new TaskResult(task.getIdentifier());
            }
            taskResult.setStartDate(new Date());
        } else {
            task = (Task) savedInstanceState.getSerializable(EXTRA_TASK);
            taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
        }

        task.validateParameters();

        task.onViewChange(Task.ViewChangeType.ActivityCreate, this, currentStep);
    }

    @RequiresApi(23)
    @Override
    public void requestPermissions(String... permissions) {
        requestPermissions(permissions, STEP_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STEP_PERMISSION_REQUEST) {
            PermissionResult result = new PermissionResult(permissions, grantResults);
            List<PermissionListener> permissionListeners = ViewUtils.findViewsOf(findViewById(android.R.id.content), PermissionListener.class, true);
            for (PermissionListener listener : permissionListeners) {
                listener.onPermissionGranted(result);
            }
        }
    }

    /**
     * Returns the actual current step being shown.
     *
     * @return an instance of @Step
     */
    public Step getCurrentStep() {
        return currentStep;
    }

    protected String getCurrentTaskId()
    {
        return task.getIdentifier();
    }

    protected void showNextStep() {
        Step nextStep = task.getStepAfterStep(currentStep, taskResult);
        if (nextStep == null) {
            saveAndFinish();
        } else {
            showStep(nextStep, true);
        }
    }

    protected void showPreviousStep() {
        Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
        if (previousStep == null) {
            finish();
        } else {
            showStep(previousStep,false);
        }
    }

    private void showStep(Step step, boolean isMovingForward)
    {
        stepCount +=  isMovingForward ? 1 : -1;

        StepLayout stepLayout = getLayoutForStep(step);
        stepLayout.getLayout().setTag(R.id.rsb_step_layout_id, step.getIdentifier());
        root.show(stepLayout, isMovingForward ? StepSwitcher.SHIFT_LEFT : StepSwitcher.SHIFT_RIGHT);
        actionBar.setDisplayHomeAsUpEnabled(stepCount > 1 && showBackArrow);
        currentStep = step;

    }

    protected StepLayout getLayoutForStep(Step step) {
        // Allow the back/up arrow to be displayed by default
        showBackArrow = true;
        // Change the title on the activity
        String title = task.getTitleForStep(this, step);
        setActionBarTitle(title);
        step.setStepTheme(colorPrimary, colorPrimaryDark, colorSecondary, principalTextColor, secondaryTextColor, actionFailedColor);
        setActivityTheme(colorPrimary, colorPrimaryDark);

        // Get result from the TaskResult, can be null
        StepResult result = taskResult.getStepResult(step.getIdentifier());

        if(step instanceof FormStep)
        {
            for(QuestionStep questionStep : ((FormStep) step).getFormSteps())
            {
                questionStep.setStepTheme(step.getPrimaryColor(), step.getColorPrimaryDark(), step.getColorSecondary(),
                        step.getPrincipalTextColor(), step.getSecondaryTextColor(), step.getActionFailedColor());
            }
        }

        // Return the Class & constructor
        StepLayout stepLayout = createLayoutFromStep(step);
        if (stepLayout instanceof SurveyStepLayout) {
            ((SurveyStepLayout) stepLayout).initialize(step, result, colorPrimary, colorSecondary, principalTextColor, secondaryTextColor);
            ((SurveyStepLayout) stepLayout).isStepEmpty().observe(this, (isEmpty) -> { });
        } else if (stepLayout instanceof ConsentVisualStepLayout) {
            ((ConsentVisualStepLayout) stepLayout).initialize(step, result, colorPrimary, colorSecondary, principalTextColor, secondaryTextColor);
        } else {
            stepLayout.initialize(step, result);
        }

        stepLayout.setCallbacks(this);

        return stepLayout;
    }

    @NonNull
    private StepLayout createLayoutFromStep(Step step) {
        try {
            Class cls = step.getStepLayoutClass();
            Constructor constructor = cls.getConstructor(Context.class);
            return (StepLayout) constructor.newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void saveAndFinish() {
        taskResult.setEndDate(new Date());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onPause() {
        hideKeyboard();
        super.onPause();

        task.onViewChange(Task.ViewChangeType.ActivityPause, this, currentStep);
    }

    @Override
    protected void onResume() {
        super.onResume();
        task.onViewChange(Task.ViewChangeType.ActivityResume, this, currentStep);
    }

    @Override
    protected void onStop() {
        super.onStop();
        task.onViewChange(Task.ViewChangeType.ActivityStop, this, currentStep);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rsb_activity_view_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            notifyStepOfBackPress();
            return true;
        } else if (item.getItemId() == R.id.rsb_action_cancel) {
            new MaterialDialog.Builder(this)
                    .title(R.string.rsb_task_cancel_title)
                    .content(R.string.rsb_task_cancel_text)
                    .theme(Theme.LIGHT)
                    .positiveColor(colorPrimary)
                    .negativeColor(colorPrimary)
                    .negativeText(R.string.rsb_cancel)
                    .positiveText(R.string.rsb_task_cancel_positive)
                    .onPositive(new MaterialDialog.SingleButtonCallback()
                    {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                        {
                            finish();
                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        notifyStepOfBackPress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TASK, task);
        outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
        outState.putSerializable(EXTRA_STEP, currentStep);
    }

    private void notifyStepOfBackPress() {
        StepLayout currentStepLayout = (StepLayout) findViewById(R.id.rsb_current_step);
        currentStepLayout.isBackEventConsumed();
    }

    @Override
    public void onDataReady() {
        super.onDataReady();

        if (currentStep == null) {
            currentStep = task.getStepAfterStep(null, taskResult);
        }

        showStep(currentStep, true);
    }

    @Override
    public void onDataFailed() {
        super.onDataFailed();
        Toast.makeText(this, R.string.rsb_error_data_failed, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result) {
        onSaveStepResult(step.getIdentifier(), result);

        onExecuteStepAction(action);
    }

    protected void onSaveStepResult(String id, StepResult result) {
        taskResult.setStepResultForStepIdentifier(id, result);
    }

    protected void onExecuteStepAction(int action) {
        if (action == StepCallbacks.ACTION_NEXT) {
            showNextStep();
        } else if (action == StepCallbacks.ACTION_PREV) {
            showPreviousStep();
        } else if (action == StepCallbacks.ACTION_END) {
            saveAndFinish();
        } else if (action == StepCallbacks.ACTION_NONE) {
            // Used when onSaveInstanceState is called of a view. No action is taken.
        } else {
            throw new IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See StepCallbacks for allowable arguments");
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive() && imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private void showConfirmExitDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(
                "Are you sure you want to exit?")
                .setMessage(R.string.lorem_medium)
                .setPositiveButton("End Task", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onCancelStep() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void setActionbarVisible(final boolean setVisible) {
        actionBar.setHomeButtonEnabled(setVisible);
        actionBar.setDisplayShowHomeEnabled(setVisible);
        actionBar.setDisplayHomeAsUpEnabled(setVisible);
        showBackArrow = setVisible;
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void setActivityTheme(final int primaryColor, final int primaryColorDark) {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();

                    if (primaryColorDark == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    } else {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    }
                    window.setStatusBarColor(primaryColorDark);
                }
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(primaryColor));
                }
            }
        });
    }
}
