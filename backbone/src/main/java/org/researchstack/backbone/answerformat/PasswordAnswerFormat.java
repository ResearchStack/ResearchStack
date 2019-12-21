package org.researchstack.backbone.answerformat;

import android.text.InputType;

/**
 * Created by TheMDP on 1/4/17.
 */

public class PasswordAnswerFormat extends TextAnswerFormat {

    static final int DEFAULT_PASSWORD_MIN_LENGTH    = 4;
    static final int DEFAULT_PASSWORD_MAX_LENGTH    = 16;
    static final String PASSWORD_VALIDATION_REGEX   = "^\\p{ASCII}*$";

    /**
     * Creates a TextAnswerFormat with no maximum length
     * Also, default constructor needed for serilization/deserialization of object
     */
    public PasswordAnswerFormat()
    {
        super();
        setMinumumLength(DEFAULT_PASSWORD_MIN_LENGTH);
        setMaximumLength(DEFAULT_PASSWORD_MAX_LENGTH);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        setIsMultipleLines(false);
        setValidationRegex(PASSWORD_VALIDATION_REGEX);
    }
}
