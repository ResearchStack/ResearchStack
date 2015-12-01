package co.touchlab.researchstack.glue.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStackApplication;
import co.touchlab.researchstack.glue.common.model.StudyOverviewModel;
import co.touchlab.researchstack.glue.common.task.SignInTask;
import co.touchlab.researchstack.glue.common.task.SignUpTask;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.glue.ui.adapter.OnboardingPagerAdapter;
import co.touchlab.researchstack.glue.ui.views.PageIndicator;
import co.touchlab.researchstack.glue.utils.JsonUtils;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class OnboardingActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_onboarding);

        StudyOverviewModel model = parseStudyOverviewModel();
        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this, model.getQuestions());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
//      TODO  pager.setPageTransformer();
//      TODO  pager.setPageMargin();

        final PageIndicator indicator = (PageIndicator) findViewById(R.id.pager_indicator);
        indicator.removeAllMarkers(true);
        indicator.addMarkers(adapter.getCount(),
                             R.drawable.ic_pageindicator_current_dark,
                             R.drawable.ic_pageindicator_default_dark,
                             true);

        pager.clearOnPageChangeListeners();
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                indicator.setActiveMarker(position);
            }
        });
    }

    //TODO Read on main thread for intense UI blockage.
    private StudyOverviewModel parseStudyOverviewModel()
    {
        int fileResId = ResearchStackApplication.getInstance().getStudyOverviewResourceId();
        return JsonUtils.loadClassFromRawJson(OnboardingActivity.this, StudyOverviewModel.class, fileResId);
    }

    public void onSignUpClicked(View view)
    {
        SignUpTask task = new SignUpTask();
        startActivity(ViewTaskActivity.newIntent(this, task));
    }

    public void onSignInClicked(View view)
    {
        SignInTask task = new SignInTask();
        startActivity(ViewTaskActivity.newIntent(this,
                task));
    }
}
