package co.touchlab.researchstack.glue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import co.touchlab.researchstack.core.ui.PinCodeActivity;
import co.touchlab.researchstack.core.utils.ObservableUtils;
import co.touchlab.researchstack.glue.DataProvider;
import co.touchlab.researchstack.glue.R;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class SplashActivity extends PinCodeActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        DataProvider.getInstance().initialize(this)
                .compose(ObservableUtils.applyDefault())
                .subscribe(response -> {
                    if(DataProvider.getInstance().isSignedIn(SplashActivity.this))
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
    public void onDataAuth()
    {
        super.onDataAuth();
    }

    @Override
    public void onDataFailed()
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
