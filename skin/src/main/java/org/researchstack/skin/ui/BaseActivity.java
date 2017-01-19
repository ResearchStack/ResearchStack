package org.researchstack.skin.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.task.OnboardingTask;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.ui.layout.SignUpEligibleStepLayout;

import rx.Observable;
import rx.functions.Action1;

public class BaseActivity extends PinCodeActivity {

    BroadcastReceiver errorBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogExt.i(getClass(), "errorBroadcastReceiver()");

            if (AppPrefs.getInstance(context).skippedOnboarding()) {
                // We don't want to bother a user that has skipped sign-up with the signed out
                // or consent messages. Short-circuiting until we have an approved message to show
                // a user that has skipped.
                return;
            }
            String messageText = null;
            String actionText = null;
            int length = Snackbar.LENGTH_INDEFINITE;
            Action1<View> action = null;

            switch (intent.getAction()) {
                case DataProvider.ERROR_CONSENT_REQUIRED:
                    messageText = getString(R.string.rss_network_error_consent);
                    actionText = getString(R.string.rss_network_error_consent_action);
                    action = v -> {
                        Task task = TaskProvider.getInstance().get(TaskProvider.TASK_ID_CONSENT);
                        Intent consentTask = ViewTaskActivity.newIntent(BaseActivity.this, task);
                        startActivityForResult(consentTask, SignUpEligibleStepLayout.CONSENT_REQUEST);
                    };
                    break;

                case DataProvider.ERROR_NOT_AUTHENTICATED:
                    messageText = getString(R.string.rss_network_error_sign_in);
                    actionText = getString(R.string.rss_network_error_sign_in_action);
                    action = v -> {
                        boolean hasPinCode = StorageAccess.getInstance()
                                .hasPinCode(BaseActivity.this);
                        SignInTask task = (SignInTask) TaskProvider.getInstance()
                                .get(TaskProvider.TASK_ID_SIGN_IN);
                        task.setHasPasscode(hasPinCode);
                        startActivityForResult(SignUpTaskActivity.newIntent(BaseActivity.this,
                                task), OnboardingActivity.REQUEST_CODE_SIGN_IN);
                    };
                    break;
            }

            // Throw up a Snackbar
            View root = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(root, messageText, length);
            if (action != null) {
                snackbar.setAction(actionText, action::call);
            }
            snackbar.getView().setOnClickListener(v -> snackbar.dismiss());
            snackbar.setActionTextColor(ContextCompat.getColor(BaseActivity.this,
                    R.color.rss_snackbar_action_color));
            TextView messageView = getSnackBarMessageView(snackbar);
            if (messageView != null) {
                messageView.setTextColor(Color.WHITE);
            }

            snackbar.show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter errorFilter = new IntentFilter();
        errorFilter.addAction(DataProvider.ERROR_CONSENT_REQUIRED);
        errorFilter.addAction(DataProvider.ERROR_NOT_AUTHENTICATED);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(errorBroadcastReceiver, errorFilter);
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
    private TextView getSnackBarMessageView(Snackbar snackbar) {
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
        if (requestCode == OnboardingActivity.REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
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
