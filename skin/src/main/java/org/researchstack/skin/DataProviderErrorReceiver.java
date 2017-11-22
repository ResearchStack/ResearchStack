package org.researchstack.skin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.onboarding.OnboardingTaskType;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.skin.ui.BaseActivity;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by liujoshua on 11/22/2017.
 */
public class DataProviderErrorReceiver extends BroadcastReceiver {

    public static final String ACTION_DATA_PROVIDER_ERROR = "org.researchstack.skin.DataProviderError";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogExt.i(getClass(), "Received intent with action: " + intent.getAction());

        if (AppPrefs.getInstance().skippedOnboarding()) {
            // We don't want to bother a user that has skipped sign-up with the signed out
            // or consent messages. Short-circuiting until we have an approved message to show
            // a user that has skipped.
            return;
        }

        if (!(context instanceof BaseActivity)) {
            LogExt.w(getClass(), "Received by class that is not a BaseActivity");
            return;
        }

        handleInActivity((BaseActivity) context);
    }

    public static void handleErrors(@NonNull Context context) {
        if (!AppPrefs.getInstance().getDataProviderErrors().isEmpty()) {
            LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(new Intent(ACTION_DATA_PROVIDER_ERROR));
        }
    }

    public static void handleError(@NonNull Context context,
                                   @NonNull @DataProvider.Errors String error) {
        checkNotNull(context);
        checkNotNull(error);

        if (!Strings.isNullOrEmpty(error)) {
            AppPrefs.getInstance().addDataProviderError(error);
        }
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(ACTION_DATA_PROVIDER_ERROR));
    }


    @VisibleForTesting
    void handleInActivity(BaseActivity activity) {
        String messageText = null;
        String actionText = null;
        int length = Snackbar.LENGTH_INDEFINITE;

        View.OnClickListener action = null;

        Set<String> errors = AppPrefs.getInstance().getDataProviderErrors();

        String errorToHandle = null;
        if (errors.contains(DataProvider.Errors.ERROR_APP_UPGRADE_REQUIRED)) {
            errorToHandle = DataProvider.Errors.ERROR_APP_UPGRADE_REQUIRED;
            messageText = activity.getString(R.string.rss_network_error_upgrade_app);
            actionText = activity.getString(R.string.rss_network_error_upgrade_app_action);

            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
            playStoreIntent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
            action = v -> activity.startActivity(playStoreIntent);
        } else if (errors.contains(DataProvider.Errors.ERROR_NOT_AUTHENTICATED)) {
            errorToHandle = DataProvider.Errors.ERROR_NOT_AUTHENTICATED;
            messageText = activity.getString(R.string.rss_network_error_sign_in);
            actionText = activity.getString(R.string.rss_network_error_sign_in_action);
            action = v -> ResearchStack.getInstance().getOnboardingManager()
                    .launchOnboarding(OnboardingTaskType.LOGIN, activity);
        } else if (errors.contains(DataProvider.Errors.ERROR_CONSENT_REQUIRED)) {
            errorToHandle = DataProvider.Errors.ERROR_NOT_AUTHENTICATED;
            messageText = activity.getString(R.string.rss_network_error_consent);
            actionText = activity.getString(R.string.rss_network_error_consent_action);
            action = v -> ResearchStack.getInstance().getOnboardingManager()
                    .launchOnboarding(OnboardingTaskType.RECONSENT, activity);
        }

        // Throw up a Snackbar
        View root = activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(root, messageText, length);
        if (action != null) {
            View.OnClickListener finalAction = action;
            String finalErrorToHandle = errorToHandle;

            snackbar.setAction(actionText, v -> {
                finalAction.onClick(v);
                AppPrefs.getInstance().clearDataProviderError(finalErrorToHandle);
            });
        }
        snackbar.getView().setOnClickListener(v -> snackbar.dismiss());
        snackbar.setActionTextColor(ContextCompat.getColor(activity,
                R.color.rss_snackbar_action_color));
        TextView messageView = activity.getSnackBarMessageView(snackbar);
        if (messageView != null) {
            messageView.setTextColor(Color.WHITE);
        }

        snackbar.show();
    }
}
