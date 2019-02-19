package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.TouchAnywhereStepLayout;

/**
 * Created by David Evans, 2019.
 *
 * The `TouchAnywhereStep` class represents a step that enables the user to begin the task by touching anywhere on the screen.
 *
 */

public class TouchAnywhereStep extends ActiveStep {

    TouchAnywhereStep() {
        super();
    }

    public TouchAnywhereStep(String identifier) {
        super(identifier);

        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldContinueOnFinish(true);

    }

    {
        private RelativeLayout layout;
            
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
                
            layout   = (RelativeLayout)findViewById(R.id.mainLayout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}

