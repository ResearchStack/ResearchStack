package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.core.result.QuestionResult;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

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
    public void onSceneCreated(View scene)
    {
        //Do Nothing
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<String>>(stepIdentifier);
    }
}
