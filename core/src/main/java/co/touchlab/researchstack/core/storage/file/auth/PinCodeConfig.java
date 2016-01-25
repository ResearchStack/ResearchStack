package co.touchlab.researchstack.core.storage.file.auth;
import android.text.InputType;
import android.text.format.DateUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;

public class PinCodeConfig
{
    private static final String DIGITS_NUMERIC      = "1234567890";
    private static final String DIGITS_ALPHABETIC   = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS_ALPHANUMERIC = DIGITS_ALPHABETIC + DIGITS_NUMERIC;

    public enum Type
    {
        Alphabetic(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                InputType.TYPE_TEXT_VARIATION_NORMAL,
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                DigitsKeyListener.getInstance(DIGITS_ALPHABETIC)),

        Numeric(InputType.TYPE_CLASS_NUMBER,
                InputType.TYPE_NUMBER_VARIATION_NORMAL,
                InputType.TYPE_NUMBER_VARIATION_PASSWORD,
                DigitsKeyListener.getInstance(DIGITS_NUMERIC)),

        AlphaNumeric(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                InputType.TYPE_TEXT_VARIATION_NORMAL,
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                DigitsKeyListener.getInstance(DIGITS_ALPHANUMERIC));

        private int         inputType;
        private int         inputTypeVisible;
        private int         inputTypeHidden;
        private KeyListener listener;

        Type(int inputType, int inputTypeVisible, int inputTypeHidden, KeyListener listener)
        {
            this.inputType = inputType;
            this.inputTypeVisible = inputTypeVisible;
            this.inputTypeHidden = inputTypeHidden;
            this.listener = listener;
        }

        public int getInputType()
        {
            return inputType;
        }

        public KeyListener getDigitsKeyListener()
        {
            return listener;
        }

        public int getVisibleVariationType(boolean visible)
        {
            return visible ? inputTypeVisible : inputTypeHidden;
        }
    }

    private Type type;
    private long autoLockTime;
    private int  length;

    public PinCodeConfig()
    {
        this(DateUtils.MINUTE_IN_MILLIS);
    }

    public PinCodeConfig(long autoLockTime)
    {
        this(Type.Numeric, 4, autoLockTime);
    }

    public PinCodeConfig(Type type, int length, long autoLockTime)
    {
        this.type = type;
        this.length = length;
        this.autoLockTime = autoLockTime;
    }

    public Type getPinType()
    {
        return type;
    }

    public int getPinLength()
    {
        return length;
    }

    public long getPinAutoLockTime()
    {
        return autoLockTime;
    }
}
