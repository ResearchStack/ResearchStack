package org.researchstack.backbone.storage.file.aes;


import com.tozny.crypto.android.AesCbcWithIntegrity;


public class AesProvider extends PinProtectedProvider
{
    public AesProvider()
    {
        super();
    }

    @Override
    protected Encrypter createEncrypter(AesCbcWithIntegrity.SecretKeys masterKey)
    {
        return new AesEncrypter(masterKey);
    }
}
