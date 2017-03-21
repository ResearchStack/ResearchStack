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
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepLayoutHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // Add the Forgot Password UI below the login form
        submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
        submitBar.setNegativeTitle(R.string.rsb_forgot_password);
        submitBar.setNegativeAction(v -> forgotPasswordClicked());
    }

    @Override
    protected void onNextClicked() {
        boolean isAnswerValid = isAnswerValid(subQuestionStepData, true);
        if (isAnswerValid) {
            showLoadingDialog();

            final String email = getEmail();
            final String password = getPassword();

            Observable<DataResponse> login = DataProvider.getInstance()
                    .signIn(getContext(), email, password);

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
                    if (throwable.toString().contains("statusCode=412")) {
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
