package org.researchstack.skin.ui;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.DataProviderErrorReceiver;
import org.researchstack.skin.task.OnboardingTask;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.ui.layout.SignUpEligibleStepLayout;

import rx.Observable;

import static org.researchstack.skin.DataProviderErrorReceiver.ACTION_DATA_PROVIDER_ERROR;

public class BaseActivity extends PinCodeActivity {
    private BroadcastReceiver errorBroadcastReceiver = new DataProviderErrorReceiver();

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter errorFilter = new IntentFilter();
        errorFilter.addAction(ACTION_DATA_PROVIDER_ERROR);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(errorBroadcastReceiver, errorFilter);

        DataProviderErrorReceiver.handleErrors(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(errorBroadcastReceiver);
    }

    /**
     * Method is not safe and assumes tv-id or tv-index wont change.
     *
     * @return Snackbar message TextView
     */
    public TextView getSnackBarMessageView(Snackbar snackbar) {
        // Try id for app level snackbar id
        int id = org.researchstack.skin.R.id.snackbar_text;
        TextView tv = (TextView) snackbar.getView().findViewById(id);
        if (tv != null) {
            return tv;
        }

        // Try id for lib level snackbar id
        id = android.support.design.R.id.snackbar_text;
        tv = (TextView) snackbar.getView().findViewById(id);
        if (tv != null) {
            return tv;
        }

        // Lastly, get item at pos 0 and check if its a TextView. We don't use instanceOf since the
        // action is a Button who's super-type is also TextView.
        ViewGroup snackBarContainer = (ViewGroup) snackbar.getView();
        View childZero = snackBarContainer.getChildAt(0);
        if (childZero.getClass() == TextView.class) {
            return (TextView) childZero;
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OverviewActivity.REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String email = (String) result.getStepResult(OnboardingTask.SignInStepIdentifier)
                    .getResultForIdentifier(SignInTask.ID_EMAIL);
            String password = (String) result.getStepResult(OnboardingTask.SignInStepIdentifier)
                    .getResultForIdentifier(SignInTask.ID_PASSWORD);

            if (email != null && password != null) {
                Intent intent = new Intent(this, EmailVerificationActivity.class);
                intent.putExtra(EmailVerificationActivity.EXTRA_EMAIL, email);
                intent.putExtra(EmailVerificationActivity.EXTRA_PASSWORD, password);
                startActivity(intent);
            }
        } else if (requestCode == SignUpEligibleStepLayout.CONSENT_REQUEST && resultCode == RESULT_OK) {
            TaskResult consentResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            Observable.fromCallable(() -> {
                // Upload consent object
                DataProvider.getInstance().uploadConsent(this, consentResult);
                return null;
            }).compose(ObservableUtils.applyDefault()).subscribe();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}