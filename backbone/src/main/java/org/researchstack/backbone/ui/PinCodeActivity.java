package org.researchstack.backbone.ui;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.storage.file.auth.PinCodeConfig;
import org.researchstack.backbone.ui.views.PinCodeLayout;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.ThemeUtils;
import org.researchstack.backbone.utils.UiThreadContext;

import rx.Observable;
import rx.functions.Action1;

public class PinCodeActivity extends AppCompatActivity implements StorageAccessListener
{
    private PinCodeLayout pinCodeLayout;
    private Action1<Boolean> toggleKeyboardAction;

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

        requestStorageAccess();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        storageAccessUnregister();
    }

    private void requestStorageAccess()
    {
        LogExt.i(getClass(), "requestStorageAccess()");
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccessRegister();
        storageAccess.requestStorageAccess(this);
    }

    private void storageAccessRegister()
    {
        LogExt.i(getClass(), "storageAccessRegister()");
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccess.register(this);
    }

    private void storageAccessUnregister()
    {
        LogExt.i(getClass(), "storageAccessUnregister()");
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccess.unregister(this);
    }

    @Override
    public void onDataReady()
    {
        LogExt.i(getClass(), "onDataReady()");
        storageAccessUnregister();
    }

    @Override
    public void onDataFailed()
    {
        LogExt.e(getClass(), "onDataFailed()");
        storageAccessUnregister();
    }

    @Override
    public void onDataAuth()
    {
        LogExt.e(getClass(), "onDataAuth()");
        storageAccessUnregister();

        // Show pincode layout
        pinCodeLayout.setVisibility(View.VISIBLE);

        // TODO figure out why keyboard wont show without delay
        // Show keyboard
        pinCodeLayout.postDelayed(() -> toggleKeyboardAction.call(true), 300);
    }


    //TODO Create Third PinCode layout, or refactor and use PinCodeLayout,
    // and move following code within there.
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // TODO figure out a better way to get/store pin code config
        PinCodeConfig config = StorageAccess.getInstance().getPinCodeConfig();

        //TODO get Pincode theme from activity theme using custom attribute
        pinCodeLayout = new PinCodeLayout(new ContextThemeWrapper(this, R.style.Backbone));
        pinCodeLayout.setBackgroundColor(Color.WHITE);
        pinCodeLayout.setVisibility(View.GONE);

        int errorColor = getResources().getColor(R.color.rsb_error);

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
                        summary.setText(R.string.rsb_pincode_enter_summary);
                    }
                })
                .filter(pin -> pin != null && pin.length() == config.getPinLength())
                .doOnNext(pin -> {
                    pincode.setEnabled(false);
                    pinCodeLayout.showProgress(true);
                })
                .flatMap(pin -> {
                    return Observable.create(subscriber -> {
                        UiThreadContext.assertBackgroundThread();

                        StorageAccess.getInstance().authenticate(PinCodeActivity.this, pin);
                        subscriber.onNext(true);
                    }).compose(ObservableUtils.applyDefault()).doOnError(throwable -> {
                        toggleKeyboardAction.call(true);
                        throwable.printStackTrace();
                        summary.setText(R.string.rsb_pincode_enter_error);
                        summary.setTextColor(errorColor);
                        pinCodeLayout.showProgress(false);
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
                        pinCodeLayout.showProgress(false);
                        // TODO clean this whole auth thing up some more
                        // authenticate() no longer calls notifyReady(), call this after auth
                        requestStorageAccess();
                    }
                });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        getWindowManager().addView(pinCodeLayout, params);
    }
}
