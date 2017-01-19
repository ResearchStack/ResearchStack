package org.researchstack.backbone.storage.file;

import android.text.InputFilter;
import android.text.InputType;
import android.text.format.DateUtils;

import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.TextUtils;

/**
 * This class allows you to customize the type/strength of the pin code that the user must create to
 * protect their data.
 */
public class PinCodeConfig {
    private static final String DIGITS_NUMERIC = "1234567890";
    private static final String DIGITS_ALPHABETIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS_ALPHANUMERIC = DIGITS_ALPHABETIC + DIGITS_NUMERIC;
    private Type type;
    private long autoLockTime;
    private int length;
    /**
     * Constructs the default pin config, 4 digits and 5 minute lockout time
     */
    public PinCodeConfig() {
        this(5 * DateUtils.MINUTE_IN_MILLIS);
    }
    /**
     * Constructs a pin config with 4 digits and the provided lockout time
     *
     * @param autoLockTime the time before the user must re-enter their pin
     */
    public PinCodeConfig(long autoLockTime) {
        this(PinCodeType.Numeric, 4, autoLockTime);
    }

    /**
     * Constructs a pin config with the specific type, character length, and lockout time
     *
     * @param type         the {@link Type} representing the pincode restrictions
     * @param length       the character length of the pin code
     * @param autoLockTime the time before the user must re-enter their pin
     */
    public PinCodeConfig(Type type, int length, long autoLockTime) {
        this.type = type;
        this.length = length;
        this.autoLockTime = autoLockTime;
    }

    /**
     * Returns the pin type representing the restrictions on the pin code for the app
     *
     * @return the pin type
     */
    public Type getPinType() {
        return type;
    }

    /**
     * Returns the character length required for the pin
     *
     * @return the pin length
     */
    public int getPinLength() {
        return length;
    }

    /**
     * Returns the amount of time in milliseconds that the user must be gone from the app for before
     * they are prompted for their pin code again.
     *
     * @return the lockout time
     */
    public long getPinAutoLockTime() {
        return autoLockTime;
    }

    /**
     * Sets the amount of time in milliseconds that the user must be gone from the app for before
     * they are prompted for their pin code again.
     * <p>
     * This may be a setting in your app, and therefore can be updated at any time.
     *
     * @param pinAutoLockTime the lockout time
     */
    public void setPinAutoLockTime(long pinAutoLockTime) {
        this.autoLockTime = pinAutoLockTime;
    }

    /**
     * General {@link Type}s that should cover most desired pin code configs: Alpha, Numeric, and
     * Alphanumberic.
     */
    public enum PinCodeType implements Type {
        Alphabetic(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                InputType.TYPE_TEXT_VARIATION_NORMAL,
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                new TextUtils.AlphabeticFilter()),

        Numeric(InputType.TYPE_CLASS_NUMBER,
                InputType.TYPE_NUMBER_VARIATION_NORMAL,
                InputType.TYPE_NUMBER_VARIATION_PASSWORD,
                new TextUtils.NumericFilter()),

        AlphaNumeric(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                InputType.TYPE_TEXT_VARIATION_NORMAL,
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                new TextUtils.AlphanumericFilter());

        private int inputType;
        private int inputTypeVisible;
        private int inputTypeHidden;
        private InputFilter filter;

        PinCodeType(int inputType, int inputTypeVisible, int inputTypeHidden, InputFilter filter) {
            this.inputType = inputType;
            this.inputTypeVisible = inputTypeVisible;
            this.inputTypeHidden = inputTypeHidden;
            this.filter = filter;
        }

        @Override
        public int getInputType() {
            return inputType;
        }

        @Override
        public int getInputTypeStringId() {
            if (this == PinCodeType.Numeric) {
                return R.string.rsb_pincode_enter_digit;
            } else if (this == PinCodeType.Alphabetic) {
                return R.string.rsb_pincode_enter_letter;
            } else {
                return R.string.rsb_pincode_enter_character;
            }
        }

        @Override
        public InputFilter getInputFilter() {
            return filter;
        }

        @Override
        public int getVisibleVariationType(boolean visible) {
            return visible ? inputTypeVisible : inputTypeHidden;
        }
    }

    /**
     * The interface that the {@link PinCodeType} implements. Since you cannot extend an enum to add
     * your own types, implement this interface as an alternative if you need your own PinCodeType.
     */
    public interface Type {
        /**
         * Returns the {@link InputType} that should be applied to the EditText during pincode
         * entry.
         *
         * @return the input type for the EditText
         */
        int getInputType();

        /**
         * Returns the id for the string resource representing the input character type.
         * <p>
         * This will be inserted into the instructions for creating a pincode. For example, if you
         * return 'digit', it will tell the user to enter a '4-digit code'. If you return 'letter',
         * it will say '4-letter code'.
         *
         * @return the resource id for the string representing the input character type
         */
        int getInputTypeStringId();

        /**
         * Returns the {@link InputFilter} for the EditText. Use this to limit the types of
         * characters that may be used in the pin code type.
         *
         * @return the input filter
         */
        InputFilter getInputFilter();

        /**
         * Returns the {@link InputType} that should be applied to the EditText based on whether the
         * text is visible or not.
         *
         * @return the input type for the EditText
         */
        int getVisibleVariationType(boolean visible);
    }
}
