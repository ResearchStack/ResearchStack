package co.touchlab.researchstack.common.secure.aes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by kgalligan on 11/24/15.
 */
public class DataDecoder
{
    private SecretKey key;

    public static final String SALT = "you shouldn't do this";
    private final Cipher cipher;

    public DataDecoder(char[] passphrase) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        key = KeyHelper.generateKey(passphrase, SALT.getBytes());
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
    }

    public byte[] decrypt(byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException
    {
        return cipher.doFinal(encrypted);
    }
}
