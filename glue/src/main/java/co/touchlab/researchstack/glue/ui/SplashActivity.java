package co.touchlab.researchstack.glue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.file.aes.AesFileAccess;
import co.touchlab.researchstack.core.ui.PassCodeActivity;

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
        Log.w("asdf", "onDataReady: " + getClass().getSimpleName());
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
        if(ResearchStack.getInstance().storedUserExists())
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
        LogExt.d(getClass(),
                "Launching activity");

        FileAccess fileAccess = StorageManager.getFileAccess();
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
