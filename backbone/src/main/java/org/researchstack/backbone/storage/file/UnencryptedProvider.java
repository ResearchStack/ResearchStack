package org.researchstack.backbone.storage.file;
import android.content.Context;

import org.researchstack.backbone.storage.file.aes.ClearEncrypter;
import org.researchstack.backbone.storage.file.aes.Encrypter;


public class UnencryptedProvider implements EncryptionProvider
{

    public UnencryptedProvider()
    {
    }

    @Override
    public boolean hasPinCode(Context context)
    {
        throw new RuntimeException("Pin codes should not be used with this encryption provider");
    }

    @Override
    public void createPinCode(Context context, String pin)
    {
        throw new RuntimeException("Pin codes should not be used with this encryption provider");
    }

    @Override
    public void changePinCode(Context context, String oldPin, String newPin)
    {
        throw new RuntimeException("Pin codes should not be used with this encryption provider");
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
        throw new RuntimeException("Pin codes should not be used with this encryption provider");
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
