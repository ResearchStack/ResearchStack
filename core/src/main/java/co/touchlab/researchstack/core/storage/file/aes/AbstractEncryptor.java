package co.touchlab.researchstack.core.storage.file.aes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by kgalligan on 11/26/15.
 */
public abstract class AbstractEncryptor
{

    public static final String SALT = "you shouldn't do this";
    protected final Cipher cipher;

    public AbstractEncryptor(char[] passphrase, int bitLength) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        SecretKey key = KeyHelper.generateKey(passphrase, SALT.getBytes(), bitLength);
        cipher = Cipher.getInstance("AES");
        cipher.init(getMode(), key);
    }

    public abstract int getMode();
}
