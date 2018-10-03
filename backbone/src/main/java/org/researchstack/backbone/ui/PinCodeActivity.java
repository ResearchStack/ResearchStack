package org.researchstack.backbone.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.R;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.PasscodeStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.FingerprintStepLayout;
import org.researchstack.backbone.ui.views.PinCodeLayout;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepLayoutHelper;
import org.researchstack.backbone.utils.ThemeUtils;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PinCodeActivity extends AppCompatActivity implements StorageAccessListener {
    private PinCodeLayout pinCodeLayout;
    private FingerprintStepLayout fingerprintLayout;
    private Action1<Boolean> toggleKeyboardAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogExt.i(getClass(), "logAccessTime()");
        StorageAccess.getInstance().logAccessTime();

        storageAccessUnregister();
        if(pinCodeLayout != null && ViewCompat.isAttachedToWindow(pinCodeLayout)) {
            getWindowManager().removeView(pinCodeLayout);
        }
        if(fingerprintLayout != null) {
            fingerprintLayout.stopListening();
            if (ViewCompat.isAttachedToWindow(fingerprintLayout)) {
                getWindowManager().removeView(fingerprintLayout);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestStorageAccess();
    }

    protected void requestStorageAccess() {
        LogExt.i(getClass(), "requestStorageAccess()");
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccessRegister();
        storageAccess.requestStorageAccess(this);
    }

    protected void storageAccessRegister() {
        LogExt.i(getClass(), "storageAccessRegister()");
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccess.register(this);
    }

    protected void storageAccessUnregister() {
        LogExt.i(getClass(), "storageAccessUnregister()");
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccess.unregister(this);
    }

    @Override
    public void onDataReady() {
        LogExt.i(getClass(), "onDataReady()");

        storageAccessUnregister();

        // this fixes the race condition where fragments from the viewpager weren't created yet
        // need a more permanent solution for notifying fragments of onDataReady() after creation
        new Handler().post(() -> {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                LogExt.i(getClass(),
                        "Fragments found on stack. Checking for StorageAccessListener.");

                for (Fragment fragment : fragments) {
                    if (fragment instanceof StorageAccessListener) {
                        LogExt.i(getClass(), "Notifying " + fragment.getClass().getSimpleName() +
                                " of onDataReady");

                        ((StorageAccessListener) fragment).onDataReady();
                    }
                }
            }
        });
    }

    @Override
    public void onDataFailed() {
        LogExt.e(getClass(), "onDataFailed()");

        storageAccessUnregister();

        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        if (fragments != null) {
            LogExt.i(getClass(), "Fragments found on stack. Checking for StorageAccessListener.");

            for (Fragment fragment : fragments) {
                if (fragment instanceof StorageAccessListener) {
                    LogExt.i(getClass(), "Notifying " + fragment.getClass().getSimpleName() +
                            " of onDataFailed");

                    ((StorageAccessListener) fragment).onDataFailed();
                }
            }
        }
    }

    @Override
    public void onDataAuth() {
        LogExt.e(getClass(), "onDataAuth()");
        storageAccessUnregister();

        if (StorageAccess.getInstance().usesFingerprint(this)) {
            initFingerprintLayout();
        } else  {
            initPincodeLayout();
        }
    }

    private void initFingerprintLayout() {
        int theme = ThemeUtils.getPassCodeTheme(this);
        fingerprintLayout = new FingerprintStepLayout(new ContextThemeWrapper(this, theme));
        fingerprintLayout.setBackgroundColor(Color.WHITE);

        PasscodeStep step = new PasscodeStep("FingerprintStep", null, null);
        step.setUseFingerprint(true);
        fingerprintLayout.initialize(step, null);
        fingerprintLayout.setCallbacks(new StepCallbacks() {
            @Override
            public void onSaveStep(int action, Step step, StepResult result) {
                // is the way the FingerprintStepLayout signals that we should end the activity
                if (action == ACTION_END) {
                    finish();
                } else {
                    // Move to the next state, which signals a successful data auth
                    transitionToNextState();
                }
            }

            @Override
            public void onCancelStep() {
                // the cancel step signals to the pin code activity the FingerprintStepLayout needs setup again
                signOut();
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        getWindowManager().addView(fingerprintLayout, params);
    }

    private void initPincodeLayout() {
        // Show pincode layout
        PinCodeConfig config = StorageAccess.getInstance().getPinCodeConfig();

        int theme = ThemeUtils.getPassCodeTheme(this);
        pinCodeLayout = new PinCodeLayout(new ContextThemeWrapper(this, theme));
        pinCodeLayout.setBackgroundColor(Color.WHITE);

        pinCodeLayout.getForgotPasscodeButton().setVisibility(View.VISIBLE);
        pinCodeLayout.getForgotPasscodeButton().setOnClickListener(this::forgotPasscodeClicked);

        int errorColor = getResources().getColor(R.color.rsb_error);

        TextView summary = (TextView) pinCodeLayout.findViewById(R.id.text);
        EditText pincode = (EditText) pinCodeLayout.findViewById(R.id.pincode);

        toggleKeyboardAction = enable -> {
            pincode.setEnabled(enable);
            pincode.setText("");
            pincode.requestFocus();
            if (enable) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(pincode, InputMethodManager.SHOW_FORCED);
            }
        };

        RxTextView.textChanges(pincode).map(CharSequence::toString).doOnNext(pin -> {
            if (summary.getCurrentTextColor() == errorColor) {
                summary.setTextColor(ThemeUtils.getTextColorPrimary(PinCodeActivity.this));
                pinCodeLayout.resetSummaryText();
            }
        }).filter(pin -> pin != null && pin.length() == config.getPinLength()).doOnNext(pin -> {
            pincode.setEnabled(false);
            pinCodeLayout.showProgress(true);
        }).flatMap(pin -> Observable.fromCallable(() -> {
            StorageAccess.getInstance().authenticate(PinCodeActivity.this, pin);
            return true;
        }).compose(ObservableUtils.applyDefault()).doOnError(throwable -> {
            toggleKeyboardAction.call(true);
            throwable.printStackTrace();
            summary.setText(R.string.rsb_pincode_enter_error);
            summary.setTextColor(errorColor);
            pinCodeLayout.showProgress(false);
        }).onErrorResumeNext(throwable1 -> {
            return Observable.empty();
        })).subscribe(success -> {
            if (!success) {
                toggleKeyboardAction.call(true);
            } else {
                transitionToNextState();
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        getWindowManager().addView(pinCodeLayout, params);

        // Show keyboard, needs to be delayed, not sure why
        pinCodeLayout.postDelayed(() -> toggleKeyboardAction.call(true), 300);
    }

    /**
     * Since all data in the app is protected by a passcode, we must remove all the data
     * that currently exists, so that we can set the user up with a new passcode
     *
     * This alert dialog should provide sufficient warning to the user before all their local data is removed
     *
     * @param v button that was tapped
     */
    public void forgotPasscodeClicked(View v) {
        new AlertDialog.Builder(this).setTitle(R.string.rsb_reset_passcode)
                .setMessage(R.string.rsb_reset_passcode_message)
                .setCancelable(false)
                .setPositiveButton(R.string.rsb_log_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOut();
                    }
                })
                .setNegativeButton(R.string.rsb_cancel, null)
                .show();
    }

    private void signOut() {
        // Signs the user out of the app, so they have to start from scratch
        // Only gives a callback to response on success, the rest is handled by StepLayoutHelper
        DataProvider.getInstance().signOut(this).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<DataResponse>() {
            @Override
            public void call(DataResponse response) {
                if (!PinCodeActivity.this.isFinishing()) {
                    transitionToNextState();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (!PinCodeActivity.this.isFinishing()) {
                    new AlertDialog.Builder(PinCodeActivity.this)
                            .setMessage(throwable.getLocalizedMessage())
                            .setPositiveButton(getString(R.string.rsb_ok), null)
                            .create().show();
                }
            }
        });
    }

    /**
     * By removing the pincode layout and re-requesting storage access, we force the
     * activity to re-evaluate its pincode state and move on to the next screen
     */
    private void transitionToNextState() {
        if (pinCodeLayout != null) {
            getWindowManager().removeView(pinCodeLayout);
            pinCodeLayout = null;
        }

        if (fingerprintLayout != null && ViewCompat.isAttachedToWindow(fingerprintLayout)) {
            getWindowManager().removeView(fingerprintLayout);
            fingerprintLayout = null;
        }

        // authenticate() no longer calls notifyReady(), call this after auth
        requestStorageAccess();
    }
}
