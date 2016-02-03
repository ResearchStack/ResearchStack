package co.touchlab.researchstack.backbone.storage.file.aes;


import com.tozny.crypto.android.AesCbcWithIntegrity;

/**
 * Created by bradleymcdermott on 2/3/16.
 */
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
