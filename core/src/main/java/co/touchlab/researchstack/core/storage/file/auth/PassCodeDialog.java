package co.touchlab.researchstack.core.storage.file.auth;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.helpers.LogExt;
import rx.functions.Action1;

@Deprecated
public class PassCodeDialog extends android.support.v7.app.AlertDialog
{
    private static final String TAG = PassCodeDialog.class.getSimpleName();

    private Handler handler = new Handler(Looper.getMainLooper());

    private PinCodeConfig config;

    private TextView           titleView;
    private EditText           editText;
    private Action1<String>    authAction;
    private Action1<Throwable> failAction;

    public PassCodeDialog(Context context, PinCodeConfig config, int themeResId)
    {
        super(context, themeResId);
        this.config = config;

        initView(context);
    }

    // Force to use Activity context and not getContext so that we can preserve theme.attr that
    // are defined in researcher app
    private void initView(Context context)
    {
        LogExt.i(getClass(), "getBodyView()");

        LayoutInflater inflater = LayoutInflater.from(context);

        int resId = config.getPinType() == PinCodeConfig.Type.AlphaNumeric
                ? R.layout.dialog_pin_entry_alphanumeric
                : R.layout.dialog_pin_entry;

        View view = inflater.inflate(resId, null);

        titleView = (TextView) view.findViewById(R.id.title);

        editText = (EditText) view.findViewById(R.id.pinValue);

        InputFilter.LengthFilter filter = new InputFilter.LengthFilter(config.getPinLength());
        editText.setFilters(new InputFilter[] {filter});
        editText.setText("");

        // Changed to android implementation, Rx was throwing NPE when calling Object.getClass()
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s != null && s.length() == config.getPinLength())
                {
                    handler.postDelayed(() -> {
                        try
                        {
                            LogExt.i(TAG, "Auth Action Executing");
                            authAction.call(s.toString());

                            LogExt.i(TAG, "Auth Action Success, dismissing dialog");
                            PassCodeDialog.this.dismiss();
                        }
                        catch(Exception e)
                        {
                            LogExt.e(TAG, "Auth Action Failed");
                            editText.setText("");
                            failAction.call(new RuntimeException("Invalid PassCode", e));
                        }
                    }, 300);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        setView(view);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        titleView.setText(title);
    }

    @Override
    public void setTitle(int titleId)
    {
        titleView.setText(titleId);
    }

    public void setAuthAction(Action1<String> action)
    {
        this.authAction = action;
    }

    public void setFailAction(Action1<Throwable> failAction)
    {
        this.failAction = failAction;
    }
}
