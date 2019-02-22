package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.TouchAnywhereStep;

/**
 * Created by David Evans, 2019.
 *
 * The TouchAnywhereStepLayout displays instruction text and sets an 'onclicklistener' for the main layout*/

public class TouchAnywhereStepLayout extends ActiveStepLayout {

    private RelativeLayout layout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        layout = (RelativeLayout)findViewById(R.id.rsb_active_step_layout_touch_anywhere);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, null);
            }
        });
        
    }
    
}
