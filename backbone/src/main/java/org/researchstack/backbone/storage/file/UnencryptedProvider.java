package org.researchstack.backbone.storage.file;
import android.content.Context;

import org.researchstack.backbone.storage.file.aes.ClearEncrypter;
import org.researchstack.backbone.storage.file.aes.Encrypter;

/**
 * Use this class as the EncryptionProvider when you want to store all data without encryption. Pin
 * protection is still available, but will not be used to encrypt files written.
 */
public class UnencryptedProvider implements EncryptionProvider
{

    public static final String PIN_CODE_ERROR_MSG = "Pin codes should not be used with this encryption provider";

    public UnencryptedProvider()
    {
    }

    @Override
    public boolean hasPinCode(Context context)
    {
        throw new RuntimeException(PIN_CODE_ERROR_MSG);
    }

    @Override
    public void createPinCode(Context context, String pin)
    {
        throw new RuntimeException(PIN_CODE_ERROR_MSG);
    }

    @Override
    public void changePinCode(Context context, String oldPin, String newPin)
    {
        throw new RuntimeException(PIN_CODE_ERROR_MSG);
    }

    @Override
    public void removePinCode(Context context)
    {
        throw new RuntimeException(PIN_CODE_ERROR_MSG);
    }

    @Override
    public boolean needsAuth(Context context, PinCodeConfig codeConfig)
    {
        // never needs auth since there's no encryption
        return false;
    }

    @Override
    public void startWithPassphrase(Context context, String pin)
    {
        throw new RuntimeException(PIN_CODE_ERROR_MSG);
    }

    @Override
    public void logAccessTime()
    {
        // No-op
    }

    @Override
    public Encrypter getEncrypter()
    {
        return new ClearEncrypter();
    }
}
