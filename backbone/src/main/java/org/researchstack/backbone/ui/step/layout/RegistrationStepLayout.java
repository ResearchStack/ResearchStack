package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import org.researchstack.backbone.DataProvider;

import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepLayoutHelper;

import rx.Observable;

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
    public boolean isBackEventConsumed() {
        return true;  // can't move backwards from this step layout
    }

    @Override
    protected void onNextClicked() {
        boolean isAnswerValid = isAnswerValid(subQuestionStepData, true);
        if (isAnswerValid) {

            final String email = getEmail();
            final String password = getPassword();
            final String confirmPassword = getConfirmPassword();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), getString(R.string.rsb_error_passwords_do_not_match), Toast.LENGTH_SHORT).show();
                return;
            }

            Observable<DataResponse> registration = DataProvider.getInstance()
                    // As of right now, username is unused in and email is only supported
                    .signUp(getContext(), email, email, password)
                    .compose(ObservableUtils.applyDefault());

            // Only gives a callback to response on success, the rest is handled by StepLayoutHelper
            StepLayoutHelper.safePerformWithAlerts(registration, this, response ->
                    super.onNextClicked()
            );
        }
    }
}
