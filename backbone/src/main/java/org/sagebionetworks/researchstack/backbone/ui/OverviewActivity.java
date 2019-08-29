package org.sagebionetworks.researchstack.backbone.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sagebionetworks.researchstack.backbone.DataProvider;
import org.sagebionetworks.researchstack.backbone.StorageAccess;
import org.sagebionetworks.researchstack.backbone.onboarding.OnboardingTaskType;
import org.sagebionetworks.researchstack.backbone.result.TaskResult;
import org.sagebionetworks.researchstack.backbone.task.OrderedTask;
import org.sagebionetworks.researchstack.backbone.utils.ResUtils;
import org.sagebionetworks.researchstack.backbone.utils.TextUtils;
import org.sagebionetworks.researchstack.backbone.AppPrefs;
import org.sagebionetworks.researchstack.backbone.R;
import org.sagebionetworks.researchstack.backbone.ResearchStack;
import org.sagebionetworks.researchstack.backbone.ResourceManager;
import org.sagebionetworks.researchstack.backbone.TaskProvider;
import org.sagebionetworks.researchstack.backbone.UiManager;
import org.sagebionetworks.researchstack.backbone.model.StudyOverviewModel;
import org.sagebionetworks.researchstack.backbone.onboarding.OnboardingManager;
import org.sagebionetworks.researchstack.backbone.step.PassCodeCreationStep;
import org.sagebionetworks.researchstack.backbone.task.OnboardingTask;
import org.sagebionetworks.researchstack.backbone.task.SignInTask;
import org.sagebionetworks.researchstack.backbone.task.SignUpTask;
import org.sagebionetworks.researchstack.backbone.ui.adapter.OnboardingPagerAdapter;

/**
 * OverviewActivity is the landing page for a user who is not signed up or signed in
 * it gives an overview of the study, as well as buttons to sign in or sign up
 */
