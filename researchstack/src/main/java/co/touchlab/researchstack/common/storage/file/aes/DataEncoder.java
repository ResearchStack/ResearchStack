package co.touchlab.researchstack.common.storage.file.aes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by kgalligan on 11/24/15.
 */
public class DataEncoder extends AbstractEncryptor
{
    public DataEncoder(char[] passphrase, int bitLength) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        super(passphrase, bitLength);
    }

    @Override
    public int getMode()
    {
        return Cipher.ENCRYPT_MODE;
    }

    public byte[] encrypt(byte[] clear) throws BadPaddingException, IllegalBlockSizeException
    {
        return cipher.doFinal(clear);
    }
}
