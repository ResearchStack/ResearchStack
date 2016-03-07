package org.researchstack.skin.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.model.StudyOverviewModel;
import org.researchstack.skin.step.PassCodeCreationStep;
import org.researchstack.skin.task.OnboardingTask;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.task.SignUpTask;
import org.researchstack.skin.ui.adapter.OnboardingPagerAdapter;
import org.researchstack.skin.utils.JsonUtils;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class OnboardingActivity extends PinCodeActivity implements View.OnClickListener
{
    public static final int REQUEST_CODE_SIGN_UP  = 21473;
    public static final int REQUEST_CODE_SIGN_IN  = 31473;
    public static final int REQUEST_CODE_PASSCODE = 41473;
    private View      pagerFrame;
    private TabLayout tabStrip;
    private Button    skip;
    private Button    signUp;
    private TextView  signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_onboarding);

        ImageView logoView = (ImageView) findViewById(R.id.layout_studyoverview_landing_logo);
        TextView titleView = (TextView) findViewById(R.id.layout_studyoverview_landing_title);
        TextView subtitleView = (TextView) findViewById(R.id.layout_studyoverview_landing_subtitle);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_studyoverview_main);
        StudyOverviewModel model = parseStudyOverviewModel();
        for(int i = 0; i < model.getQuestions().size(); i++)
        {
            AppCompatButton button = (AppCompatButton) LayoutInflater.from(this)
                    .inflate(R.layout.rss_button_study_overview, linearLayout, false);
            button.setText(model.getQuestions().get(i).getTitle());
            // set the index for opening the viewpager to the correct page on click
            button.setTag(i);
            linearLayout.addView(button);
            button.setOnClickListener(this);
        }

        signUp = (Button) findViewById(R.id.intro_sign_up);
        signIn = (TextView) findViewById(R.id.intro_sign_in);

        skip = (Button) findViewById(R.id.intro_skip);
        skip.setVisibility(UiManager.getInstance().isConsentSkippable() ? View.VISIBLE : View.GONE);

        logoView.setImageResource(ResourceManager.getInstance().getLargeLogoDiseaseIcon());

        StudyOverviewModel.Question data = model.getQuestions().get(0);
        titleView.setText(data.getTitle());
        if(! TextUtils.isEmpty(data.getDetails()))
        {
            subtitleView.setText(data.getDetails());
        }
        else
        {
            subtitleView.setVisibility(View.GONE);
        }

        pagerFrame = findViewById(R.id.pager_frame);
        pagerFrame.setOnClickListener(v -> hidePager());
        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this, model.getQuestions());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabStrip = (TabLayout) findViewById(R.id.pager_title_strip);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        tabStrip.setTabsFromPagerAdapter(adapter);
        tabStrip.setupWithViewPager(pager);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        /**
         * TODO This check is a problem -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
         * We can never go "back" from the SignInActivity. When heading back from
         * SignUpTaskActivity, Onboarding activity will register its file-access listener resulting
         * on onDataReady being called. This, in turn, does the check below and calls
         * onSignInClicked, which creates and starts SignUpTaskActivity
         */
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

    @Override
    public void onClick(View v)
    {
        showPager((int) v.getTag());
    }

    private void showPager(int index)
    {
        pagerFrame.setVisibility(View.VISIBLE);
        tabStrip.getTabAt(index).select();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            // TODO change elevation?
            //            skip.setElevation();
            //            signUp.setElevation();
        }
        signIn.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void hidePager()
    {
        pagerFrame.setVisibility(View.GONE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            // TODO change elevation?
            //            skip.setElevation();
            //            signUp.setElevation();
        }

        signIn.setTextColor(getResources().getColor(android.R.color.black));
    }

    @Override
    public void onBackPressed()
    {
        if(pagerFrame.getVisibility() == View.VISIBLE)
        {
            hidePager();
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void onSignUpClicked(View view)
    {
        hidePager();

        boolean hasPin = StorageAccess.getInstance().hasPinCode(this);

        SignUpTask task = (SignUpTask) TaskProvider.getInstance().get(TaskProvider.TASK_ID_SIGN_UP);
        task.setHasPasscode(hasPin);
        startActivityForResult(SignUpTaskActivity.newIntent(this, task), REQUEST_CODE_SIGN_UP);
    }

    public void onSkipClicked(View view)
    {
        hidePager();
        boolean hasPasscode = StorageAccess.getInstance().hasPinCode(this);
        if(! hasPasscode)
        {
            PassCodeCreationStep step = new PassCodeCreationStep(OnboardingTask.SignUpPassCodeCreationStepIdentifier,
                    R.string.rss_passcode);
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
        hidePager();
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
        // Onboarding completion is checked in splash activity. The check allows us to pass through
        // to MainActivity even if we haven't signed in. We want to set this true in every case so
        // the user is really only forced through Onboarding once. If they leave the study, they must
        // re-enroll in Settings, which starts OnboardingActivty.
        AppPrefs.getInstance(this).setOnboardingComplete(true);

        // Start MainActivity w/ clear_top and single_top flags. MainActivity may
        // already be on the activity-task. We want to re-use that activity instead
        // of creating a new instance and have two instance active.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
