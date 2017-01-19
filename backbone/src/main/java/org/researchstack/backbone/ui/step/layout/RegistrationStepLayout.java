package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

import org.researchstack.backbone.DataProvider;

import org.researchstack.backbone.utils.ObservableUtils;

/**
 * Created by TheMDP on 1/15/17.
 */

public class RegistrationStepLayout extends ProfileStepLayout {
    public RegistrationStepLayout(Context context) {
        super(context);
    }

    public RegistrationStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RegistrationStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public RegistrationStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onNextClicked() {
        boolean isAnswerValid = isAnswerValid(subQuestionSteps.keySet(), true);
        if (isAnswerValid) {
            showLoadingDialog();

            final String email = getEmail();
            final String password = getPassword();

            DataProvider.getInstance()
                    // As of right now, username is unused in and email is only supported
                    .signUp(getContext(), email, email, password)
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
}
