package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;


/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, 2019.
 *
 * The TouchAnywhereStepLayout displays instruction text and sets an 'onclicklistener' for the main
 * layout to move to the next step with a single tap of the screen
 *
 * */

public class TouchAnywhereStepLayout extends ActiveStepLayout {

    private RelativeLayout layout;

    public TouchAnywhereStepLayout(Context context) {
        super(context);
    }

    public TouchAnywhereStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchAnywhereStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public TouchAnywhereStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, activeStep, null);
        return false;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);

        LayoutInflater.from(getContext())
                .inflate(R.layout.rsb_step_layout_touch_anywhere, this, true);

        setupOnClickListener();

        // These relate to elements of the linear layout, which can be displayed or not
        titleTextview.setVisibility(View.VISIBLE);
        textTextview.setVisibility(View.VISIBLE);
        timerTextview.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressBarHorizontal.setVisibility(View.GONE);
        submitBar.setVisibility(View.GONE);
    }

    private void setupOnClickListener() {
        layout = findViewById(R.id.rsb_active_step_layout_touch_anywhere);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we can use skip() as we currently just need to move on to the next step
                skip();
            }
        });

    }

}