package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;

/**
 * TODO Implement
 */
public class SignInScene extends SceneImpl
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
        return new StepResult<Boolean>(stepIdentifier);
    }
}
