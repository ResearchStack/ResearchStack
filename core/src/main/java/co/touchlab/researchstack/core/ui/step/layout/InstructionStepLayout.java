package co.touchlab.researchstack.core.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

public class InstructionStepLayout extends RelativeLayout implements StepLayout
{
    private SceneCallbacks callbacks;
    private Step           step;

    public InstructionStepLayout(Context context)
    {
        super(context);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        initializeScene();
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(SceneCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    private void initializeScene()
    {
        LayoutInflater.from(getContext())
                      .inflate(R.layout.scene_instruction, this, true);

        // Set Title
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(step.getTitle());

        // Set Summary
        TextView summaryView = (TextView) findViewById(R.id.summary);
        summaryView.setText(step.getText());

        // Set Next
        TextView next = (TextView) findViewById(R.id.next);
        RxView.clicks(next)
              .subscribe(v -> {
                  callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, null);
              });
    }
}
