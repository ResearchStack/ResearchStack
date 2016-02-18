package co.touchlab.researchstack.skin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import co.touchlab.researchstack.backbone.StorageAccess;
import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.task.OrderedTask;
import co.touchlab.researchstack.backbone.ui.PinCodeActivity;
import co.touchlab.researchstack.backbone.ui.ViewTaskActivity;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.DataProvider;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.TaskProvider;
import co.touchlab.researchstack.skin.UiManager;
import co.touchlab.researchstack.skin.model.StudyOverviewModel;
import co.touchlab.researchstack.skin.step.PassCodeCreationStep;
import co.touchlab.researchstack.skin.task.OnboardingTask;
import co.touchlab.researchstack.skin.task.SignInTask;
import co.touchlab.researchstack.skin.task.SignUpTask;
import co.touchlab.researchstack.skin.ui.adapter.OnboardingPagerAdapter;
import co.touchlab.researchstack.skin.utils.JsonUtils;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class OnboardingActivity extends PinCodeActivity
{
    public static final int REQUEST_CODE_SIGN_UP  = 21473;
    public static final int REQUEST_CODE_SIGN_IN  = 31473;
    public static final int REQUEST_CODE_PASSCODE = 41473;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_onboarding);

        StudyOverviewModel model = parseStudyOverviewModel();
        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this, model.getQuestions());

        findViewById(R.id.intro_skip).setVisibility(UiManager.getInstance().isConsentSkippable()
                ? View.VISIBLE
                : View.GONE);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        //      TODO  pager.setPageTransformer();
        //      TODO  pager.setPageMargin();

        //        final PageIndicator indicator = (PageIndicator) findViewById(R.id.pager_indicator);
        //        indicator.removeAllMarkers(true);
        //        indicator.addMarkers(adapter.getCount(),
        //                R.drawable.ic_pageindicator_current_dark,
        //                R.drawable.ic_pageindicator_default_dark,
        //                true);

        //        pager.clearOnPageChangeListeners();
        //        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        //        {
        //            @Override
        //            public void onPageSelected(int position)
        //            {
        //                indicator.setActiveMarker(position);
        //            }
        //        });

        // let them view this page without making passcode, but call onDataReady if they have
        //        NewStorageManager newStorageManager = NewStorageManager.getInstance();
        //        if(((AesFileAccess) fileAccess).passphraseExists(this))
        //        {
        //            initFileAccess();
        //        }
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        // go straight to login screen if signed up but not verified
        if(DataProvider.getInstance().isSignedUp(this))
        {
            onSignInClicked(null);
        }
    }

    //TODO Read on main thread for intense UI blockage.
    private StudyOverviewModel parseStudyOverviewModel()
    {
        int fileResId = ResourceManager.getInstance().getStudyOverviewSections();
        return JsonUtils.loadClass(OnboardingActivity.this, StudyOverviewModel.class, fileResId);
    }

    public void onSignUpClicked(View view)
    {
        boolean hasPin = StorageAccess.getInstance().hasPinCode(this);

        SignUpTask task = (SignUpTask) TaskProvider.getInstance().get(TaskProvider.TASK_ID_SIGN_UP);
        task.setHasPasscode(hasPin);
        startActivityForResult(SignUpTaskActivity.newIntent(this, task), REQUEST_CODE_SIGN_UP);
    }

    public void onSkipClicked(View view)
    {
        boolean hasPasscode = StorageAccess.getInstance().hasPinCode(this);
        if(!hasPasscode)
        {
            PassCodeCreationStep step = new PassCodeCreationStep(OnboardingTask.SignUpPassCodeCreationStepIdentifier,
                    R.string.passcode);
            // TODO make a passcode task?
            OrderedTask task = new OrderedTask("PasscodeTask", step);
            startActivityForResult(ViewTaskActivity.newIntent(this, task), REQUEST_CODE_PASSCODE);
        }
        else
        {
            startMainActivity();
        }
    }

    public void onSignInClicked(View view)
    {
        boolean hasPasscode = StorageAccess.getInstance().hasPinCode(this);

        SignInTask task = (SignInTask) TaskProvider.getInstance().get(TaskProvider.TASK_ID_SIGN_IN);
        task.setHasPasscode(hasPasscode);
        startActivityForResult(SignUpTaskActivity.newIntent(this, task), REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK)
        {
            finish();
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String email = (String) result.getStepResult(OnboardingTask.SignInStepIdentifier)
                    .getResultForIdentifier(SignInTask.ID_EMAIL);
            String password = (String) result.getStepResult(OnboardingTask.SignInStepIdentifier)
                    .getResultForIdentifier(SignInTask.ID_PASSWORD);

            // TODO find better way to tell the difference between sign in and needs verification
            if(email == null || password == null)
            {

                startMainActivity();
            }
            else
            {
                Intent intent = new Intent(this, EmailVerificationActivity.class);
                intent.putExtra(EmailVerificationActivity.EXTRA_EMAIL, email);
                intent.putExtra(EmailVerificationActivity.EXTRA_PASSWORD, password);
                startActivity(intent);
            }

        }
        else if(requestCode == REQUEST_CODE_SIGN_UP && resultCode == RESULT_OK)
        {

            finish();

            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String email = (String) result.getStepResult(OnboardingTask.SignUpStepIdentifier)
                    .getResultForIdentifier(SignUpTask.ID_EMAIL);
            String password = (String) result.getStepResult(OnboardingTask.SignUpStepIdentifier)
                    .getResultForIdentifier(SignUpTask.ID_PASSWORD);
            Intent intent = new Intent(this, EmailVerificationActivity.class);
            intent.putExtra(EmailVerificationActivity.EXTRA_EMAIL, email);
            intent.putExtra(EmailVerificationActivity.EXTRA_PASSWORD, password);
            startActivity(intent);
        }
        else if(requestCode == REQUEST_CODE_PASSCODE && resultCode == RESULT_OK)
        {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String passcode = (String) result.getStepResult(OnboardingTask.SignUpPassCodeCreationStepIdentifier)
                    .getResult();
            StorageAccess.getInstance().setPinCode(this, passcode);

            startMainActivity();
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
