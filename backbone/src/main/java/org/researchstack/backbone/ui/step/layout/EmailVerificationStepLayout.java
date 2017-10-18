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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.PasswordAnswerFormat;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.User;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.EmailVerificationSubStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.RegistrationStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubstepListStep;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepLayoutHelper;
import org.researchstack.backbone.utils.StepResultHelper;
import org.researchstack.backbone.utils.ThemeUtils;

import rx.Observable;

/**
 * Created by TheMDP on 1/21/17.
 *
 * The EmailVerificationStepLayout consists of the Email Verification screen,
 * and a Registration screen ,which is used to change the user's email if it's not the correct one.
 *
 * Under the hood, this StepLayout is a SubstepListStep which uses a view pager to switch between
 * screens.  Having both screens in the same StepLayout allows them to safely exchange the
 * user's password they set when changing email.
 */

public class EmailVerificationStepLayout extends ViewPagerSubstepListStepLayout {

    private static final int EMAIL_VERIFY_SUBSTEP_INDEX = 0;
    private static final int REGISTRATION_SUBSTEP_INDEX = 1;

    private String setPasswordOnLoad;

    public EmailVerificationStepLayout(Context context) {
        super(context);
    }

    public EmailVerificationStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateStep(step);
        super.initialize(step, result);
    }

    @Override
    protected void onStepLayoutCreated(StepLayout stepLayout, int index) {
        super.onStepLayoutCreated(stepLayout, index);
        if (index == EMAIL_VERIFY_SUBSTEP_INDEX && stepLayout instanceof SubStepLayout) {
            setupVerifyEmailSubstepLayout((SubStepLayout)stepLayout);
        } else if (index == REGISTRATION_SUBSTEP_INDEX && stepLayout instanceof RegistrationStepLayout) {
            setupRegistrationSubstepLayout((RegistrationStepLayout)stepLayout);
        }
    }

    protected void setupVerifyEmailSubstepLayout(SubStepLayout substepLayout) {
        substepLayout.setSubstepListener(() -> {
            viewPagerAdapter.moveNext();
            saveViewPagerIndex();
        });
        if (setPasswordOnLoad != null) {
            substepLayout.setPassword(setPasswordOnLoad);
            setPasswordOnLoad = null;
        }
    }

    protected void setupRegistrationSubstepLayout(RegistrationStepLayout registrationStepLayout) {
        // Re-word the standard registration step layout submit bar buttons, and hook them
        // into custom actions that make sense for this SubstepLayout
        final Step registrationSubstep = substepListStep.getStepList().get(REGISTRATION_SUBSTEP_INDEX);
        SubmitBar submitBar = registrationStepLayout.getSubmitBar();
        submitBar.setPositiveTitle(R.string.rsb_change);
        submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
        submitBar.setNegativeTitle(R.string.rsb_cancel);
        submitBar.setNegativeAction(v -> super.onSaveStep(ACTION_PREV, registrationSubstep, null));
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result) {
        int indexOfStep = substepListStep.getStepList().indexOf(step);

        // If the registration substep is trying to move next,
        // That means that the email has been changed for the user
        // and we need to update the EmailVerificationSubstep to reflect the changes
        // and also send a previous movement to the subclass to animate the change
        if (indexOfStep == REGISTRATION_SUBSTEP_INDEX && action == ACTION_NEXT) {

            // Update email layout to reflect the changes to email and password
            StepResult passwordStepResult = StepResultHelper.findStepResult(
                    result, ProfileInfoOption.PASSWORD.getIdentifier());

            if (passwordStepResult != null && passwordStepResult.getResult() instanceof String) {
                SubStepLayout emailLayout = getEmailSubstepLayout();
                emailLayout.setPassword((String)passwordStepResult.getResult());
                emailLayout.updateEmailText();
                emailLayout.updatePasswordState();
            }

            // Move the view pager back to the email verification step layout
            super.onSaveStep(ACTION_PREV, step, result);

        } else if (indexOfStep == EMAIL_VERIFY_SUBSTEP_INDEX && action == ACTION_NEXT) {
            // This actually means a successfully verified email, so send user forward,
            // which will skip the RegistrationStep
            stepResult.getResults().put(step.getIdentifier(), result);
            super.onComplete();

        } else {
            super.onSaveStep(action, step, result);
        }
    }

    /**
     * @param password explicitly set password for EmailVerificationSubstepLayout
     */
    public void setPassword(String password) {
        // We may need to wait until the the StepLayout is created
        SubStepLayout substepLayout = getEmailSubstepLayout();
        if (substepLayout != null) {
            substepLayout.setPassword(password);
        } else {
            setPasswordOnLoad = password;
        }
    }

    protected void validateStep(Step step) {
        if (!(step instanceof EmailVerificationStep)) {
            throw new IllegalStateException("EmailVerificationStepLayout needs EmailVerificationStep");
        }

        EmailVerificationStep emailVerificationStep = (EmailVerificationStep)step;
        if (!(emailVerificationStep.getStepList().get(EMAIL_VERIFY_SUBSTEP_INDEX) instanceof EmailVerificationSubStep)) {
            throw new IllegalStateException("EmailVerificationStepLayout expects EmailVerificationSubStep at index " + EMAIL_VERIFY_SUBSTEP_INDEX);
        }
        if (!(emailVerificationStep.getStepList().get(REGISTRATION_SUBSTEP_INDEX) instanceof RegistrationStep)) {
            throw new IllegalStateException("EmailVerificationStepLayout expects RegistrationStep at index " + EMAIL_VERIFY_SUBSTEP_INDEX);
        }
        substepListStep = emailVerificationStep;
    }

    protected SubStepLayout getEmailSubstepLayout() {
        return (SubStepLayout)super.getStepLayout(EMAIL_VERIFY_SUBSTEP_INDEX);
    }

    protected RegistrationStepLayout getRegistrationSubstepLayout() {
        return (RegistrationStepLayout) super.getStepLayout(REGISTRATION_SUBSTEP_INDEX);
    }

    public static class SubStepLayout extends FixedSubmitBarLayout implements StepLayout {

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // Communicate w/ host
        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        protected StepCallbacks callbacks;
        protected EmailVerificationSubStep emailStep;
        protected StepResult<String> innerStepResult;

        private SubstepListener substepListener;

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
        @Nullable View passwordVerifyView;

        public SubStepLayout(Context context) {
            super(context);
        }

        public SubStepLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SubStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(21)
        public SubStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public int getContentResourceId() {
            return R.layout.rsb_layout_email_verification;
        }

        @Override
        public void initialize(Step step, StepResult result) {
            validateStepAndResult(step, result);

            setupPasswordFromResult();

            // Setup submit bar actions and titles
            SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
            submitBar.setPositiveTitle(getContext().getString(R.string.rsb_continue));
            submitBar.setPositiveAction(v -> signIn());
            submitBar.setNegativeAction(v -> resendVerificationEmail());
            submitBar.setNegativeTitle(getContext().getString(R.string.rsb_resend_email));

            if (emailStep.getTitle() != null) {
                ((TextView)findViewById(R.id.rsb_step_title)).setText(emailStep.getTitle());
            }

            RxView.clicks(findViewById(R.id.rsb_email_verification_wrong_email)).subscribe(v -> changeEmail());

            updateEmailText();
            updatePasswordState();
        }

        private void setupPasswordFromResult() {
            // This is an edge case for when user has changed their email,
            //  and the password result is directly accessible from this class
            if (innerStepResult != null && innerStepResult.getResult() instanceof String) {
                setPassword((String)innerStepResult.getResult());
            }
        }

        public void updatePasswordState() {
            // If the the password isn't in the TaskResult, we have to make the user re-enter their's
            if (getPassword() == null) {
                if (passwordVerifyView == null) {
                    createValidatePasswordStepBody(container());
                } else {
                    passwordVerifyView.setVisibility(View.VISIBLE);
                }
            } else if (passwordVerifyView != null) {
                passwordVerifyView.setVisibility(View.GONE);
            }
        }

        public void updateEmailText() {
            if (emailStep.getText() == null) {
                int accentColor = ThemeUtils.getAccentColor(getContext());
                String accentColorString = "#" + Integer.toHexString(Color.red(accentColor)) +
                        Integer.toHexString(Color.green(accentColor)) +
                        Integer.toHexString(Color.blue(accentColor));
                final String email = getEmail();
                String formattedSummary = getContext().getString(R.string.rsb_confirm_summary,
                        "<font color=\"" + accentColorString + "\">" + email + "</font>");
                ((AppCompatTextView) findViewById(R.id.rsb_email_verification_body)).setText(Html.fromHtml(
                        formattedSummary));
            } else {
                ((AppCompatTextView) findViewById(R.id.rsb_email_verification_body)).setText(emailStep.getText());
            }
        }

        @Override
        public Parcelable onSaveInstanceState() {
            callbacks.onSaveStep(StepCallbacks.ACTION_NONE, emailStep, innerStepResult);
            return super.onSaveInstanceState();
        }

        @SuppressWarnings("unchecked")  // needed for unchecked StepResult generic type casting
        protected void validateStepAndResult(Step step, StepResult result) {
            if (!(step instanceof EmailVerificationSubStep)) {
                throw new IllegalStateException(
                        "EmailVerificationStepLayout is only compatible with a EmailVerificationSubStep");
            }

            emailStep = (EmailVerificationSubStep) step;

            this.innerStepResult = result;
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
            return true;  // can't move backwards from this step layout
        }

        public void setCallbacks(StepCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        protected void changeEmail() {
            // Send the user back to the Registration step, but we will let the listener control that
            if (substepListener != null) {
                substepListener.onChangeEmailRequested();
            }
        }

        protected void resendVerificationEmail() {

            final String email = getEmail();

            Observable<DataResponse> resend = DataProvider.getInstance()
                    .resendEmailVerification(getContext(), email)
                    .compose(ObservableUtils.applyDefault());

            // Only gives a callback to response on success, the rest is handled by StepLayoutHelper
            StepLayoutHelper.safePerformWithAlerts(resend, this, response ->
            { // loading dialog will dismiss indicating success
            });
        }

        protected void signIn() {
            final String password = getPassword();
            if (password == null || password.isEmpty()) {
                Toast.makeText(getContext(), R.string.rsb_error_invalid_password, Toast.LENGTH_SHORT).show();
                return;
            }

            Observable<DataResponse> verify = DataProvider.getInstance()
                    .verifyEmail(getContext(), password)
                    .compose(ObservableUtils.applyDefault());

            StepLayoutHelper.safePerformWithOnlyLoadingAlerts(verify, this, new StepLayoutHelper.WebCallback() {
                @Override
                public void onSuccess(DataResponse response) {
                    if(response.isSuccess()) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, emailStep, innerStepResult);
                    } else {
                        showOkAlertDialog(response.getMessage());
                    }
                }

                @Override
                public void onFail(Throwable throwable) {
                    // TODO: fix this once the BridgeDataProvider is fixed
                    if (throwable.toString().toLowerCase().contains("ConsentRequired".toLowerCase())) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, emailStep, innerStepResult);
                    } else {
                        showOkAlertDialog(throwable.getMessage());
                    }
                }
            });
        }

        protected RelativeLayout container() {
            return (RelativeLayout)findViewById(R.id.rsb_email_verification_container);
        }

        /**
         * If the app has crashed, or user has force closed it, we will need them to re-enter their password
         * Since they will be essentially signing in again
         * @param container container to add the step body view to
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

        public void setPassword(@Nullable String password) {
            // If we had the enter password view, remove it now that we have a valid password
            if (passwordVerifyView != null) {
                container().removeView(passwordVerifyView);
                passwordVerifyView = null;
                passwordStepBody = null;
            }
            this.password = password;

            // Save this in our step result
            StepResult<String> passwordResult = new StepResult<>(emailStep);
            passwordResult.setResult(password);
            callbacks.onSaveStep(StepCallbacks.ACTION_NONE, emailStep, innerStepResult);
        }

        protected String getPassword() {
            if (passwordVerifyView == null || passwordVerifyView.getVisibility() == View.GONE) {
                return password;
            } else if (passwordStepBody != null) {
                StepResult result = passwordStepBody.getStepResult(false);
                return (String)result.getResult();
            } else {
                return null;
            }
        }

        public void setSubstepListener(SubstepListener listener) {
            substepListener = listener;
        }

        public interface SubstepListener {
            void onChangeEmailRequested();
        }
    }
}
