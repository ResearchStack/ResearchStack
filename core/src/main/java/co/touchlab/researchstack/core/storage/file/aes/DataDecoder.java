package co.touchlab.researchstack.core.storage.file.aes;
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
public class DataDecoder extends AbstractEncryptor
{
    public DataDecoder(char[] passphrase, int bitLength) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        super(passphrase, bitLength);
    }

    @Override
    public int getMode()
    {
        return Cipher.DECRYPT_MODE;
    }

    public byte[] decrypt(byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException
    {
        return cipher.doFinal(encrypted);
    }
}
