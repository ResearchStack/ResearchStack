package co.touchlab.researchstack.core.ui;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.StorageAccess;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.core.storage.file.auth.AuthStorageAccessListener;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.ui.views.PinCodeLayout;
import co.touchlab.researchstack.core.utils.ObservableUtils;
import co.touchlab.researchstack.core.utils.ThemeUtils;
import co.touchlab.researchstack.core.utils.UiThreadContext;
import rx.Observable;
import rx.functions.Action1;

public class PinCodeActivity extends AppCompatActivity
{


    private View             pinCodeLayout;
    private Action1<Boolean> toggleKeyboardAction;

    AuthStorageAccessListener storageAccessListener = new AuthStorageAccessListener<PinCodeConfig>()
    {
        @Override
        public void dataReady()
        {
            onDataReady();
        }

        @Override
        public void dataAccessError()
        {
            onDataFailed();
        }

        @Override
        public void dataAuth(PinCodeConfig config)
        {
            onDataAuth(config);
        }
    };

    private void storageAccessRegister()
    {
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccess.register(storageAccessListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        LogExt.i(getClass(), "logAccessTime()");
        StorageAccess.getInstance().logAccessTime();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        initStorageAccess();

        //        if(NewStorageManager.getInstance() instanceof AuthDataAccess)
        //        {
        //            LogExt.i(getClass(), "checkAutoLock()");
        //            ((AuthDataAccess) NewStorageManager.getInstance())
        //                    .checkAutoLock(this);
        //        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        storageAccessUnregister();
    }

    private void storageAccessUnregister()
    {
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccess.unregister(storageAccessListener);
    }

    private void initStorageAccess()
    {
        LogExt.i(getClass(), "initStorageAccess()");
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccessRegister();
        storageAccess.initStorageAccess(this);
    }

    protected void onDataReady()
    {
        LogExt.i(getClass(), "onDataReady()");
        storageAccessUnregister();
    }

    protected void onDataFailed()
    {
        LogExt.e(getClass(), "onDataFailed()");
        storageAccessUnregister();
    }


    //TODO Create Third PinCode layout, or refactor and use PinCodeLayout,
    // and move following code within there.
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        PinCodeConfig config = StorageAccess.getInstance().getPinCodeConfig();

        pinCodeLayout = new PinCodeLayout(this);
        pinCodeLayout.setBackgroundColor(Color.WHITE);
        pinCodeLayout.setVisibility(View.GONE);

        int errorColor = getResources().getColor(R.color.error);

        TextView summary = (TextView) pinCodeLayout.findViewById(R.id.text);
        EditText pincode = (EditText) pinCodeLayout.findViewById(R.id.pincode);

        toggleKeyboardAction = enable -> {
            pincode.setEnabled(enable);
            pincode.setText("");
            pincode.requestFocus();
            if(enable)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(pincode, InputMethodManager.SHOW_FORCED);
            }
        };

        RxTextView.textChanges(pincode)
                .map(CharSequence:: toString)
                .doOnNext(pin -> {
                    if(summary.getCurrentTextColor() == errorColor)
                    {
                        // TODO Figure out a better way of handling if we are in an error state. Its probably
                        // better to use the views state and set enabled/disabled instead
                        summary.setTextColor(ThemeUtils.getTextColorPrimary(PinCodeActivity.this));
                        summary.setText(R.string.rsc_pincode_enter_summary);
                    }
                })
                .filter(pin -> pin != null && pin.length() == config.getPinLength())
                .doOnNext(pin -> pincode.setEnabled(false)).flatMap(pin -> {
            return Observable.create(subscriber -> {
                UiThreadContext.assertBackgroundThread();

                storageAccessRegister();
                ((AuthDataAccess) StorageAccess.getInstance()).authenticate(PinCodeActivity.this,
                        pin);
                subscriber.onNext(true);
            }).compose(ObservableUtils.applyDefault()).doOnError(throwable -> {
                storageAccessUnregister();
                toggleKeyboardAction.call(true);
                summary.setText(R.string.rsc_pincode_enter_error);
                summary.setTextColor(errorColor);
            }).onErrorResumeNext(throwable1 -> {
                return Observable.empty();
            });
        })
                .subscribe(success -> {
                    if(! (boolean) success)
                    {
                        toggleKeyboardAction.call(true);
                    }
                    else
                    {
                        pinCodeLayout.setVisibility(View.GONE);
                    }
                });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        getWindowManager().addView(pinCodeLayout, params);
    }

    protected void onDataAuth(PinCodeConfig config)
    {
        LogExt.e(getClass(), "onDataAuth()");
        storageAccessUnregister();

        // Show pincode layout
        pinCodeLayout.setVisibility(View.VISIBLE);

        // TODO figure out why keyboard wont show without delay
        // Show keyboard
        pinCodeLayout.postDelayed(() -> toggleKeyboardAction.call(true), 300);
    }
}
