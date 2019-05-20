package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;


import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;


/**
 * Created by David Evans, Laurence Hurst, 2019.
 *
 * The TouchAnywhereStepLayout displays instruction text and sets an 'onclicklistener' for the main layout
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
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        setupTouchAnywhereViews();
    }

    private void setupTouchAnywhereViews() {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        
        layout = (RelativeLayout)findViewById(R.id.rsb_active_step_layout_touch_anywhere);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, null);
            }
        });
        
    }
    
}
