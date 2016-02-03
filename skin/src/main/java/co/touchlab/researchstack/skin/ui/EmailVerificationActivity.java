package co.touchlab.researchstack.skin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.task.OrderedTask;
import co.touchlab.researchstack.backbone.ui.PinCodeActivity;
import co.touchlab.researchstack.backbone.ui.ViewTaskActivity;
import co.touchlab.researchstack.backbone.utils.ObservableUtils;
import co.touchlab.researchstack.backbone.utils.ResUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.DataProvider;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.task.OnboardingTask;
import co.touchlab.researchstack.skin.task.SignUpTask;
import co.touchlab.researchstack.skin.ui.layout.SignUpStepLayout;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class EmailVerificationActivity extends PinCodeActivity
{
    public static final  String EXTRA_EMAIL          = "EXTRA_EMAIL";
    public static final  String EXTRA_PASSWORD       = "EXTRA_PASSWORD";
    private static final String CHANGE_EMAIL_ID      = "CHANGE_EMAIL_ID";
    private static final int    REQUEST_CHANGE_EMAIL = 0;

    private String email;
    private String password;
    private View   progress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        progress = findViewById(R.id.progress);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        email = getIntent().getStringExtra(EXTRA_EMAIL);
        password = getIntent().getStringExtra(EXTRA_PASSWORD);

        ((AppCompatImageView) findViewById(R.id.study_logo)).setImageResource(ResourceManager.getInstance()
                .getLargeLogoDiseaseIcon());
        updateEmailText();

        RxView.clicks(findViewById(R.id.email_verification_wrong_email))
                .subscribe(v -> changeEmail());

        RxView.clicks(findViewById(R.id.email_verification_resend))
                .subscribe(v -> resendVerificationEmail());

        RxView.clicks(findViewById(R.id.continue_button)).subscribe(v -> attemptSignIn());
    }

    private void updateEmailText()
    {
        ((AppCompatTextView) findViewById(R.id.email_verification_body)).setText(getString(R.string.email_verification_body,
                ResUtils.getApplicationName(this),
                email));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CHANGE_EMAIL && resultCode == RESULT_OK)
        {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            StepResult stepResult = taskResult.getStepResult(OnboardingTask.SignUpStepIdentifier);

            // TODO figure out how we're doing this username field
            email = (String) stepResult.getResultForIdentifier(SignUpTask.ID_EMAIL);
            String newUsername = (String) stepResult.getResultForIdentifier(SignUpTask.ID_USERNAME);
            password = (String) stepResult.getResultForIdentifier(SignUpTask.ID_PASSWORD);
            updateEmailText();
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void changeEmail()
    {
        Step signUpStep = new Step(OnboardingTask.SignUpStepIdentifier);
        signUpStep.setStepLayoutClass(SignUpStepLayout.class);
        signUpStep.setTitle(getString(R.string.change_email));

        Intent intent = new Intent(this, ViewTaskActivity.class);
        intent.putExtra(ViewTaskActivity.EXTRA_TASK, new OrderedTask(CHANGE_EMAIL_ID, signUpStep));
        startActivityForResult(intent, REQUEST_CHANGE_EMAIL);
    }

    private void resendVerificationEmail()
    {
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
                    Toast.makeText(this, dataResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    // TODO Cast throwable to HttpException -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                    // Convert errorBody to JSON-String, convert json-string to object
                    // (BridgeMessageResponse) and pass BridgeMessageResponse.getMessage()to
                    // toast
                    progress.animate()
                            .alpha(0)
                            .withEndAction(() -> progress.setVisibility(View.GONE));
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void attemptSignIn()
    {
        progress.animate().alpha(1).withStartAction(() -> {
            progress.setVisibility(View.VISIBLE);
            progress.setAlpha(0);
        });

        DataProvider.getInstance()
                .signIn(this, email, password)
                .compose(ObservableUtils.applyDefault())
                .subscribe(dataResponse -> {
                    if(dataResponse.isSuccess())
                    {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    else
                    {
                        progress.animate()
                                .alpha(0)
                                .withEndAction(() -> progress.setVisibility(View.GONE));
                        Toast.makeText(EmailVerificationActivity.this,
                                dataResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    progress.animate()
                            .alpha(0)
                            .withEndAction(() -> progress.setVisibility(View.GONE));
                    Toast.makeText(EmailVerificationActivity.this,
                            R.string.email_not_verified,
                            Toast.LENGTH_SHORT).show();
                });
    }
}
