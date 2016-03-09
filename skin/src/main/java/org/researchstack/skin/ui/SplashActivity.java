package org.researchstack.skin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.file.UnencryptedProvider;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.notification.TaskAlertReceiver;

import rx.Observable;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class SplashActivity extends PinCodeActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        // Init all notifications
        Observable.create(subscriber -> {
            if(StorageAccess.getInstance().getEncryptionProvider() instanceof UnencryptedProvider
                    || StorageAccess.getInstance().hasPinCode(this) )
            {
                subscriber.onNext(null);
            }
        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
            sendBroadcast(new Intent(TaskAlertReceiver.ALERT_CREATE_ALL));
        });


        DataProvider.getInstance()
                .initialize(this)
                .compose(ObservableUtils.applyDefault())
                .subscribe(response -> {

                    if(AppPrefs.getInstance(this).isOnboardingComplete() ||
                            DataProvider.getInstance().isSignedIn(this))
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
