package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayoutImpl;
import co.touchlab.researchstack.glue.ObservableUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.ui.adapter.TextWatcherAdapter;

/**
 * TODO Implement
 */
public class SignInStepLayout extends StepLayoutImpl<Boolean>
{

    private AppCompatEditText username;
    private AppCompatEditText password;
    private TextView          forgotPassword;

    public SignInStepLayout(Context context)
    {
        super(context);
    }

    public SignInStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignInStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
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

        username = (AppCompatEditText) body.findViewById(R.id.email);
        username.setText("walter@touchlab.co");
        username.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(! TextUtils.isEmpty(username.getError()))
                {
                    username.setError(null);
                }
            }
        });

        password = (AppCompatEditText) body.findViewById(R.id.password);
        password.setText("password");
        password.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(! TextUtils.isEmpty(password.getError()))
                {
                    password.setError(null);
                }
            }
        });
        password.setOnEditorActionListener((v, actionId, event) -> {
            if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE))
            {
                onNextClicked();
                return true;
            }
            return false;
        });

        forgotPassword = (TextView) body.findViewById(R.id.forgot_password);
    }

    @Override
    protected void onNextClicked()
    {
        if(isAnswerValid())
        {
            ResearchStack.getInstance()
                    .getDataProvider()
                    .signIn(username.getText().toString(), password.getText().toString())
                    .compose(ObservableUtils.applyDefault())
                    .subscribe(dataResponse -> {
                        getStepResult().setResult(true);
                        getCallbacks().onSaveStep(SceneCallbacks.ACTION_NEXT,
                                getStep(),
                                getStepResult());
                    }, throwable -> {
                        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    });
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        if(! isEmailValid())
        {
            username.setError(getString(R.string.error_invalid_email));
        }

        if(! isPasswordValid())
        {
            password.setError(getString(R.string.error_invalid_password));
        }

        return TextUtils.isEmpty(username.getError()) && TextUtils.isEmpty(password.getError());
    }

    public boolean isEmailValid()
    {
        CharSequence target = username.getText();
        return ! TextUtils.isEmpty(target) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isPasswordValid()
    {
        CharSequence target = password.getText();
        return ! TextUtils.isEmpty(target);
    }

}
