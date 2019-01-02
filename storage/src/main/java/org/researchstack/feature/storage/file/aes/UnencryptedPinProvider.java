package org.researchstack.backbone.storage.file.aes;

import com.tozny.crypto.android.AesCbcWithIntegrity;

/**
 * This implementation of PinProtectedProvider uses the base class for pin creation and
 * authorization, but returns a clear text encrypter, allowing the data to pass through with no
 * encryption or decryption. Use this class if you want pin protection of the app itself but not the
 * data it saves to disk.
 */
public class UnencryptedPinProvider extends PinProtectedProvider {
    public UnencryptedPinProvider() {
        super();
    }

    @Override
    protected Encrypter createEncrypter(AesCbcWithIntegrity.SecretKeys masterKey) {
        // Master key is created only for pin protection of the app, never used for file encryption
        return new ClearEncrypter();
    }
}
