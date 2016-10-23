package org.researchstack.backbone.storage.file;
import android.content.Context;

import org.researchstack.backbone.storage.file.aes.Encrypter;

/**
 * This interface is used to create, change, and authenticate pin codes provided by the user.
 */
public interface EncryptionProvider
{
    /**
     * Returns whether the user has created a pin code.
     *
     * @param context android context
     * @return boolean indicating whether the user has created a pin code
     */
    boolean hasPinCode(Context context);

    /**
     * Create a pin code from the provided input
     *
     * @param context android context
     * @param pin     the pin that the user entered
     */
    void createPinCode(Context context, String pin);

    /**
     * Removes the pin code if one exists.
     *
     * @param context android context
     */
    void removePinCode(Context context);

    /**
     * Changes the pin code if the old pin provided matches
     *
     * @param context android context
     * @param oldPin  user input for old pin
     * @param newPin  user input for the new pin
     */
    void changePinCode(Context context, String oldPin, String newPin);

    /**
     * Returns a boolean indicating whether authentication is needed. If your Encrypter is not
     * ready, return false.
     *
     * @param context    android context
     * @param codeConfig the pin config for the app
     * @return a boolean indicating whether pin code authorization is needed
     */
    boolean needsAuth(Context context, PinCodeConfig codeConfig);

    /**
     * This method should attempt to initialize the Encrypter with the provided pin, otherwise it
     * should throw a {@link StorageAccessException}
     *
     * @param context android context
     * @param pin     the pin input from the user
     */
    void startWithPassphrase(Context context, String pin);

    /**
     * Logs the time of access for deciding whether to re-prompt the user for a pin after having
     * been away from the app for longer than the lock time.
     */
    void logAccessTime();

    /**
     * Returns the Encrypter for data being written to disk, which should be initialized in {@link
     * #startWithPassphrase}.
     *
     * @return the encrypter
     */
    Encrypter getEncrypter();
}