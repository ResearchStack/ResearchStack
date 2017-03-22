package org.researchstack.backbone.utils;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.View;

import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.AlertFrameLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by TheMDP on 1/16/17.
 */

public class StepLayoutHelper {

    @NonNull
    @MainThread
    public static  StepLayout createLayoutFromStep(Step step, Context context)
    {
        try
        {
            Class cls = step.getStepLayoutClass();
            Constructor constructor = cls.getConstructor(Context.class);
            return (StepLayout) constructor.newInstance(context);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
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
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(dataResponse -> {
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
