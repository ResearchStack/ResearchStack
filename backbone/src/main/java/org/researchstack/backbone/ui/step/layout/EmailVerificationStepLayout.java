package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.PasswordAnswerFormat;
import org.researchstack.backbone.model.ConsentSignatureBody;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.User;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.ConsentReviewSubstepListStep;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.ui.step.body.TextQuestionBody;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepLayoutHelper;
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

    protected StepResult<StepResult> stepResult;

    /**
     * If this is set, the EmailVerificationStepLayout will not create a field for the user
     * to enter their password.
     */
    @Nullable protected String password;

    /**
     * This is only created if the user's app crashed or they forced closed the app
     * while they were going through the sign up process
     */
    @Nullable StepBody passwordStepBody;
    @Nullable View     passwordVerifyView;

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
    public void initialize(Step step, StepResult result) {
        validateStepAndResult(step);

        this.stepResult = result;

        // Setup submit bar actions and titles
        SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
        submitBar.setPositiveTitle(getContext().getString(R.string.rsb_continue));
        submitBar.setPositiveAction(v -> signIn());
        submitBar.setNegativeAction(v -> resendVerificationEmail());
        submitBar.setNegativeTitle(getContext().getString(R.string.rsb_resend_email));

        updateEmailText();

        RxView.clicks(findViewById(R.id.rsb_email_verification_wrong_email)).subscribe(v -> changeEmail());

        // If the the password isn't in the TaskResult, we have to make the user re-enter their's
        if (getPassword() == null) {
            createValidatePasswordStepBody(container());
        }
    }

    // TODO: switch over to reading text from Step, and doing this in SurveyFactory
    private void updateEmailText() {
        int accentColor = ThemeUtils.getAccentColor(getContext());
        String accentColorString = "#" + Integer.toHexString(Color.red(accentColor)) +
                Integer.toHexString(Color.green(accentColor)) +
                Integer.toHexString(Color.blue(accentColor));
        final String email = getEmail();
        String formattedSummary = getContext().getString(R.string.rsb_confirm_summary,
                "<font color=\"" + accentColorString + "\">" + email + "</font>");
        ((AppCompatTextView) findViewById(R.id.rsb_email_verification_body)).setText(Html.fromHtml(
                formattedSummary));
    }

    @Override
    public Parcelable onSaveInstanceState() {
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
    public boolean isBackEventConsumed() {
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

    protected void resendVerificationEmail() {
        showLoadingDialog();
        final String email = getEmail();
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

    protected void signIn() {
        final String password = getPassword();
        if (password == null || password.isEmpty()) {
            Toast.makeText(getContext(), R.string.rsb_error_invalid_password, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog();
        DataProvider.getInstance()
                .verifyEmail(getContext(), password)
                .compose(ObservableUtils.applyDefault())
                .subscribe(dataResponse -> {
                    hideLoadingDialog();
                    if(dataResponse.isSuccess()) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, emailStep, stepResult);
                    } else {
                        showOkAlertDialog(dataResponse.getMessage());
                    }
                }, error -> {
                    hideLoadingDialog();
                    // TODO: fix this once the BridgeDataProvider is fixed
                    if (error.toString().toLowerCase().contains("ConsentRequired".toLowerCase())) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, emailStep, stepResult);
                    } else {
                        showOkAlertDialog(error.getMessage());
                    }
                });
    }

    protected RelativeLayout container() {
        return (RelativeLayout)findViewById(R.id.rsb_email_verification_container);
    }

    /**
     * If the app has crashed, or user has force closed it, we will need them to re-enter their password
     * Since they will be essentially signing in again
     */
    protected void createValidatePasswordStepBody(RelativeLayout container) {
        // Create a verify password step
        QuestionStep verifyPasswordStep = new QuestionStep(ProfileInfoOption.PASSWORD.getIdentifier());
        verifyPasswordStep.setAnswerFormat(new PasswordAnswerFormat());
        verifyPasswordStep.setPlaceholder(getContext().getString(R.string.rsb_password_placeholder));
        verifyPasswordStep.setTitle(getContext().getString(R.string.rsb_verify_password));

        // Use FormStepLayout logic to create the StepLayout for verifyPasswordStep
        passwordStepBody = SurveyStepLayout.createStepBody(verifyPasswordStep, null);
        passwordVerifyView = FormStepLayout.initStepBodyHolder(layoutInflater, container, verifyPasswordStep, passwordStepBody);

        // Replace Space with password view
        View oldPasswordSpace = findViewById(R.id.rsb_email_verify_reenter_password_space);
        passwordVerifyView.setLayoutParams(oldPasswordSpace.getLayoutParams());
        container.removeView(oldPasswordSpace);
        container.addView(passwordVerifyView);
    }

    protected String getEmail() {
        User user = DataProvider.getInstance().getUser(getContext());
        if (user == null || user.getEmail() == null) {
            throw new IllegalStateException("Email must be set on user at this point");
        }
        return user.getEmail();
    }

    public void setPassword(String password) {
        if (passwordVerifyView != null) {
            container().removeView(passwordVerifyView);
            passwordStepBody = null;
        }
        this.password = password;
    }

    protected String getPassword() {
        if (passwordStepBody == null) {
            return password;
        } else {
            StepResult result = passwordStepBody.getStepResult(false);
            return (String)result.getResult();
        }
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
