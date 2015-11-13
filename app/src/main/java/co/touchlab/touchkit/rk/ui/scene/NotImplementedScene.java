package co.touchlab.touchkit.rk.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.scene.Scene;

@Deprecated
public class NotImplementedScene extends Scene
{

    public NotImplementedScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        TextView textView = new TextView(getContext());
        textView.setText("Not Implemented: " + getStep().getIdentifier());
        return textView;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<String>>(stepIdentifier);
    }
}
