package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Deprecated // Use NavigationFormStep or NavigationSubtaskStep instead
public class ConsentQuizModel implements Serializable
{
    private String             failureTitle;
    private String             failureMessage;
    private String             successTitle;
    private String             successMessage;
    private int                allowedFailures;
    private List<QuizQuestion> questions;

    // fields with defaults
    private String incorrectIcon = "rsb_quiz_retry";
    private String correctIcon   = "rss_ic_quiz_valid";

    ConsentQuizModel() {
        super();
    }

    public String getFailureTitle()
    {
        return failureTitle;
    }

    public String getFailureMessage()
    {
        return failureMessage;
    }

    public String getSuccessTitle()
    {
        return successTitle;
    }

    public String getSuccessMessage()
    {
        return successMessage;
    }

    public int getAllowedFailures()
    {
        return allowedFailures;
    }

    public List<QuizQuestion> getQuestions()
    {
        return questions;
    }

    public String getIncorrectIcon()
    {
        return incorrectIcon;
    }

    public String getCorrectIcon()
    {
        return correctIcon;
    }

    public class QuizQuestion implements Serializable
    {
        private String       identifier;

        /** iOS named it "title" but Android named it prompt, so allow for parsing of both */
        @SerializedName(value="prompt", alternate = {"title"})
        private String       prompt;

        private ConsentQuestionType type;
        private String       expectedAnswer;
        private String       text;
        private String       positiveFeedback;
        private String       negativeFeedback;

        /**
         * There are multiple ways you can provide the options for a quiz question:
         * textChoices - a simple String list of choices for the user, the answer
         *               format will be the index in the array of the selected item
         */
        private List<String> textChoices;
        /**
         * There are multiple ways you can provide the options for a quiz question:
         * items - a list of text / value pairs that can provide any type of answer value per text
         */
        private List<Choice> items;

        public String getIdentifier()
        {
            return identifier;
        }

        public void setIdentifier(String identifier)
        {
            this.identifier = identifier;
        }

        public String getPrompt()
        {
            return prompt;
        }

        public ConsentQuestionType getType()
        {
            return type;
        }

        public String getExpectedAnswer()
        {
            return expectedAnswer;
        }

        public String getText()
        {
            return text;
        }

        public List<String> getTextChoices()
        {
            return textChoices;
        }

        public List<Choice> getItems() {
            return items;
        }

        public String getPositiveFeedback()
        {
            return positiveFeedback == null ? "" : positiveFeedback;
        }

        public String getNegativeFeedback()
        {
            return negativeFeedback == null ? "" : negativeFeedback;
        }
    }
}