public class OverviewActivity extends PinCodeActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_SIGN_UP = 21473;
    public static final int REQUEST_CODE_SIGN_IN = 31473;
    public static final int REQUEST_CODE_PASSCODE = 41473;
    private View pagerFrame;
    private View pagerContainer;
    private TabLayout tabStrip;
    private Button skip;
    private Button signUp;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.rsb_activity_onboarding);

        ImageView logoView = (ImageView) findViewById(R.id.layout_studyoverview_landing_logo);
        TextView titleView = (TextView) findViewById(R.id.layout_studyoverview_landing_title);
        TextView subtitleView = (TextView) findViewById(R.id.layout_studyoverview_landing_subtitle);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_studyoverview_main);
        StudyOverviewModel model = parseStudyOverviewModel();

        // The first item is used for the main activity and not the tabbed dialog
        StudyOverviewModel.Question welcomeQuestion = model.getQuestions().remove(0);

        titleView.setText(welcomeQuestion.getTitle());

        if (!TextUtils.isEmpty(welcomeQuestion.getDetails())) {
            subtitleView.setText(welcomeQuestion.getDetails());
        } else {
            subtitleView.setVisibility(View.GONE);
        }

        // add Read Consent option to list and tabbed dialog
        if ("yes".equals(welcomeQuestion.getShowConsent())) {
            StudyOverviewModel.Question consent = new StudyOverviewModel.Question();
            consent.setTitle(getString(R.string.rsb_read_consent_doc));
            consent.setDetails(ResourceManager.getInstance().getConsentHtml().getName());
            model.getQuestions().add(0, consent);
        }

        for (int i = 0; i < model.getQuestions().size(); i++) {
            AppCompatButton button = (AppCompatButton) LayoutInflater.from(this)
                    .inflate(R.layout.rsb_button_study_overview, linearLayout, false);
            button.setText(model.getQuestions().get(i).getTitle());
            // set the index for opening the viewpager to the correct page on click
            button.setTag(i);
            linearLayout.addView(button);
            button.setOnClickListener(this);
        }

        signUp = (Button) findViewById(R.id.intro_sign_up);
        signUp.setOnClickListener(this::onSignUpClicked);  // lamba to call internal method, IDE threw warning until I switched to it
        signIn = (TextView) findViewById(R.id.intro_sign_in);
        signIn.setOnClickListener(this::onSignInClicked);

        skip = (Button) findViewById(R.id.intro_skip);
        skip.setVisibility(UiManager.getInstance().isConsentSkippable() ? View.VISIBLE : View.GONE);
        skip.setOnClickListener(this::onSkipClicked);

        int resId = ResUtils.getDrawableResourceId(this, model.getLogoName());
        logoView.setImageResource(resId);

        pagerContainer = findViewById(R.id.pager_container);
        pagerContainer.setTranslationY(48);
        pagerContainer.setAlpha(0);
        pagerContainer.setScaleX(.9f);
        pagerContainer.setScaleY(.9f);

        pagerFrame = findViewById(R.id.pager_frame);
        pagerFrame.setAlpha(0);
        pagerFrame.setOnClickListener(v -> hidePager());

        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this, model.getQuestions());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        tabStrip = (TabLayout) findViewById(R.id.pager_title_strip);
        tabStrip.setupWithViewPager(pager);
    }

    @Override
    public void onDataAuth() {
        if (StorageAccess.getInstance().hasPinCode(this)) {
            super.onDataAuth();
        } else // allow onboarding if no pincode
        {
            onDataReady();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // We shouldn't be able to get here if the user is signed in and consented,
        // Go to MainActivity instead
        if (DataProvider.getInstance().isSignedIn(this) && DataProvider.getInstance().isConsented()) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private StudyOverviewModel parseStudyOverviewModel()
    {
        return ResourceManager.getInstance().getStudyOverview().create(this);
    }

    @Override
    public void onClick(View v) {
        showPager((int) v.getTag());
    }

    private void showPager(int index) {
        pagerFrame.animate().alpha(1)
                .setDuration(150)
                .withStartAction(() -> pagerFrame.setVisibility(View.VISIBLE))
                .withEndAction(() -> {
                    pagerContainer.animate()
                            .translationY(0)
                            .setDuration(100)
                            .alpha(1)
                            .scaleX(1)
                            .scaleY(1);
                });
        tabStrip.getTabAt(index).select();
        skip.setActivated(true);
        signUp.setActivated(true);

        int colorFrom = ContextCompat.getColor(this, android.R.color.black);
        int colorTo = ContextCompat.getColor(this, android.R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(150);
        colorAnimation.addUpdateListener(animator -> signIn.setTextColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    private void hidePager() {
        pagerContainer.animate()
                .translationY(48)
                .alpha(0)
                .setDuration(100)
                .scaleX(.9f)
                .scaleY(.9f)
                .withEndAction(() -> {
                    pagerFrame.animate()
                            .alpha(0)
                            .setDuration(150)
                            .withEndAction(() -> pagerFrame.setVisibility(View.GONE));
                    skip.setActivated(false);
                    signUp.setActivated(false);
                });

        int colorFrom = ContextCompat.getColor(this, android.R.color.white);
        int colorTo = ContextCompat.getColor(this, android.R.color.black);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(150);
        colorAnimation.addUpdateListener(animator -> signIn.setTextColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    @Override
    public void onBackPressed() {
        if (pagerFrame.getVisibility() == View.VISIBLE) {
            hidePager();
        } else {
            super.onBackPressed();
        }
    }

    public void onSignUpClicked(View view) {
        hidePager();

        OnboardingManager onboardingManager = ResearchStack.getInstance().getOnboardingManager();
        if (onboardingManager != null) {
            // TODO: make sure User is completely signed out
            onboardingManager.launchOnboarding(OnboardingTaskType.REGISTRATION, this);
        } else {
            Log.e(getClass().getSimpleName(), "Old flow is deprecated, please implement OnboardingManager");
            boolean hasPin = StorageAccess.getInstance().hasPinCode(this);
            SignUpTask task = (SignUpTask) TaskProvider.getInstance().get(TaskProvider.TASK_ID_SIGN_UP);
            task.setHasPasscode(hasPin);
            startActivityForResult(SignUpTaskActivity.newIntent(this, task), REQUEST_CODE_SIGN_UP);
        }
    }

    public void onSkipClicked(View view) {
        hidePager();
        boolean hasPasscode = StorageAccess.getInstance().hasPinCode(this);
        if (!hasPasscode) {
            PassCodeCreationStep step = new PassCodeCreationStep(OnboardingTask.SignUpPassCodeCreationStepIdentifier,
                    R.string.rsb_passcode);
            OrderedTask task = new OrderedTask("PasscodeTask", step);
            startActivityForResult(ConsentTaskActivity.newIntent(this, task),
                    REQUEST_CODE_PASSCODE);
        } else {
            skipToMainActivity();
        }
    }

    public void onSignInClicked(View view) {
        hidePager();

        OnboardingManager onboardingManager = ResearchStack.getInstance().getOnboardingManager();
        if (onboardingManager != null) {
            // TODO: make sure User is completely signed out
            onboardingManager.launchOnboarding(OnboardingTaskType.LOGIN, this);
        } else {
            Log.e(getClass().getSimpleName(), "Old flow is deprecated, please implement OnboardingManager");
            boolean hasPasscode = StorageAccess.getInstance().hasPinCode(this);
            SignInTask task = (SignInTask) TaskProvider.getInstance().get(TaskProvider.TASK_ID_SIGN_IN);
            task.setHasPasscode(hasPasscode);
            startActivityForResult(SignUpTaskActivity.newIntent(this, task), REQUEST_CODE_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            finish();

            AppPrefs.getInstance(this).setSkippedOnboarding(false);
            AppPrefs.getInstance(this).setOnboardingComplete(true);

            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String email = (String) result.getStepResult(OnboardingTask.SignInStepIdentifier)
                    .getResultForIdentifier(SignInTask.ID_EMAIL);
            String password = (String) result.getStepResult(OnboardingTask.SignInStepIdentifier)
                    .getResultForIdentifier(SignInTask.ID_PASSWORD);

            if (email == null || password == null) {
                startMainActivity();
            } else {
                Intent intent = new Intent(this, EmailVerificationActivity.class);
                intent.putExtra(EmailVerificationActivity.EXTRA_EMAIL, email);
                intent.putExtra(EmailVerificationActivity.EXTRA_PASSWORD, password);
                startActivity(intent);
            }

        } else if (requestCode == REQUEST_CODE_SIGN_UP && resultCode == RESULT_OK) {

            finish();

            AppPrefs.getInstance(this).setSkippedOnboarding(false);
            AppPrefs.getInstance(this).setOnboardingComplete(true);

            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String email = (String) result.getStepResult(OnboardingTask.SignUpStepIdentifier)
                    .getResultForIdentifier(SignUpTask.ID_EMAIL);
            String password = (String) result.getStepResult(OnboardingTask.SignUpStepIdentifier)
                    .getResultForIdentifier(SignUpTask.ID_PASSWORD);
            Intent intent = new Intent(this, EmailVerificationActivity.class);
            intent.putExtra(EmailVerificationActivity.EXTRA_EMAIL, email);
            intent.putExtra(EmailVerificationActivity.EXTRA_PASSWORD, password);
            startActivity(intent);
        } else if (requestCode == REQUEST_CODE_PASSCODE && resultCode == RESULT_OK) {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String passcode = (String) result.getStepResult(OnboardingTask.SignUpPassCodeCreationStepIdentifier)
                    .getResult();
            StorageAccess.getInstance().createPinCode(this, passcode);

            skipToMainActivity();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void skipToMainActivity() {
        AppPrefs.getInstance(this).setSkippedOnboarding(true);
        startMainActivity();
    }

    private void startMainActivity() {
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
