package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;

/**
 * TODO Implement
 */
public class SignInScene extends Scene
{

    public SignInScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_sign_in, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        super.onBodyCreated(body);

        AppCompatEditText email = (AppCompatEditText) body.findViewById(R.id.email);
        AppCompatEditText password = (AppCompatEditText) body.findViewById(R.id.password);
        TextView forgotPassword = (TextView) body.findViewById(R.id.forgot_password);
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
