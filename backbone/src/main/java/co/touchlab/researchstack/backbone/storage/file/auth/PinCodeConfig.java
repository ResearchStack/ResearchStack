package co.touchlab.researchstack.backbone.storage.file.auth;
import android.text.InputType;
import android.text.format.DateUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;

public class PinCodeConfig
{
    private static final String DIGITS_NUMERIC      = "1234567890";
    private static final String DIGITS_ALPHABETIC   = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS_ALPHANUMERIC = DIGITS_ALPHABETIC + DIGITS_NUMERIC;

    // TODO document this better, but you may create your own enum that implements this interface
    public interface Type
    {
        int getInputType();

        KeyListener getDigitsKeyListener();

        int getVisibleVariationType(boolean visible);
    }

    public enum PinCodeType implements Type
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

        PinCodeType(int inputType, int inputTypeVisible, int inputTypeHidden, KeyListener listener)
        {
            this.inputType = inputType;
            this.inputTypeVisible = inputTypeVisible;
            this.inputTypeHidden = inputTypeHidden;
            this.listener = listener;
        }

        @Override
        public int getInputType()
        {
            return inputType;
        }

        @Override
        public KeyListener getDigitsKeyListener()
        {
            return listener;
        }

        @Override
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
        this(PinCodeType.Numeric, 4, autoLockTime);
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
