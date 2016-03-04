package org.researchstack.backbone.storage.file;
import android.content.Context;

import org.researchstack.backbone.storage.file.aes.Encrypter;
import org.researchstack.backbone.storage.file.auth.PinCodeConfig;

public interface EncryptionProvider
{
    boolean hasPinCode(Context context);

    void setPinCode(Context context, String pin);

    void changePinCode(Context context, String oldPin, String newPin);

    boolean needsAuth(Context context, PinCodeConfig codeConfig);

    void startWithPassphrase(Context context, String pin);

    void logAccessTime();

    Encrypter getEncrypter();
}