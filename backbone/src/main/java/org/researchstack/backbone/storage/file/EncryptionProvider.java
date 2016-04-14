package org.researchstack.backbone.storage.file;
import android.content.Context;

import org.researchstack.backbone.storage.file.aes.Encrypter;

public interface EncryptionProvider
{
    boolean hasPinCode(Context context);

    void createPinCode(Context context, String pin);

    void changePinCode(Context context, String oldPin, String newPin);

    boolean needsAuth(Context context, PinCodeConfig codeConfig);

    void startWithPassphrase(Context context, String pin);

    void logAccessTime();

    Encrypter getEncrypter();
}