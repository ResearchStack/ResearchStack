package org.researchstack.backbone.answerformat;


import android.text.InputType;

import org.researchstack.backbone.utils.TextUtils;

public class EmailAnswerFormat extends TextAnswerFormat {
    private static final int MAX_EMAIL_LENGTH = 255;

    /* Default constructor needed for serilization/deserialization of object */
    public EmailAnswerFormat() {
        super(MAX_EMAIL_LENGTH);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    @Override
    public boolean isAnswerValid(String text) {
        return super.isAnswerValid(text) && TextUtils.isValidEmail(text);
    }
}
