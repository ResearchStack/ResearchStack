package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.AnyThread;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;

import org.researchstack.backbone.R;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.PasscodeStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.storage.file.KeystoreEncryptionHelper;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.utils.ResUtils;

import javax.crypto.Cipher;

import rx.functions.Action1;

/**
 * Created by TheMDP on 1/31/17.
 *
 * This class can be used as a more convenient and secure method for creating
 * an encryption key that will be used to protect the user's data
 *
 * It works with the FingerprintManager to create a secret key that is backed by the user's fingerprint
 */

@TargetApi(android.os.Build.VERSION_CODES.M) // api 23, or Android 6.0
public class FingerprintStepLayout extends InstructionStepLayout {

    /** Alias for our key in the Android Key Store */
    private static final String KEY_NAME = "fingerprint_researchstack_secret_key";

    /** Shared Prefs to store the initialization vectors */
    private static final String FINGERPRINT_IV_SHARED_PREFS = "FINGERPRINT_IV_SHARED_PREFS";
    private static final String IV_SHARED_PREFS_KEY = "IV_SHARED_PREFS_KEY";

    /** Animation durations can be customized int R.integer */
    private long animTimeFingerprintFrequency;
    private long animTimeTooManyAttemptsDelay;
    private long animTimeErrorMsgDelay;

    // Does not need customized
    private static final long DEFAULT_EXIT_REGISTRATION_DELAY_MILLIS = 20;

    private PasscodeStep fingerprintStep;

    /** Hook into all things fingerprint */
    private FingerprintManagerCompat fingerprintManager;
    /** Holds the cryptography methods for encrypting/decrypting pins */
    private FingerprintManagerCompat.CryptoObject cryptoObject;
    private Cipher cipher;

    /** Used to cancel fingerprint scanning, when this view is detached or no longer valid for any reason */
    private CancellationSignal cancellationSignal;
    boolean mSelfCancelled;
    private Runnable fingerprintAnimationRunnable;

    public FingerprintStepLayout(Context context) {
        super(context);
        init();
    }

    public FingerprintStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FingerprintStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public FingerprintStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        animTimeFingerprintFrequency = getContext().getResources().getInteger(
                R.integer.rsb_config_anim_time_fingerprint_frequency);
        animTimeTooManyAttemptsDelay = getContext().getResources().getInteger(
                R.integer.rsb_config_anim_time_fingerprint_too_many_attempts_delay);
        animTimeErrorMsgDelay = getContext().getResources().getInteger(
                R.integer.rsb_config_anim_time_fingerprint_error_delay);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetStep(step);

        initFingerPrintManager();
        super.initialize(step, result);
        setupSubmitBar();
        initInstructionStep();

