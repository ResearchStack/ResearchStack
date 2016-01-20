package co.touchlab.researchstack.core.storage.file.auth;
public class PinCodeConfig
{
    private long    autoLockTime;
    private boolean alphaNumeric;
    private int     length;

    //TODO use enum for alphanumeric
    public PinCodeConfig(boolean alphaNumeric, int length, long autoLockTime)
    {
        this.alphaNumeric = alphaNumeric;
        this.length = length;
        this.autoLockTime = autoLockTime;
    }

    public boolean isAlphaNumeric()
    {
        return alphaNumeric;
    }

    public int getLength()
    {
        return length;
    }

    public long getAutoLockTime()
    {
        return autoLockTime;
    }
}
