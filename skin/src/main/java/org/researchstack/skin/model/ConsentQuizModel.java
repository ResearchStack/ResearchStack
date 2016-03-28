package org.researchstack.skin.model;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ConsentQuizModel implements Serializable
{
    @SerializedName("evaluation_properties")
    public EvaluationProperties evalProperties;

    @SerializedName("question_properties")
    public QuestionProperties questionProperties;

    @SerializedName("questions")
    public List<QuizQuestion> questions;

    public EvaluationProperties getEvaluationProperties()
    {
        return evalProperties;
    }

    public QuestionProperties getQuestionProperties()
    {
        return questionProperties;
    }

    public List<QuizQuestion> getQuestions()
    {
        return questions;
    }

    public class QuizQuestion implements Serializable
    {
        @SerializedName("identifier")
        public String id;

        @SerializedName("prompt")
        public String question;

        @SerializedName("positiveFeedback")
        public String positiveFeedback;

        @SerializedName("negativeFeedback")
        public String negativeFeedback;

        @SerializedName("constraints")
        public TaskModel.ConstraintsModel constraints;
    }

    public class QuestionProperties implements Serializable
    {
        @SerializedName("correctTitle")
        public String correctTitle;

        @SerializedName("incorrectTitle")
        public String incorrectTitle;

        @SerializedName("correctCliffhanger")
        public String correctCliffhanger;

        @SerializedName("incorrectCliffhanger")
        public String incorrectCliffhanger;

        @SerializedName("correct")
        public String correct;

        @SerializedName("introText")
        public String introText;

    }

    public class EvaluationProperties implements Serializable
    {
        @SerializedName("correctIcon")
        public String correctIcon;

        @SerializedName("incorrectIcon")
        public String incorrectIcon;

        @SerializedName("quizAllCorrectText")
        public String quizAllCorrectText;

        @SerializedName("quizPassedText")
        public String quizPassedText;

        @SerializedName("quizFailure1Text")
        public String quizFailure1Text;

        @SerializedName("quizFailure2Text")
        public String quizFailure2Text;

        @SerializedName("maxIncorrect")
        public int maxIncorrect;
    }

}
