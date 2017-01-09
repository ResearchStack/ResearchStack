package org.researchstack.backbone.answerformat;

import android.text.InputType;

/**
 * Created by TheMDP on 1/4/17.
 */

public class PasswordAnswerFormat extends TextAnswerFormat {

    static final int DEFAULT_PASSWORD_MIN_LENGTH    = 4;
    static final int DEFAULT_PASSWORD_MAX_LENGTH    = 16;
    static final String PASSWORD_VALIDATION_REGEX   = "[[:ascii:]]";

    /**
     * Creates a TextAnswerFormat with no maximum length
     */
    public PasswordAnswerFormat()
    {
        super(DEFAULT_PASSWORD_MAX_LENGTH);
        minimumLength = DEFAULT_PASSWORD_MIN_LENGTH;
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD;
        isMultipleLines = false;
        validationRegex = PASSWORD_VALIDATION_REGEX;
    }
}