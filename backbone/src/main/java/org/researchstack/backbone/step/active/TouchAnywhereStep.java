package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.TouchAnywhereStepLayout;
//import org.researchstack.backbone.ui.step.layout.CountdownStepLayout;

/**
 * Created by David Evans, 2019.
 *
 * The `TouchAnywhereStep` class represents a step that displays a label and a
 * countdown for a time equal to its duration.
 *
 * To use the countdown step, set the `duration` property, incorporate it into a
 * task, and present the task with ViewTaskActivity.
 *
 * The countdown step is used in most of ResearchStacks's predefined active tasks.
 */

public class TouchAnywhereStep extends ActiveStep {

    public static final int DEFAULT_STEP_DURATION = 5;

    /* Default constructor needed for serilization/deserialization of object */
    CountdownStep() {
        super();
    }

    public TouchAnywhereStep(String identifier) {
        super(identifier);
        //setStepDuration(DEFAULT_STEP_DURATION);
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldContinueOnFinish(true);
        setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
    }

    @Override
    public Class getStepLayoutClass() {
        return CountdownStepLayout.class;
    }
}



*********

<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
       xmlns:android="http://schemas.android.com/apk/res/android"
       android:id="@+id/mainLayout"
       xmlns:tools="http://schemas.android.com/tools"
       android:layout_width="fill_parent"
       android:layout_height="fill_parent"
       tools:context=".MainActivity"
       android:id="@+id/mainLayout">  //<--- Provide ID

<TextView
android:id="@+id/textView"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_centerInParent="true"
/>

</RelativeLayout>


//public class MainActivity extends AppCompatActivity {
    
    private RelativeLayout layout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        layout   = (RelativeLayout)findViewById(R.id.mainLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // here you can write code to proceed next step.
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        
    }
    
//    }
