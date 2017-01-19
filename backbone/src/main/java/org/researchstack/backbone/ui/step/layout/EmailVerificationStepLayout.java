package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepResultHelper;
import org.researchstack.backbone.utils.ThemeUtils;

/**
 * Created by TheMDP on 1/18/17.
 */

public class EmailVerificationStepLayout extends FixedSubmitBarLayout implements StepLayout {

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    protected StepCallbacks callbacks;

    protected EmailVerificationStep emailStep;

    protected TaskResult taskResult;
    protected StepResult<StepResult> stepResult;

    public EmailVerificationStepLayout(Context context) {
        super(context);
    }

    public EmailVerificationStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmailVerificationStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public EmailVerificationStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsb_layout_email_verification;
    }

    @Override
    public void initialize(Step step, StepResult result, TaskResult taskResult) {
        validateStepAndResult(step);

        this.taskResult = taskResult;
        this.stepResult = result;
        this.taskResult = taskResult;

        // Setup submit bar actions and titles
        SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
        submitBar.setPositiveTitle(getContext().getString(R.string.rsb_continue));
        submitBar.setPositiveAction(v -> attemptSignIn());
        submitBar.setNegativeAction(v -> resendVerificationEmail());
        submitBar.setNegativeTitle(getContext().getString(R.string.rsb_resend_email));

        updateEmailText();

        RxView.clicks(findViewById(R.id.email_verification_wrong_email)).subscribe(v -> changeEmail());
    }

    // TODO: switch over to reading text from Step, and doing this in SurveyFactory
    private void updateEmailText() {
        int accentColor = ThemeUtils.getAccentColor(getContext());
        String accentColorString = "#" + Integer.toHexString(Color.red(accentColor)) +
                Integer.toHexString(Color.green(accentColor)) +
                Integer.toHexString(Color.blue(accentColor));
        final String email = getEmail(taskResult);
        String formattedSummary = getContext().getString(R.string.rsb_confirm_summary,
                "<font color=\"" + accentColorString + "\">" + email + "</font>");
        ((AppCompatTextView) findViewById(R.id.email_verification_body)).setText(Html.fromHtml(
                formattedSummary));
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, emailStep, stepResult);
        return super.onSaveInstanceState();
    }

    protected void validateStepAndResult(Step step) {
        if (!(step instanceof EmailVerificationStep)) {
            throw new IllegalStateException(
                    "EmailVerificationStepLayout is only compatible with a EmailVerificationStep");
        }

        emailStep = (EmailVerificationStep) step;
    }

    @Override
    public View getLayout() {
        return this;
    }

    /**
     * Method allowing a step to consume a back event.
     *
     * @return a boolean indication whether the back event is consumed
     */
    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, emailStep, stepResult);
        return false;
    }

    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    protected void changeEmail() {
        // TODO: create change email screen like in iOS
        Toast.makeText(getContext(), "TODO: implement change email screen", Toast.LENGTH_SHORT).show();
    }

    protected void resendVerificationEmail()
    {
        showLoadingDialog();
        final String email = getEmail(taskResult);
        DataProvider.getInstance()
                .resendEmailVerification(getContext(), email)
                .compose(ObservableUtils.applyDefault())
                .subscribe(dataResponse -> {
                    hideLoadingDialog();
                    Toast.makeText(getContext(), dataResponse.getMessage(), Toast.LENGTH_LONG).show();
                }, throwable -> {
                    // Convert errorBody to JSON-String, convert json-string to object
                    // (BridgeMessageResponse) and pass BridgeMessageResponse.getMessage()to
                    // toast
                    hideLoadingDialog();
                    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    protected void attemptSignIn()
    {
        showLoadingDialog();
        DataProvider.getInstance()
                .verifyEmail(getContext())
                .compose(ObservableUtils.applyDefault())
                .subscribe(dataResponse -> {
                    hideLoadingDialog();
                    if(dataResponse.isSuccess()) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, emailStep, stepResult);
                    } else {
                        Toast.makeText(getContext(), R.string.rsb_email_not_verified, Toast.LENGTH_LONG).show();
                    }
                }, error -> {
                    hideLoadingDialog();
                    Toast.makeText(getContext(), R.string.rsb_email_not_verified, Toast.LENGTH_LONG).show();
                });
    }

    protected String getEmail(TaskResult taskResult) {
        return getStringResult(taskResult, ProfileInfoOption.EMAIL.getIdentifier());
    }

    protected String getPassword(TaskResult taskResult) {
        return getStringResult(taskResult, ProfileInfoOption.PASSWORD.getIdentifier());
    }

    protected String getStringResult(TaskResult taskResult, String stepIdentifier) {
        StepResult stepResult = StepResultHelper.findStepResult(taskResult, stepIdentifier);
        if (stepResult != null) {
            Object resultObj = stepResult.getResult();
            if (resultObj instanceof String) {
                return (String)resultObj;
            }
        }
        return null;
    }
}
