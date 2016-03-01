package co.touchlab.researchstack.backbone.storage.file;
import android.content.Context;

import co.touchlab.researchstack.backbone.storage.file.aes.ClearEncrypter;
import co.touchlab.researchstack.backbone.storage.file.aes.Encrypter;
import co.touchlab.researchstack.backbone.storage.file.auth.PinCodeConfig;

/**
 * Created by bradleymcdermott on 2/3/16.
 */
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
    public void setPinCode(Context context, String pin)
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
