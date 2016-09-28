package org.researchstack.skin.ui.layout;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.model.DocumentProperties;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.ConsentSectionModel;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.ui.adapter.TextWatcherAdapter;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SignInStepLayout extends RelativeLayout implements StepLayout
{
    private Context ctx;
    private View               progress;
    private AppCompatEditText  email;
    private AppCompatEditText  password;
    private TextView           forgotPassword;
    private Step               step;
    private StepResult<String> result;
    private StepCallbacks      callbacks;
    private boolean generatePassword;
    private int passwordLength;

    public SignInStepLayout(Context context)
    {
        super(context);
        this.ctx = context;
    }

    public SignInStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.ctx = context;
    }

    public SignInStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.ctx = context;
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result == null ? new StepResult<>(step) : result;

        View layout = LayoutInflater.from(getContext()).inflate(R.layout.rss_layout_sign_in, this, true);

        ConsentSectionModel data = ResourceManager.getInstance()
                .getConsentSections()
                .create(ctx);
        DocumentProperties properties = data.getDocumentProperties();
        generatePassword = properties.generatePassword();
        passwordLength = properties.passwordLenght();

        progress = layout.findViewById(R.id.progress);

        email = (AppCompatEditText) layout.findViewById(R.id.username);
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



        if(generatePassword){
            TableRow passwordrow = (TableRow) layout.findViewById(R.id.passwordrow);
            passwordrow.setVisibility(View.GONE);

            AppCompatTextView forgotPasswrod = (AppCompatTextView) layout.findViewById(R.id.forgot_password);
            forgotPasswrod.setVisibility(View.GONE);
        } else {
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
                    signIn();
                    return true;
                }
                return false;
            });

            forgotPassword = (TextView) layout.findViewById(R.id.forgot_password);
            RxView.clicks(forgotPassword).subscribe(v -> {
                if(! isEmailValid())
                {
                    Toast.makeText(getContext(), R.string.rss_error_invalid_email, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                DataProvider.getInstance()
                        .forgotPassword(getContext(), email.getText().toString())
                        .compose(ObservableUtils.applyDefault())
                        .subscribe(dataResponse -> {
                            Toast.makeText(getContext(), dataResponse.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        });
            });
        }


        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveAction(v -> signIn());
        submitBar.getNegativeActionView().setVisibility(GONE);
    }

    private void signIn()
    {
        if(isAnswerValid())
        {
            String pass = "";
            final String username = this.email.getText().toString();
            if(generatePassword){
                // generate random password
                SecureRandom random = new SecureRandom();
                pass = new BigInteger(passwordLength * 5, random).toString(32);
                pass = String.format("%1$" + passwordLength + "s", pass).replace(' ', 'x');
            } else {
                pass = this.password.getText().toString();
            }
            final String passwordS = pass;

            progress.animate().alpha(1).withStartAction(() -> {
                progress.setVisibility(View.VISIBLE);
                progress.setAlpha(0);
            }).withEndAction(() -> {
                DataProvider.getInstance()
                        .signIn(getContext(), username, passwordS)
                        .compose(ObservableUtils.applyDefault())
                        .subscribe(dataResponse -> {
                            if(dataResponse.isSuccess())
                            {
                                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
                            }
                            else
                            {
                                handleError(dataResponse.getMessage(), username, passwordS);
                            }
                        }, throwable -> {
                            handleError(throwable.getMessage(), username, passwordS);
                        });
            });
        }
    }

    private void handleError(String message, String username, String password)
    {
        progress.animate().alpha(0).withEndAction(() -> progress.setVisibility(View.GONE));

        if(username.equals(DataProvider.getInstance().getUserEmail(getContext())))
        {
            // Sign in returns 404 if they haven't verified email. If the email
            // matches the one they used to sign up, go to verification activity
            result.setResultForIdentifier(SignInTask.ID_EMAIL, username);
            result.setResultForIdentifier(SignInTask.ID_PASSWORD, password);
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
            return;
        }

        // Convert errorBody to JSON-String, convert json-string to object
        // (BridgeMessageResponse) and pass BridgeMessageResponse.getMessage()to
        // toast
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public boolean isAnswerValid()
    {
        if(! isEmailValid())
        {
            email.setError(getResources().getString(R.string.rss_error_invalid_email));
        }

        if(generatePassword)
        {
            return TextUtils.isEmpty(email.getError());
        } else {
            CharSequence target = password.getText();
            if(! TextUtils.isEmpty(target)){
                password.setError(getResources().getString(R.string.rss_error_invalid_password));
            }
            if (passwordLength > 0 && target.length() >= passwordLength) {
                password.setError(getResources().getString(R.string.rss_error_small_password, passwordLength));
            }

            return TextUtils.isEmpty(email.getError()) && TextUtils.isEmpty(password.getError());
        }
    }

    public boolean isEmailValid()
    {
        CharSequence target = email.getText();
        return TextUtils.isValidEmail(target);
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

}
