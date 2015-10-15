package co.touchlab.touchkit.rk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.helpers.LogExt;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Observable.create(subscriber -> {
            initialize();
            subscriber.onNext(true);
            subscriber.onCompleted();
        })
                .throttleWithTimeout(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::launchActivity,
                        this::showErrorScreen);
    }

    private void initialize()
    {
        // initialize stuff for the app
        LogExt.d(getClass(), "initializing");
    }

    private void showErrorScreen(Throwable error)
    {
        error.printStackTrace();
        Toast.makeText(SplashActivity.this,
                "Error when initializing app",
                Toast.LENGTH_LONG)
                .show();
    }

    private void launchActivity(Object item)
    {
        LogExt.d(getClass(), "Launching activity");
        if(isUserSignedIn())
        {
            launchPinActivity();
        }
        else if(isUserSignedUp())
        {
            launchEmailVerificationActivity();
        }
        else
        {
            launchOnboardingActivity();
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

    private void launchPinActivity()
    {
        startActivity(new Intent(this, UserPinActivity.class));
    }

    private boolean isUserSignedUp()
    {
        return false;
    }

    private boolean isUserSignedIn()
    {
        return false;
    }
}
