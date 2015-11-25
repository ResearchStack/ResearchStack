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
public class DataEncoder
{
    private SecretKey key;

    public static final String SALT = "you shouldn't do this";
    private final Cipher cipher;

    public DataEncoder(char[] passphrase) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        key = KeyHelper.generateKey(passphrase, SALT.getBytes());
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
    }

    public byte[] encrypt(byte[] clear) throws BadPaddingException, IllegalBlockSizeException
    {
        return cipher.doFinal(clear);
    }


}
