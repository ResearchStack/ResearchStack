package org.researchstack.backbone.storage.file.aes;


import com.tozny.crypto.android.AesCbcWithIntegrity;

/**
 * This implementation of PinProtectedProvider uses the base class for pin creation and
 * authorization, and uses {@link AesEncrypter}'s standard encryption for protection of all data
 * written through its Encrypter.
 */
public class AesProvider extends PinProtectedProvider {
    public AesProvider() {
        super();
    }

    @Override
    protected Encrypter createEncrypter(AesCbcWithIntegrity.SecretKeys masterKey) {
        return new AesEncrypter(masterKey);
    }
}
