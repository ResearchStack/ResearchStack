package co.touchlab.researchstack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.helpers.LogExt;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.storage.FileAccess;
import co.touchlab.researchstack.common.storage.aes.AesFileAccess;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class SplashActivity extends PassCodeActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> launchActivity(), 1000);
    }

    @Override
    protected void onDataReady()
    {
        super.onDataReady();
        Log.w("asdf", "onDataReady: "+ getClass().getSimpleName());
        /*User user = ResearchStackApplication.getInstance().getCurrentUser();

        if(user != null && user.isSignedIn())
        {
            launchScheduleActivity();
        }
        else if(user != null && user.isSignedUp())
        {
            launchEmailVerificationActivity();
        }
        else
        {
            launchOnboardingActivity();
        }*/

        //TODO: Fix routing
        if(ResearchStackApplication.getInstance().storedUserExists())
        {
            new Handler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    launchScheduleActivity();
                }
            });
        }
        else
        {
            launchOnboardingActivity();
        }

        finish();
    }

    @Override
    protected void onDataFailed()
    {
        super.onDataFailed();
        finish();
    }

    private void showErrorScreen(Throwable error)
    {
        error.printStackTrace();
        Toast.makeText(SplashActivity.this, "Error when initializing app", Toast.LENGTH_LONG)
             .show();
    }

    private void launchActivity()
    {
        LogExt.d(getClass(), "Launching activity");

        FileAccess fileAccess = ResearchStackApplication.getInstance().getFileAccess();
        if(((AesFileAccess)fileAccess).passphraseExists(this))
        {
            initFileAccess();
        }
        else
        {
            launchOnboardingActivity();
            finish();
        }
    }

    private void launchOnboardingActivity()
    {
        startActivity(new Intent(this, OnboardingActivity.class));
    }

    private void launchEmailVerificationActivity()
    {
        startActivity(new Intent(this, EmailVerificationActivity.class));
    }

    private void launchScheduleActivity()
    {
        startActivity(new Intent(this, MainActivity.class));
    }
}
