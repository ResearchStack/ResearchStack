package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;

/**
 * TODO Implement
 */
public class SignInScene extends SceneImpl
{

    public SignInScene(Context context)
    {
        super(context);
    }

    public SignInScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignInScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
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

}
