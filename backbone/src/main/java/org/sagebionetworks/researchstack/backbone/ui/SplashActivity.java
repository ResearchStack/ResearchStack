package org.sagebionetworks.researchstack.backbone.ui;

import android.content.Intent;
import android.os.Bundle;

import org.sagebionetworks.researchstack.backbone.StorageAccess;
import org.sagebionetworks.researchstack.backbone.utils.ObservableUtils;
import org.sagebionetworks.researchstack.backbone.AppPrefs;
import org.sagebionetworks.researchstack.backbone.DataProvider;
import org.sagebionetworks.researchstack.backbone.notification.TaskAlertReceiver;


public class SplashActivity extends PinCodeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDataReady() {
        super.onDataReady();
        // Init all notifications
        sendBroadcast(new Intent(TaskAlertReceiver.ALERT_CREATE_ALL));

        DataProvider.getInstance()
                .initialize(this)
                .compose(ObservableUtils.applyDefault())
                .subscribe(response -> {

                    if(AppPrefs.getInstance().isOnboardingComplete() ||
                      (DataProvider.getInstance().isSignedIn(this) && DataProvider.getInstance().isConsented()))
                    {
                        launchMainActivity();
                    } else {
                        launchOnboardingActivity();
                    }

                    finish();
                });
    }

    @Override
    public void onDataAuth() {
        if (StorageAccess.getInstance().hasPinCode(this)) {
            super.onDataAuth();
        } else // allow them through to onboarding if no pincode
        {
            onDataReady();
        }
    }

    @Override
    public void onDataFailed() {
        super.onDataFailed();
        finish();
    }

    protected void launchOnboardingActivity() {
        // TODO: this shouldnt be hardcoded
        // TODO: consider an OnboardingManager class like iOS
        startActivity(new Intent(this, OverviewActivity.class));
    }

    protected void launchMainActivity() {
        // TODO: this shouldnt be hardcoded
        // TODO: consider an OnboardingManager class like iOS
        startActivity(new Intent(this, MainActivity.class));
    }
}
