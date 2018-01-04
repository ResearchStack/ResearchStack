package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepLayoutHelper;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by TheMDP on 1/14/17.
 */

public class LoginStepLayout extends ProfileStepLayout {
    public LoginStepLayout(Context context) {
        super(context);
    }

    public LoginStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public LoginStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        super.initialize(step, result);

        FormStepData emailStepData = getFormStepData(ProfileInfoOption.EMAIL.getIdentifier());
        if (emailStepData != null) {
            // Add the Forgot Password UI below the login form
            // Only add this if there is an Email step in the form. This might not be present if,
            // for example, we are logging in using a method other than Email.
            if (submitBar != null) {
                submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
                submitBar.setNegativeTitle(R.string.rsb_forgot_password);
                submitBar.setNegativeAction(v -> forgotPasswordClicked());
            }
        }
    }

    @Override
    protected void onNextClicked() {
        boolean isAnswerValid = isAnswerValid(subQuestionStepData, true);
        if (isAnswerValid) {
            showLoadingDialog();

            final String email = getEmail();
            boolean hasEmail = email != null && !email.isEmpty();
            final String password = getPassword();
            boolean hasPassword = password != null && !password.isEmpty();
            final String externalId = getExternalId();
            boolean hasExternalId = externalId != null && !externalId.isEmpty();

            Observable<DataResponse> login;
            if (hasEmail && hasPassword) {
                // Login with email and password.
                login = DataProvider.getInstance().signIn(getContext(), email, password);
            } else if (hasEmail && !getProfileStep().getProfileInfoOptions().contains
                    (ProfileInfoOption.PASSWORD)) {
                login = DataProvider.getInstance().requestSignInLink(email);
            }else if (hasExternalId) {
                // Login with external ID.
                login = DataProvider.getInstance().signInWithExternalId(getContext(), externalId);
            } else {
                // This should never happen, but if it does, fail gracefully.
                hideLoadingDialog();
                showOkAlertDialog("Unexpected error: No credentials provided.");
                return;
            }

            // Only gives a callback to response on success, the rest is handled by StepLayoutHelper
            StepLayoutHelper.safePerform(login, this, new StepLayoutHelper.WebCallback() {
                @Override
                public void onSuccess(DataResponse response) {
                    hideLoadingDialog();
                    LoginStepLayout.super.onNextClicked();
                }

                @Override
                public void onFail(Throwable throwable) {
                    hideLoadingDialog();
                    // TODO: use the status code instead of this string
                    if (throwable instanceof UnknownHostException) {
                        // This is likely a no internet connection error
                        LoginStepLayout.super.showOkAlertDialog(getString(R.string.rsb_error_no_internet));
                    } else if (throwable.toString().contains("statusCode=412")) {
                        // Moving to the next step will trigger the re-consent flow
                        // Since the user is not consented, but signed in successfully
                        LoginStepLayout.super.onNextClicked();
                    } else {
                        showOkAlertDialog(throwable.getMessage());
                    }
                }
            });
        }
    }

    protected void forgotPasswordClicked() {
        // Forgot password button only needs a valid email
        List<FormStepData> validSteps = new ArrayList<>();
        validSteps.add(getFormStepData(ProfileInfoOption.EMAIL.getIdentifier()));
        boolean isEmailValid = isAnswerValid(validSteps, true);
        if (isEmailValid) {

            Observable<DataResponse> forgotPassword = DataProvider.getInstance()
                    .forgotPassword(getContext(), getEmail())
                    .compose(ObservableUtils.applyDefault());

            // Only gives a callback to response on success, the rest is handled by StepLayoutHelper
            StepLayoutHelper.safePerformWithAlerts(forgotPassword, this, response ->
                    showOkAlertDialog(response.getMessage())
            );
        }
    }
}
