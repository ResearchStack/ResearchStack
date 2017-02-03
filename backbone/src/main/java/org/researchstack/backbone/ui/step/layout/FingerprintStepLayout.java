package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;

import org.researchstack.backbone.R;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.FingerprintStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.utils.ResUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

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

    /** Reference to Android Key Store */
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    /** Alias for our key in the Android Key Store */
    private static final String KEY_NAME = "researchstack_data_key";

    /** Encryption type used for key generation */
    private static final String AES_MODE =
                    KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7;

    private static final String UTF_8    = "UTF-8";

    private static final String SHARED_PREFS_KEY = "FingerprintStepLayoutSharedPrefs";
    private static final String IV_KEY = "IvForDecryption";

    /** Used when generating a random pincode */
    private static final int RANDOM_PINCODE_LENGTH = 32;

    private static final long DEFAULT_FINGERPRINT_ANIMATION_MILLIS = 2000;
    private static final long DEFAULT_TOO_MANYATTEMPTS_MILLIS = 2500;
    private static final long DEFAULT_ERROR_TIMEOUT_MILLIS = 1600;
    private static final long DEFAULT_EXIT_REGISTRATION_DELAY_MILLIS = 20;

    private FingerprintStep fingerprintStep;

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
    }

    public FingerprintStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FingerprintStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public FingerprintStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetStep(step);

        InstructionStep instructionStep = this.step;

        if (instructionStep.getTitle() == null) {
            instructionStep.setTitle(getContext().getString(R.string.rsb_fingerprint_title));
        }

        if (instructionStep.getText() == null) {
            instructionStep.setTitle(getContext().getString(R.string.rsb_fingerprint_text));
        }

        initFingerPrintManager();

        super.initialize(step, result);

        submitBar.setVisibility(View.GONE);

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
                postDelayed(fingerprintAnimationRunnable, DEFAULT_FINGERPRINT_ANIMATION_MILLIS);
            }
        };
        post(fingerprintAnimationRunnable);
    }

    private void initFingerPrintManager() {
        fingerprintManager = FingerprintManagerCompat.from(getContext());
        // We already have a fingerprint set, and this steplayout will be dismissed once
        // the callbacks are set
        if (fingerprintStep.isCreationStep() && StorageAccess.getInstance().hasPinCode(getContext())) {
            return;
        }
        if (initCipher()) {
            cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
            startListening(cryptoObject);
        }
    }

    @Override
    protected void validateAndSetStep(Step step) {
        super.validateAndSetStep(step);

        if (!(step instanceof FingerprintStep)) {
            throw new IllegalStateException("FingerprintStepLayout expects a FingerprintStep");
        }

        fingerprintStep = (FingerprintStep)step;
    }

    protected FingerprintManagerCompat.AuthenticationCallback authCallbacks = new FingerprintManagerCompat.AuthenticationCallback() {

        static final int ERROR_MSG_TOO_MANY_ATTEMPTS = 7;

        @Override
        public void onAuthenticationError(final int errMsgId, final CharSequence errString) {
            // Callback is not on the main thread, so send it to the main thread
            removeCallbacks(fingerprintAnimationRunnable);
            post(new Runnable() {
                @Override
                public void run() {
                    if (!mSelfCancelled) {
                        showError(errString);
                    }
                    if (errMsgId == ERROR_MSG_TOO_MANY_ATTEMPTS) {
                        // post delay to change the text back to hint text
                        removeCallbacks(fingerprintAnimationRunnable);
                        fingerprintAnimationRunnable = new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onSaveStep(StepCallbacks.ACTION_END, fingerprintStep, null);
                            }
                        };
                        postDelayed(fingerprintAnimationRunnable, DEFAULT_TOO_MANYATTEMPTS_MILLIS);
                    }
                }
            });
        }

        @Override
        public void onAuthenticationHelp(final int helpMsgId, final CharSequence helpString) {
            // Callback is not on the main thread, so send it to the main thread
            removeCallbacks(fingerprintAnimationRunnable);
            post(new Runnable() {
                @Override
                public void run() {
                    showError(helpString);
                }
            });
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            // Callback is not on the main thread, so send it to the main thread
            removeCallbacks(fingerprintAnimationRunnable);
            post(new Runnable() {
                @Override
                public void run() {
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

        @Override
        public void onAuthenticationFailed() {
            // Callback is not on the main thread, so send it to the main thread
            removeCallbacks(fingerprintAnimationRunnable);
            post(new Runnable() {
                @Override
                public void run() {
                    showError(getContext().getResources().getString(R.string.rsb_fingerprint_not_recognized));
                }
            });
        }
    };

    protected void handleFingerprintSuccess() {
        if (fingerprintStep.isCreationStep()) {
            generateAndEncryptPin();
        } else {
            injectPin();
        }
        stopListening();
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, fingerprintStep, null);
    }

    /**
     * Creates a symmetric key in the Android Key Store, which can only be used after the user has
     * authenticated with fingerprint.
     */
    private void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            // we know our pincode is securely random, since we generate it ourselves,
                            // so we do not need an Initialization Vector (IV)
                            .setRandomizedEncryptionRequired(false)
                            .setUserAuthenticationRequired(true)
                            .build());
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the {@link #createKey()}
     * method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated, which in all cases we must ask the user to sign back in with their DataProvider credentials
     */
    private boolean initCipher() {
        try {
            if (fingerprintStep.isCreationStep()) {
                createKey();
            }

            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher = Cipher.getInstance(AES_MODE);
            if (fingerprintStep.isCreationStep()) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                // Here we need to load the initialization vector that was used to encrypt the data
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(loadIv()));
            }
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            // A delay is needed so that the Displaying task has time to complete it's
            // view rendering, and can properly handle the callback state
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showUnrecoverableEntryAlert();
                }
            }, DEFAULT_ERROR_TIMEOUT_MILLIS);
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
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

    /**
     * The initialization vector is handled by Android FingerprintManager, but we still need
     * to save it, because it is used in decryption
     * This can only be saved once fingerprint is verified
     */
    private void saveIv() {
        byte[] decodedIv = cipher.getIV();
        String base64Iv = Base64.encodeToString(decodedIv, Base64.DEFAULT);
        getContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .edit().putString(IV_KEY, base64Iv).apply();
    }

    /**
     * @return a byte[] that should be plugged into the decryption cipher initialization
     */
    private byte[] loadIv() {
        String base64Iv = getContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE).getString(IV_KEY, "");
        byte[] decodedIv = Base64.decode(base64Iv, Base64.DEFAULT);
        return decodedIv;
    }

    private void generateAndEncryptPin() {
        // The AndroidKeyStore can be used to help secure sensitive data,
        // but it doesn't actually store the sensitive data, so we need to
        // generate and store the encrypted secret ourselves
        RandomStringGenerator pinGenerator = new RandomStringGenerator();
        String rawPin = pinGenerator.randomString(RANDOM_PINCODE_LENGTH);

        byte[] encodedBytes;
        try {
            byte[] decodedBytes = rawPin.getBytes(UTF_8);
            encodedBytes = cipher.doFinal(decodedBytes);
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Failed to do encryption", e);
        }
        String encryptedBase64EncodedPincode = Base64.encodeToString(encodedBytes, Base64.DEFAULT);

        StorageAccess.getInstance().createPinCode(getContext(), rawPin);
        StorageAccess.getInstance().setUsesFingerprint(getContext(), encryptedBase64EncodedPincode);

        saveIv(); // this will be used for decrypting the secure pin later
    }

    private final class RandomStringGenerator {
        static final String AB = "%+\\/'!#$^?:,.(){}[]~-_0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        String randomString( int len ) {
            StringBuilder sb = new StringBuilder( len );
            for( int i = 0; i < len; i++ )
                sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
            return sb.toString();
        }
    }

    private void injectPin() {
        try {
            String encryptedPincode = StorageAccess.getInstance().getFingerprint(getContext());
            byte[] encrypted = Base64.decode(encryptedPincode, Base64.DEFAULT);
            byte[] decodedBytes = cipher.doFinal(encrypted);
            String fingerprintPin = new String(decodedBytes, UTF_8);

            StorageAccess.getInstance().authenticate(getContext(), fingerprintPin);
        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Failed to do decryption " + e);
        }
    }

    public boolean isFingerprintAuthAvailable() {
        return fingerprintManager.isHardwareDetected()
                && fingerprintManager.hasEnrolledFingerprints();
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        cancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        fingerprintManager.authenticate(cryptoObject, 0, cancellationSignal, authCallbacks, null);
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        super.setCallbacks(callbacks);
        if (fingerprintStep.isCreationStep() && StorageAccess.getInstance().hasPinCode(getContext())) {
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
            !isFingerprintAuthAvailable())
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
        postDelayed(fingerprintAnimationRunnable, DEFAULT_ERROR_TIMEOUT_MILLIS);
    }
}
