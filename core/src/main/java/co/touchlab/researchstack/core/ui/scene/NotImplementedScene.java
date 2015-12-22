package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

@Deprecated
public class NotImplementedScene extends TextView implements Scene<Boolean>
{

    private SceneCallbacks callbacks;

    public NotImplementedScene(Context context)
    {
        super(context);
    }

    public NotImplementedScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotImplementedScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        setText("Not Implemented: " + step.getIdentifier());

        setOnClickListener(v -> {
            callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, result);
        });
    }

    @Override
    public View getView()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
