package co.touchlab.touchkit.rk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.StudyOverviewModel;
import co.touchlab.touchkit.rk.common.task.SignUpTask;

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
        pager.setOffscreenPageLimit(5);
        pager.setAdapter(adapter);

//      TODO  pager.setPageTransformer();
//      TODO  pager.setPageMargin();
    }

    //TODO Read on main thread for intense UI blockage.
    private StudyOverviewModel parseStudyOverviewModel()
    {

        Gson gson = new GsonBuilder().setDateFormat("MMM yyyy").create();
        InputStream stream = getResources().openRawResource(R.raw.study_overview);
        Reader reader = null;
        try
        {
            reader = new InputStreamReader(stream, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw  new RuntimeException(e);
        }

        return gson.fromJson(reader, StudyOverviewModel.class);
    }

    public void onSignUpClicked(View view)
    {
        SignUpTask task = new SignUpTask();
        startActivity(ViewTaskActivity.newIntent(this,
                task));
    }

    public void onSignInClicked(View view)
    {
        startActivity(new Intent(this, SignInActivity.class));
    }
}
