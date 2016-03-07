package org.researchstack.core.storage.file.aes;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.researchstack.backbone.storage.file.aes.ClearEncrypter;
import org.researchstack.backbone.storage.file.aes.Encrypter;
import org.researchstack.backbone.storage.file.aes.PinProtectedProvider;

/**
 * Created by bradleymcdermott on 2/3/16.
 */
public class UnencryptedPinProvider extends PinProtectedProvider
{
    public UnencryptedPinProvider()
    {
        super();
    }

    @Override
    protected Encrypter createEncrypter(AesCbcWithIntegrity.SecretKeys masterKey)
    {
        // Master key is created only for pin protection of the app, never used for file encryption
        return new ClearEncrypter();
    }
}
