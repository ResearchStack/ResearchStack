package co.touchlab.researchstack.backbone.storage.file;
import android.content.Context;

import co.touchlab.researchstack.backbone.storage.file.aes.Encrypter;
import co.touchlab.researchstack.backbone.storage.file.auth.PinCodeConfig;

public interface EncryptionProvider
    {
        boolean hasPinCode(Context context);

        void setPinCode(Context context, String pin);

        boolean ready();

        boolean needsAuth(Context context);

        void startWithPassphrase(Context context, String pin);

        void logAccessTime();

        Encrypter getEncrypter();

        PinCodeConfig getPinCodeConfig();
    }