package co.touchlab.researchstack.glue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.ui.PassCodeActivity;
import co.touchlab.researchstack.glue.DataProvider;
import co.touchlab.researchstack.glue.ObservableUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;

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
    }

    @Override
    protected void onDataReady()
    {
        super.onDataReady();
        DataProvider dataProvider = ResearchStack.getInstance().getDataProvider();

        dataProvider.initialize(this)
                .compose(ObservableUtils.applyDefault())
                .subscribe(response -> {
                    if(dataProvider.isSignedIn(SplashActivity.this))
                    {
                        launchMainActivity();
                    }
                    else
                    {
                        launchOnboardingActivity();
                    }

                    finish();
                });
    }

    @Override
    protected void onDataAuth(PinCodeConfig config)
    {
        super.onDataReady();
        launchOnboardingActivity();
        finish();
    }

    @Override
    protected void onDataFailed()
    {
        super.onDataFailed();
        finish();
    }

    @Deprecated //TODO Not sure if we are going to use this ever?
    private void showErrorScreen(Throwable error)
    {
        error.printStackTrace();
        Toast.makeText(SplashActivity.this, "Error when initializing app", Toast.LENGTH_LONG)
                .show();
    }

    private void launchOnboardingActivity()
    {
        startActivity(new Intent(this, OnboardingActivity.class));
    }

    private void launchMainActivity()
    {
        startActivity(new Intent(this, MainActivity.class));
    }
}
