package co.touchlab.researchstack.common.storage;
/**
 * Created by kgalligan on 11/24/15.
 */
public class SecurityProfile
{
    public enum EncryptionType
    {
        None, AES_128, AES_192, AES_256
    }

    private EncryptionType encryptionType;
    private boolean alphaNumeric;
    private int minLength;
    private int maxLength;

    /*
    This will probably get much bigger.  Should use a builder instead (probably).
     */
    public SecurityProfile(EncryptionType encryptionType, boolean alphaNumeric, int minLength, int maxLength)
    {
        this.encryptionType = encryptionType;
        this.alphaNumeric = alphaNumeric;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public EncryptionType getEncryptionType()
    {
        return encryptionType;
    }

    public boolean isAlphaNumeric()
    {
        return alphaNumeric;
    }

    public int getMinLength()
    {
        return minLength;
    }

    public int getMaxLength()
    {
        return maxLength;
    }
}
