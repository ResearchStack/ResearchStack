package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;

/**
 * TODO Implement Function
 */
public class SignUpPermissionsPrimingScene extends Scene
{

    public SignUpPermissionsPrimingScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_permission_priming, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        TextView priming = (TextView) body.findViewById(R.id.priming_body);
//        body.setSummary();
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
