package co.touchlab.researchstack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import co.touchlab.researchstack.AppPrefs;
import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.helpers.LogExt;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.secure.SecurityProfile;
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
                .throttleWithTimeout(1,
                        TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this :: launchActivity, this :: showErrorScreen);
    }

    private void initialize()
    {
        // initialize stuff for the app
        LogExt.d(getClass(), "initializing");
        ResearchStackApplication.getInstance()
                .loadUser(this);
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
        LogExt.d(getClass(),
                "Launching activity");

        AppPrefs appPrefs = AppPrefs.getInstance(this);
        SecurityProfile securityProfile = ResearchStackApplication.getInstance()
                                                                  .getSecurityProfile();
        if(appPrefs.isAppPinEncoded() && securityProfile.getEncryptionType() != SecurityProfile.EncryptionType.None)
        {
            launchPinActivity();
        }
        else
        {
            User user = ResearchStackApplication.getInstance().getCurrentUser();

            if(user.isSignedUp())
            {
                launchEmailVerificationActivity();
            }
            else
            {
                launchOnboardingActivity();
            }
        }

        finish();
    }

    private void launchOnboardingActivity()
    {
        startActivity(new Intent(this,
                OnboardingActivity.class));
    }

    private void launchEmailVerificationActivity()
    {
        startActivity(new Intent(this,
                EmailVerificationActivity.class));
    }

    private void launchPinActivity()
    {
        startActivity(new Intent(this,
                MainActivity.class));
    }
}