        startScanFingerprintAnimation();
    }

    private void initInstructionStep() {
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(getContext().getString(R.string.rsb_fingerprint_title));
        textTextView.setVisibility(View.VISIBLE);
        textTextView.setText(getContext().getString(R.string.rsb_fingerprint_text));
    }

    private void startScanFingerprintAnimation() {
        refreshDetailText(
                getContext().getString(R.string.rsb_fingerprint_hint),
                getContext().getColor(R.color.rsb_hint));

        fingerprintAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                refreshDetailText(
                        getContext().getString(R.string.rsb_fingerprint_hint),
                        getContext().getResources().getColor(R.color.rsb_hint, null));
                refreshImage(ResUtils.ANIMATED_FINGERPRINT, true);
                postDelayed(fingerprintAnimationRunnable, animTimeFingerprintFrequency);
            }
        };
        post(fingerprintAnimationRunnable);
    }

    private void setupSubmitBar() {
        if (isCreationStep()) {
            submitBar.getPositiveActionView().setVisibility(View.GONE);
            submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
            submitBar.setNegativeTitle(R.string.rsb_use_passcode);
            submitBar.setNegativeAction(new Action1() {
                @Override
                public void call(Object o) {
                    showUsePasscodeAlert();
                }
            });
        } else {
            submitBar.getPositiveActionView().setVisibility(View.GONE);
            submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
            submitBar.setNegativeTitle(R.string.rsb_log_out);
            submitBar.setNegativeAction(new Action1() {
                @Override
                public void call(Object o) {
                    showLogoutAlert();
                }
            });
        }
    }

    private void initFingerPrintManager() {
        fingerprintManager = FingerprintManagerCompat.from(getContext());
        // We already have a fingerprint set, and this steplayout will be dismissed once
        // the callbacks are set
        if (isCreationStep() && StorageAccess.getInstance().hasPinCode(getContext())) {
            return;
        }

        // Create the correct cipher depending on if we are doing encryption or decryption
        if (isCreationStep()) {
            cipher = KeystoreEncryptionHelper.initCipherForEncryption(KEY_NAME);
        } else {
            String base64EncryptedPin = StorageAccess.getInstance().getFingerprint(getContext());
            cipher = KeystoreEncryptionHelper.initCipherForDecryption(
                    getContext(), KEY_NAME, base64EncryptedPin, loadIv());
        }

        if (cipher != null) {
            cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
            startListening(cryptoObject);
        } else {
            // A delay is needed so that the Displaying task has time to complete it's
            // view rendering, and can properly handle the callback state
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showUnrecoverableEntryAlert();
                }
            }, animTimeErrorMsgDelay);
        }
    }

    @Override
    protected void validateAndSetStep(Step step) {
        super.validateAndSetStep(step);

        if (!(step instanceof PasscodeStep)) {
            throw new IllegalStateException("FingerprintStepLayout expects a FingerprintStep");
        }

        fingerprintStep = (PasscodeStep) step;
    }

    protected FingerprintManagerCompat.AuthenticationCallback authCallbacks = new FingerprintManagerCompat.AuthenticationCallback() {
        /**
         * This is the error code for when the hardware shuts off due to too many attempts
         * At this point the sensor will not work for some amount of time,
         * so we should show the error message, and then kick the user from the screen
         */
        static final int ERROR_CODE_TOO_MANY_ATTEMPTS = 7;

        /** This is called on another thread for some device manufacturers */
        @Override
        @AnyThread
        public void onAuthenticationError(final int errMsgId, final CharSequence errString) {
            post(new Runnable() {
                @Override
                public void run() {
                    // Callback is not on the main thread, so send it to the main thread
                    removeCallbacks(fingerprintAnimationRunnable);
                    if (!mSelfCancelled) {
                        showError(errString);
                    }
                    if (errMsgId == ERROR_CODE_TOO_MANY_ATTEMPTS) {
                        // post delay to change the text back to hint text
                        removeCallbacks(fingerprintAnimationRunnable);
                        fingerprintAnimationRunnable = new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onSaveStep(StepCallbacks.ACTION_END, fingerprintStep, null);
                            }
                        };
                        postDelayed(fingerprintAnimationRunnable, animTimeTooManyAttemptsDelay);
                    }
                }
            });
        }

        /** This is called on another thread for some device manufacturers */
        @Override
        @AnyThread
        public void onAuthenticationHelp(final int helpMsgId, final CharSequence helpString) {
            post(new Runnable() {
                @Override
                public void run() {
                    // Callback is not on the main thread, so send it to the main thread
                    removeCallbacks(fingerprintAnimationRunnable);
                    showError(helpString);
                }
            });
        }

        /** This is called on another thread for some device manufacturers */
        @Override
        @AnyThread
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            post(new Runnable() {
                @Override
                public void run() {
                    // Callback is not on the main thread, so send it to the main thread
                    removeCallbacks(fingerprintAnimationRunnable);
                    refreshImage(ResUtils.ANIMATED_CHECK_MARK, true);
                    refreshDetailText(
                            getContext().getResources().getString(R.string.rsb_fingerprint_success),
                            getContext().getResources().getColor(R.color.rsb_success, null));

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handleFingerprintSuccess();
                        }
                    }, getResources().getInteger(R.integer.rsb_config_anim_time_check_mark));
                }
            });
        }

        /** This is called on another thread for some device manufacturers */
        @Override
        @AnyThread
        public void onAuthenticationFailed() {
            // Callback is not on the main thread, so send it to the main thread
            post(new Runnable() {
                @Override
                public void run() {
                    removeCallbacks(fingerprintAnimationRunnable);
                    showError(getContext().getResources().getString(R.string.rsb_fingerprint_not_recognized));
                }
            });
        }
    };

    protected void handleFingerprintSuccess() {
        if (isCreationStep()) {
            generateAndEncryptPin();
            saveIv();  // saves the IV for use in decryption
        } else {
            injectPin();
        }
        stopListening();
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, fingerprintStep, null);
    }

    private void showUnrecoverableEntryAlert() {
        // This is one of the only exceptions that means anything to us
        // At this point we know that the user has changed or removed their fingerprint,
        // And we will have to make them login again, same flow as forgot pincode
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.rsb_fingerprint_invalidated)
                .setNegativeButton(R.string.rsb_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callbacks.onCancelStep();
                    }
                })
                .create().show();
    }

    private void generateAndEncryptPin() {
        String rawPin = KeystoreEncryptionHelper.generateSecureRandomPin();
        StorageAccess.getInstance().createPinCode(getContext(), rawPin);

        String encryptedBase64EncodedPin = KeystoreEncryptionHelper.encryptSecret(getContext(), cipher, rawPin);
        StorageAccess.getInstance().setUsesFingerprint(getContext(), encryptedBase64EncodedPin);
    }

    private void injectPin() {
        String encryptedPincode = StorageAccess.getInstance().getFingerprint(getContext());
        String fingerprintPin = KeystoreEncryptionHelper.decryptPin(cipher, encryptedPincode);
        StorageAccess.getInstance().authenticate(getContext(), fingerprintPin);
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!KeystoreEncryptionHelper.isFingerprintAuthAvailable(fingerprintManager)) {
            return;
        }
        cancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        fingerprintManager.authenticate(cryptoObject, 0, cancellationSignal, authCallbacks, null);
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        super.setCallbacks(callbacks);
        if (isCreationStep() && StorageAccess.getInstance().hasPinCode(getContext())) {
            // A delay is needed so that the Displaying task has time to complete it's
            // view rendering, and can properly handle the callback state
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    // We already have a pin code saved, do not let user create another, which will
                    // get everything in a bad state
                    callbacks.onSaveStep(StepCallbacks.ACTION_PREV, fingerprintStep, null);
                }
            }, DEFAULT_EXIT_REGISTRATION_DELAY_MILLIS);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(fingerprintAnimationRunnable);
        stopListening();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Since we have created the fingerprint auth, it has been removed
        if (StorageAccess.getInstance().usesFingerprint(getContext()) &&
            !KeystoreEncryptionHelper.isFingerprintAuthAvailable(fingerprintManager))
        {
            showUnrecoverableEntryAlert();
        }
    }

    public void stopListening() {
        if (cancellationSignal != null) {
            mSelfCancelled = true;
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    private void showError(CharSequence error) {
        removeCallbacks(fingerprintAnimationRunnable);
        refreshDetailText(
                error.toString(),
                getContext().getResources().getColor(R.color.rsb_error, null));
        refreshImage(ResUtils.ERROR_ICON, false);

        // post delay to change the text back to hint text
        postDelayed(fingerprintAnimationRunnable, animTimeErrorMsgDelay);
    }

    private void showUsePasscodeAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.rsb_are_you_sure)
                .setMessage(R.string.rsb_fingerprint_use_passcode)
                .setNegativeButton(R.string.rsb_no, null)
                .setPositiveButton(R.string.rsb_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fingerprintStep.setUseFingerprint(false);
                        callbacks.onSaveStep(StepCallbacks.ACTION_REFRESH, fingerprintStep, null);
                    }
                })
                .create().show();
    }

    private void showLogoutAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.rsb_are_you_sure)
                .setNegativeButton(R.string.rsb_no, null)
                .setPositiveButton(R.string.rsb_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callbacks.onCancelStep();
                    }
                })
                .create().show();
    }

    /**
     * Should only be called after encryption cipher is initialized
     */
    private void saveIv() {
        String base64Iv = Base64.encodeToString(cipher.getIV(), Base64.DEFAULT);
        getContext().getSharedPreferences(FINGERPRINT_IV_SHARED_PREFS, Context.MODE_PRIVATE)
                .edit().putString(IV_SHARED_PREFS_KEY, base64Iv).apply();
    }

    /**
     * Should only be used while decrypting
     * @return the Initialization Vector (IV) used by the encryptor
     */
    private byte[] loadIv() {
        String base64Iv = getContext().getSharedPreferences(FINGERPRINT_IV_SHARED_PREFS, Context.MODE_PRIVATE)
                .getString(IV_SHARED_PREFS_KEY, "");
        return Base64.decode(base64Iv, Base64.DEFAULT);
    }

    private boolean isCreationStep() {
        return fingerprintStep.getStateOrdinal() == PasscodeCreationStepLayout.State.CREATE.ordinal();
    }
}
