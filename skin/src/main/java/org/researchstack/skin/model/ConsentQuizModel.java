package org.researchstack.skin.model;

import java.io.Serializable;
import java.util.List;

public class ConsentQuizModel implements Serializable {
    private String failureTitle;
    private String failureMessage;
    private String successTitle;
    private String successMessage;
    private int allowedFailures;
    private List<QuizQuestion> questions;

    // fields with defaults
    private String incorrectIcon = "rsb_quiz_retry";
    private String correctIcon = "rss_ic_quiz_valid";

    public String getFailureTitle() {
        return failureTitle;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public String getSuccessTitle() {
        return successTitle;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public int getAllowedFailures() {
        return allowedFailures;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public String getIncorrectIcon() {
        return incorrectIcon;
    }

    public String getCorrectIcon() {
        return correctIcon;
    }

    public class QuizQuestion implements Serializable {
        private String identifier;
        private String prompt;
        private String type;
        private String expectedAnswer;
        private String text;
        private List<String> textChoices;
        private String positiveFeedback;
        private String negativeFeedback;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getPrompt() {
            return prompt;
        }

        public String getType() {
            return type;
        }

        public String getExpectedAnswer() {
            return expectedAnswer;
        }

        public String getText() {
            return text;
        }

        public List<String> getTextChoices() {
            return textChoices;
        }

        public String getPositiveFeedback() {
            return positiveFeedback == null ? "" : positiveFeedback;
        }

        public String getNegativeFeedback() {
            return negativeFeedback == null ? "" : negativeFeedback;
        }
    }
}
