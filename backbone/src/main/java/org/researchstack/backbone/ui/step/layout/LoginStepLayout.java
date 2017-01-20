package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.ObservableUtils;

import java.util.HashSet;
import java.util.Set;

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
        boolean isAnswerValid = isAnswerValid(subQuestionSteps.keySet(), true);
        if (isAnswerValid) {
            showLoadingDialog();

            final String email = getEmail();
            final String password = getPassword();

            DataProvider.getInstance()
                    .signIn(getContext(), email, password)
                    .compose(ObservableUtils.applyDefault())
                    .subscribe(dataResponse -> {
                        hideLoadingDialog();
                        if(dataResponse.isSuccess()) {
                            super.onNextClicked();
                        } else {
                            showOkAlertDialog(dataResponse.getMessage());
                        }
                    }, throwable -> {
                        hideLoadingDialog();
                        showOkAlertDialog(throwable.getMessage());
                    });
        }
    }

    protected void forgotPasswordClicked() {
        // Forgot password button only needs a valid email
        Set<QuestionStep> validSteps = new HashSet<>();
        validSteps.add(getEmailStep());
        boolean isEmailValid = isAnswerValid(validSteps, true);
        if (isEmailValid) {
            showLoadingDialog();
            String email = getEmail();
            DataProvider.getInstance()
                    .forgotPassword(getContext(), email)
                    .compose(ObservableUtils.applyDefault())
                    .subscribe(dataResponse -> {
                        hideLoadingDialog();
                        showOkAlertDialog(dataResponse.getMessage());
                    }, throwable -> {
                        hideLoadingDialog();
                        showOkAlertDialog(throwable.getMessage());
                    });
        }
    }
}
