package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.glue.ObservableUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.task.SignUpTask;
import co.touchlab.researchstack.glue.ui.adapter.TextWatcherAdapter;

public class SignUpStepLayout extends RelativeLayout implements StepLayout
{
    private SceneCallbacks callbacks;

    private StepResult<String> result;
    private Step               step;

    private View              progress;
    private AppCompatEditText email;
    private AppCompatEditText username;
    private AppCompatEditText password;

    public SignUpStepLayout(Context context)
    {
        super(context);
    }

    public SignUpStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;

        View layout = LayoutInflater.from(getContext()).inflate(R.layout.item_sign_up, this, true);

        progress = layout.findViewById(R.id.progress);
        username = (AppCompatEditText) layout.findViewById(R.id.username);

        email = (AppCompatEditText) layout.findViewById(R.id.email);
        email.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(! TextUtils.isEmpty(email.getError()))
                {
                    email.setError(null);
                }
            }
        });
        email.setText("walter@touchlab.co");

        password = (AppCompatEditText) layout.findViewById(R.id.password);
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
                signUp();
                return true;
            }
            return false;
        });
        password.setText("password");

        RxView.clicks(layout.findViewById(R.id.next)).subscribe(v -> {
            signUp();
        });
    }

    private void signUp()
    {
        if(isAnswerValid())
        {
            final String email = this.email.getText().toString();
            String rawUsername = this.username.getText().toString();
            final String username = TextUtils.isEmpty(rawUsername) ? email : rawUsername;
            final String password = this.password.getText().toString();

            progress.animate()
                    .alpha(1)
                    .withStartAction(() -> {
                        progress.setVisibility(View.VISIBLE);
                        progress.setAlpha(0);
                    })
                    .withEndAction(() -> ResearchStack.getInstance()
                            .getDataProvider()
                            .signUp(getContext(), email, username, password)
                            .compose(ObservableUtils.applyDefault())
                            .subscribe(dataResponse -> {
                                // Save Email, Username, and Password in memory
                                result.setResultForIdentifier(SignUpTask.ID_EMAIL, email);
                                result.setResultForIdentifier(SignUpTask.ID_USERNAME, username);
                                result.setResultForIdentifier(SignUpTask.ID_PASSWORD, password);

                                callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, result);
                            }, throwable -> {
                                progress.animate()
                                        .alpha(0)
                                        .withEndAction(() -> progress.setVisibility(View.GONE));

                                // TODO Cast throwable to HttpException -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                                // Convert errorBody to JSON-String, convert json-string to object
                                // (BridgeMessageResponse) and pass BridgeMessageResponse.getMessage()to
                                // toast
                                Toast.makeText(getContext(),
                                        throwable.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }));


        }
    }

    public boolean isAnswerValid()
    {
        if(! isEmailValid())
        {
            email.setError(getResources().getString(R.string.error_invalid_email));
        }

        if(! isPasswordValid())
        {
            password.setError(getResources().getString(R.string.error_invalid_password));
        }

        if(! isUsernameValid())
        {
            username.setError(getResources().getString(R.string.error_invalid_username));
        }

        return TextUtils.isEmpty(email.getError()) &&
                TextUtils.isEmpty(password.getError()) &&
                TextUtils.isEmpty(username.getError());
    }

    public boolean isEmailValid()
    {
        CharSequence target = email.getText();
        return ! TextUtils.isEmpty(target) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isPasswordValid()
    {
        CharSequence target = password.getText();
        return ! TextUtils.isEmpty(target);
    }

    public boolean isUsernameValid()
    {
        CharSequence target = username.getText();
        return TextUtils.isEmpty(target) || target.length() >= 6;
    }


    @Override
    public View getLayout()
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
