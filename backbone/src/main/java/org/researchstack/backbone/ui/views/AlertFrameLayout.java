package org.researchstack.backbone.ui.views;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.R;

import java.lang.ref.WeakReference;

import rx.Observable;

/**
 * Created by TheMDP on 1/16/17.
 */

public class AlertFrameLayout extends FrameLayout {
    protected AlertDialog alertDialog;
    protected ProgressDialog progressDialog;

    public AlertFrameLayout(Context context) {
        super(context);
    }

    public AlertFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlertFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AlertFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Helper method for ProfileSteps that need to make calls to the web
     * Uses default localization of "Loading..."
     */
    public void showLoadingDialog() {
        showLoadingDialog(getContext().getString(R.string.rsb_loading_ellipses));
    }

    /**
     * Helper method for ProfileSteps that need to make calls to the web
     */
    public void showLoadingDialog(String title) {
        if (getContext() == null) {
            return;
        }
        hideLoadingDialog();  // just in case these are showing
        hideAlertDialog();
        progressDialog = ProgressDialog.show(getContext(), "", title);
    }

    /**
     * Helper method for ProfileSteps that need to make calls to the web
     */
    public void hideLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Helper method for showing an error alert
     * @param message message that will be show with alert
     */
    public void showOkAlertDialog(String message) {
        if (getContext() == null) {
            return;
        }
        hideLoadingDialog();  // just in case these are showing
        hideAlertDialog();
        alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(getContext().getString(R.string.rsb_ok), null)
                .create();
        alertDialog.show();
    }

    public void hideAlertDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    /**
     * @param observable that is performing a web call, or asynchronous operation
     * @param viewPerforming the view that is making the observable call
     * @param callback will be invoked if the observable is invoked, and the view is in a valid state
     */
    @MainThread
    public static void safePerform(
            Observable<DataResponse> observable,
            View viewPerforming,
            final WebCallback callback)
    {
        final WeakReference<View> weakView = new WeakReference<>(viewPerforming);
        observable.subscribe(dataResponse -> {
            // Controls canceling an observable perform through weak reference to the view
            if (weakView == null || weakView.get() == null || weakView.get().getContext() == null) {
                return; // no callback
            }
            callback.onSuccess(dataResponse);
        }, throwable -> {
            // Controls canceling an observable perform through weak reference to the view
            if (weakView == null || weakView.get() == null || weakView.get().getContext() == null) {
                return; // no callback
            }
            callback.onFail(throwable);
        });
    }

    /**
     * This is the same as safePerform except all loading dialogs and error dialogs are
     * shown automatically by this method
     *
     * @param observable that is performing a web call, or asynchronous operation
     * @param viewPerforming the view that is making the observable call
     * @param callback will be invoked if the observable is invoked, and the view is in a valid state
     */
    @MainThread
    public static void safePerformWithAlerts(
            Observable<DataResponse> observable,
            AlertFrameLayout viewPerforming,
            final WebSuccessCallback callback)
    {
        viewPerforming.showLoadingDialog();
        final WeakReference<AlertFrameLayout> weakView = new WeakReference<>(viewPerforming);
        safePerform(observable, viewPerforming, new WebCallback() {
            @Override
            public void onSuccess(DataResponse response) {
                weakView.get().hideLoadingDialog();
                if (response.isSuccess()) {
                    callback.onSuccess(response);
                } else {
                    weakView.get().showOkAlertDialog(response.getMessage());
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                weakView.get().hideLoadingDialog();
                weakView.get().showOkAlertDialog(throwable.getMessage());
            }
        });
    }

    /**
     * This is the same as safePerform except all loading dialogs are shown automatically
     *
     * @param observable that is performing a web call, or asynchronous operation
     * @param viewPerforming the view that is making the observable call
     * @param callback will be invoked if the observable is invoked, and the view is in a valid state
     */
    @MainThread
    public static void safePerformWithOnlyLoadingAlerts(
            Observable<DataResponse> observable,
            AlertFrameLayout viewPerforming,
            final WebCallback callback)
    {
        viewPerforming.showLoadingDialog();
        final WeakReference<AlertFrameLayout> weakView = new WeakReference<>(viewPerforming);
        safePerform(observable, viewPerforming, new WebCallback() {
            @Override
            public void onSuccess(DataResponse response) {
                weakView.get().hideLoadingDialog();
                callback.onSuccess(response);
            }

            @Override
            public void onFail(Throwable throwable) {
                weakView.get().hideLoadingDialog();
                callback.onFail(throwable);
            }
        });
    }

    public interface WebCallback {
        void onSuccess(DataResponse response);
        void onFail(Throwable throwable);
    }

    public interface WebSuccessCallback {
        void onSuccess(DataResponse response);
    }
}
