package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.AttributeSet;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.RequireSystemFeatureStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;

import rx.functions.Action1;

/**
 * Created by TheMDP on 2/21/17.
 */

public class RequireSystemFeatureStepLayout extends InstructionStepLayout {

    protected boolean shouldGoNextOnCallbacksSet;
    protected RequireSystemFeatureStep systemFeatureStep;

    public RequireSystemFeatureStepLayout(Context context) {
        super(context);
    }

    public RequireSystemFeatureStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RequireSystemFeatureStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RequireSystemFeatureStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        updateSystemFeatureStatus();
    }

    @Override
    protected void validateAndSetStep(Step step) {
        super.validateAndSetStep(step);

        if (!(step instanceof RequireSystemFeatureStep)) {
            throw new IllegalStateException("RequireSystemFeatureStepLayout only works with RequireSystemFeatureStep");
        }

        systemFeatureStep = (RequireSystemFeatureStep)step;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        super.setCallbacks(callbacks);
        if (shouldGoNextOnCallbacksSet) {
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, systemFeatureStep, null);
        }
    }

    public void updateSystemFeatureStatus() {
        if (systemFeatureStep.getSystemFeature() == RequireSystemFeatureStep.SystemFeature.GPS) {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (callbacks != null) {
                    callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, systemFeatureStep, null);
                } else {
                    shouldGoNextOnCallbacksSet = true;
                }
            } else {
                submitBar.setPositiveTitle(R.string.rsb_enable);
                submitBar.setPositiveAction(new Action1() {
                    @Override
                    public void call(Object o) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getContext().startActivity(gpsOptionsIntent);
                    }
                });
            }
        } else {
            throw new IllegalStateException("No other System feature supported besides GPS at the moment");
        }
    }
}
