package org.researchstack.backbone.answerformat;


import org.researchstack.backbone.utils.TextUtils;

public class EmailAnswerFormat extends TextAnswerFormat {
    private static final int MAX_EMAIL_LENGTH = 255;

    public EmailAnswerFormat() {
        super(MAX_EMAIL_LENGTH);
    }

    @Override
    public boolean isAnswerValid(String text) {
        return super.isAnswerValid(text) && TextUtils.isValidEmail(text);
    }
}
