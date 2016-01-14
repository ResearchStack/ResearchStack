package co.touchlab.researchstack.core.storage.file.aes;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.R;
import rx.Subscription;

public class PassCodeDialogBuilder extends android.support.v7.app.AlertDialog.Builder
{
    private Handler handler = new Handler(Looper.getMainLooper());

    private PassCodeStateListener passCodeStateListener;
    private Subscription          textChangeSub;

    private PassCodeState existingState;
    private PassCodeState creationState;
    private PassCodeState confirmState;

    private final LayoutInflater inflater;

    private TextView titleView;
    private EditText editText;

    public PassCodeDialogBuilder(Context context)
    {
        super(context);
        inflater = LayoutInflater.from(context);
    }

    public PassCodeDialogBuilder(Context context, int themeResId)
    {
        super(context, themeResId);
        inflater = LayoutInflater.from(context);
    }

    // Force to use Activity context and not getContext so that we can preserve theme.attr that
    // are defined in researcher app
    private void initView()
    {
        int resId = passCodeStateListener.isPassCodeAlphaNumeric()
                ? R.layout.dialog_pin_entry_alphanumeric
                : R.layout.dialog_pin_entry;

        View view = inflater.inflate(resId, null);

        titleView = (TextView) view.findViewById(R.id.title);
        editText = (EditText) view.findViewById(R.id.pinValue);

        setView(view);
    }

    public void initCurrentState()
    {
        final PassCodeState state = getPassCodeState();

        titleView.setText(state.getTitle());

        InputFilter.LengthFilter filter = new InputFilter.LengthFilter(passCodeStateListener.getPassCodeLength());
        editText.setFilters(new InputFilter[] {filter});
        editText.setText("");

        if(textChangeSub != null)
        {
            textChangeSub.unsubscribe();
        }

        textChangeSub = RxTextView.textChanges(editText)
                .filter(charSequence -> charSequence.length() ==
                        passCodeStateListener.getPassCodeLength())
                .subscribe(charSequence -> {
                    handler.postDelayed(() -> {
                        try
                        {
                            Boolean invalidateState = state.getCheckAction()
                                    .call(charSequence.toString());
                            if(invalidateState)
                            {
                                initCurrentState();
                            }
                        }
                        catch(Exception e)
                        {
                            state.getErrorAction().call(charSequence.toString(), e);
                            initCurrentState();
                        }
                    }, 300);
                });
    }

    @Override
    public android.support.v7.app.AlertDialog create()
    {
        initView();
        initCurrentState();
        return super.create();
    }

    public void setPassCodeStateListener(PassCodeStateListener passCodeStateListener)
    {
        this.passCodeStateListener = passCodeStateListener;
    }

    public PassCodeState getPassCodeState()
    {
        if(passCodeStateListener.isPassCodeExists())
        {
            return existingState;
        }
        else if(passCodeStateListener.isNewPassCodeCreated())
        {
            return confirmState;
        }
        else
        {
            return creationState;
        }
    }

    public void setExistingState(PassCodeState existingState)
    {
        this.existingState = existingState;
    }

    public void setCreationState(PassCodeState creationState)
    {
        this.creationState = creationState;
    }

    public void setConfirmState(PassCodeState confirmState)
    {
        this.confirmState = confirmState;
    }

    public interface PassCodeStateListener
    {
        boolean isPassCodeAlphaNumeric();

        boolean isPassCodeExists();

        boolean isNewPassCodeCreated();

        int getPassCodeLength();
    }
}
