package org.researchstack.skin.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.ThemeUtils;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.task.OnboardingTask;
import org.researchstack.skin.task.SignUpTask;
import org.researchstack.skin.ui.layout.SignUpStepLayout;


public class EmailVerificationActivity extends PinCodeActivity {
    public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
    public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";
    private static final String CHANGE_EMAIL_ID = "CHANGE_EMAIL_ID";
    private static final int REQUEST_CHANGE_EMAIL = 0;

    private String email;
    private String password;
    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_activity_email_verification);
        progress = findViewById(R.id.progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataReady() {
        super.onDataReady();

        email = getIntent().getStringExtra(EXTRA_EMAIL);
        password = getIntent().getStringExtra(EXTRA_PASSWORD);

        updateEmailText();

        RxView.clicks(findViewById(R.id.email_verification_wrong_email))
                .subscribe(v -> changeEmail());

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveAction(v -> attemptSignIn());
        submitBar.setNegativeAction(v -> resendVerificationEmail());
    }

    private void updateEmailText() {
        int accentColor = ThemeUtils.getAccentColor(this);
        String accentColorString = "#" + Integer.toHexString(Color.red(accentColor)) +
                Integer.toHexString(Color.green(accentColor)) +
                Integer.toHexString(Color.blue(accentColor));
        String formattedSummary = getString(R.string.rss_confirm_summary,
                "<font color=\"" + accentColorString + "\">" + email + "</font>");
        ((AppCompatTextView) findViewById(R.id.email_verification_body)).setText(Html.fromHtml(
                formattedSummary));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHANGE_EMAIL && resultCode == RESULT_OK) {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            StepResult stepResult = taskResult.getStepResult(OnboardingTask.SignUpStepIdentifier);

            String newEmail = (String) stepResult.getResultForIdentifier(SignUpTask.ID_EMAIL);
            String newPassword = (String) stepResult.getResultForIdentifier(SignUpTask.ID_PASSWORD);

            // need to overwrite the values in the intent since they will be re-read in onDataReady
            Intent intent = getIntent();
            intent.putExtra(EXTRA_EMAIL, newEmail);
            intent.putExtra(EXTRA_PASSWORD, newPassword);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void changeEmail() {
        Step signUpStep = new Step(OnboardingTask.SignUpStepIdentifier);
        signUpStep.setStepTitle(R.string.rss_sign_up);
        signUpStep.setStepLayoutClass(SignUpStepLayout.class);
        signUpStep.setTitle(getString(R.string.rss_change_email));

        Intent intent = new Intent(this, ViewTaskActivity.class);
        intent.putExtra(ViewTaskActivity.EXTRA_TASK, new OrderedTask(CHANGE_EMAIL_ID, signUpStep));
        startActivityForResult(intent, REQUEST_CHANGE_EMAIL);
    }

    private void resendVerificationEmail() {
        progress.animate().alpha(1).withStartAction(() -> {
            progress.setVisibility(View.VISIBLE);
            progress.setAlpha(0);
        });

        DataProvider.getInstance()
                .resendEmailVerification(this, email)
                .compose(ObservableUtils.applyDefault())
                .subscribe(dataResponse -> {
                    progress.animate()
                            .alpha(0)
                            .withEndAction(() -> progress.setVisibility(View.GONE));
                    Toast.makeText(this, dataResponse.getMessage(), Toast.LENGTH_LONG).show();
                }, throwable -> {
                    // Convert errorBody to JSON-String, convert json-string to object
                    // (BridgeMessageResponse) and pass BridgeMessageResponse.getMessage()to
                    // toast
                    progress.animate()
                            .alpha(0)
                            .withEndAction(() -> progress.setVisibility(View.GONE));
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void attemptSignIn() {
        progress.animate().alpha(1).withStartAction(() -> {
            progress.setVisibility(View.VISIBLE);
            progress.setAlpha(0);
        });

        DataProvider.getInstance()
                .signIn(this, email, password)
                .compose(ObservableUtils.applyDefault())
                .subscribe(dataResponse -> {
                    if (dataResponse.isSuccess()) {
                        // Start MainActivity w/ clear_top and single_top flags. MainActivity may
                        // already be on the activity-task. We want to re-use that activity instead
                        // of creating a new instance and have two instance active.
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        progress.animate()
                                .alpha(0)
                                .withEndAction(() -> progress.setVisibility(View.GONE));
                        Toast.makeText(EmailVerificationActivity.this,
                                R.string.rss_email_not_verified, Toast.LENGTH_LONG).show();
                    }
                }, error -> {
                    progress.animate()
                            .alpha(0)
                            .withEndAction(() -> progress.setVisibility(View.GONE));
                    Toast.makeText(EmailVerificationActivity.this,
                            R.string.rss_email_not_verified, Toast.LENGTH_LONG).show();
                });
    }
}
