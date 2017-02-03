package org.researchstack.backbone.storage.file;

import android.annotation.TargetApi;
import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Base64;

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
 * Created by TheMDP on 2/3/17.
 *
 * The KeystoreEncryptionHelper class aids in communicating with the Android Keystore
 * This class can create a new keystore key, and use that to encrypt and decrypt unique secrets
 *
 * Currently, the keystore keys that are generated require user authentication with a
 * fingerprint or the credential alert (password), and will throw exceptions if the user is not
 * authenticated. However, that is up to the class that uses this class to make sure the user is authenticated
 */

@TargetApi(android.os.Build.VERSION_CODES.M) // api 23, or Android 6.0
public final class KeystoreEncryptionHelper {

    /** Reference to Android Key Store */
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    /** Encryption type used for key generation */
    private static final String AES_MODE =
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7;

    private static final String UTF_8    = "UTF-8";

    /** Used when generating a random pincode */
    private static final int RANDOM_PINCODE_LENGTH = 32;

    /**
     * @param fingerprintManager that has been initialized
     * @return true if this device supports fingerprint, false otherwise
     */
    public static boolean isFingerprintAuthAvailable(FingerprintManagerCompat fingerprintManager) {
        return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
    }

    /**
     * Creates a symmetric key in the Android Key Store, which can only be used after the user has
     * authenticated with fingerprint or password
     * @param keyName the name of the key in the Android Keystore
     */
    private static void createKey(String keyName) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(keyName,
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
     * This method will cause a crash and put the app in a bad state if a key with "keyName" has
     * already been created
     *
     * @param keyName the key name to create and setup with the cipher
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated
     */
    public static Cipher initCipherForEncryption(String keyName) {
        try {
            // We need a new key generated for this encryption
            createKey(keyName);

            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(keyName, null);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher;
        } catch (KeyPermanentlyInvalidatedException e) {
            // At this point the user has either removed the lock screen security,
            // or they have added or changed the fingerprints they were previously using
            return null;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * @param context used to retrieve the Initialization Vector (IV) that was used to encrypt
     * @param keyName the Android Keystore key name
     * @param encryptedBase64Secret the encrypted secret that the cipher will be decrpting
     * @param iv the Initialization Vector(IV) used in encryption, it is responsibility of the
     *           class that uses this to save the encryptor's IV, and feed it back here when decrypting
     *           It can be stored in SharedPreferences or wherever, it doesn't really matter
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated
     */
    public static Cipher initCipherForDecryption(Context context, String keyName, String encryptedBase64Secret, byte[] iv) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(keyName, null);
            Cipher cipher = Cipher.getInstance(AES_MODE);

            // Here we need to load the initialization vector that was used to encrypt the data
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            return cipher;
        } catch (KeyPermanentlyInvalidatedException e) {
            // At this point the user has either removed the lock screen security,
            // or they have added or changed the fingerprints they were previously using
            return null;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * @param context can be android or app, used to store Initialization Vector (IV) in SharedPrefs
     * @param cipher must be initialized and the user must be authenticated using fingerprint or password
     * @param secret the secret to encrypt using the cipher
     * @return a base 64 encoded String version of the securely encrypted secret
     */
    public static String encryptSecret(Context context, Cipher cipher, String secret) {
        byte[] encodedBytes;
        try {
            byte[] decodedBytes = secret.getBytes(UTF_8);
            encodedBytes = cipher.doFinal(decodedBytes);
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Failed to do encryption", e);
        }
        String encryptedBase64EncodedSecret = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        return encryptedBase64EncodedSecret;
    }

    /**
     * @return random string secret with length 32
     */
    public static String generateSecureRandomPin() {
        // The AndroidKeyStore can be used to help secure sensitive data,
        // but it doesn't actually store the sensitive data, so we need to
        // generate and store the encrypted secret ourselves
        RandomStringGenerator pinGenerator = new RandomStringGenerator();
        return pinGenerator.randomString(RANDOM_PINCODE_LENGTH);
    }

    /**
     * This method only works if your cipher has been initialized properly to decrypt,
     * and the user has authenticated, using their fingerprint, or password
     *
     * @param cipher an initialized cipher with IV with mode DECRYPT
     * @param encryptedSecret the secret we are trying to decrypt
     * @return the decrypted secret
     */
    public static String decryptPin(Cipher cipher, String encryptedSecret) {
        try {
            byte[] encrypted = Base64.decode(encryptedSecret, Base64.DEFAULT);
            byte[] decodedBytes = cipher.doFinal(encrypted);
            return new String(decodedBytes, UTF_8);
        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Failed to do decryption " + e);
        }
    }

    /**
     * Generates a securely random string using the characters in AB at whatever length is requested
     */
    private static final class RandomStringGenerator {
        static final String AB = "%+\\/'!#$^?:,.(){}[]~-_0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        String randomString( int len ) {
            StringBuilder sb = new StringBuilder( len );
            for( int i = 0; i < len; i++ )
                sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
            return sb.toString();
        }
    }
}
