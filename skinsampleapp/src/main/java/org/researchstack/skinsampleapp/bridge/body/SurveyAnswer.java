package org.researchstack.skinsampleapp.bridge.body;

import org.researchstack.foundation.components.survey.answerformat.AnswerFormat;
import org.researchstack.foundation.components.survey.step.QuestionStep;
import org.researchstack.foundation.components.utils.FormatHelper;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SurveyAnswer
{
    public int    questionType;
    public String startDate;
    public String questionTypeName;
    public String item;
    public String endDate;

    public SurveyAnswer(StepResult stepResult)
    {
        Step step = stepResult.getStep();
        if (step instanceof QuestionStep) {
            AnswerFormat answerFormat = ((QuestionStep) step).getAnswerFormat();
            AnswerFormat.Type type = (AnswerFormat.Type) answerFormat.getQuestionType();
            this.questionType = type.ordinal();
            this.questionTypeName = type.name();
            this.startDate = FormatHelper.DEFAULT_FORMAT.format(stepResult.getStartDate());
            this.item = stepResult.getIdentifier();
            this.endDate = FormatHelper.DEFAULT_FORMAT.format(stepResult.getEndDate());
        }
        else {
            throw new RuntimeException("Step must be a question step");
        }
    }

    public static SurveyAnswer create(StepResult stepResult)
    {
        Step step = stepResult.getStep();
        if (step instanceof QuestionStep) {
            AnswerFormat answerFormat = ((QuestionStep) step).getAnswerFormat();
            AnswerFormat.Type type = (AnswerFormat.Type) answerFormat.getQuestionType();
            SurveyAnswer answer;
            switch(type)
            {
                case SingleChoice:
                case MultipleChoice:
                    answer = new ChoiceSurveyAnswer(stepResult);
                    break;
                case Integer:
                    answer = new NumericSurveyAnswer(stepResult);
                    break;
                case Boolean:
                    answer = new BooleanSurveyAnswer(stepResult);
                    break;
                case Text:
                    answer = new TextSurveyAnswer(stepResult);
                    break;
                case Date:
                    answer = new DateSurveyAnswer(stepResult);
                    break;
                case None:
                case Scale:
                case Decimal:
                case Eligibility:
                case TimeOfDay:
                case DateAndTime:
                case TimeInterval:
                case Location:
                case Form:
                default:
                    throw new RuntimeException("Cannot upload this question type to bridge");
            }
            return answer;
        }
        else {
            throw new RuntimeException("Step must be a question step");
        }

    }

    public static class BooleanSurveyAnswer extends SurveyAnswer
    {

        private final Boolean booleanAnswer;

        public BooleanSurveyAnswer(StepResult result)
        {
            super(result);
            booleanAnswer = (Boolean) result.getResult();
        }
    }

    public static class ChoiceSurveyAnswer extends SurveyAnswer
    {

        private List<?> choiceAnswers;

        public ChoiceSurveyAnswer(StepResult stepResult)
        {
            super(stepResult);

            Object result = stepResult.getResult();
            if(result instanceof List)
            {
                choiceAnswers = (List<?>) result;
            }
            else
            {
                List<Object> list = new ArrayList<>();
                list.add(result);
                choiceAnswers = list;
            }
        }
    }

    public static class NumericSurveyAnswer extends SurveyAnswer
    {

        private final Integer    numericAnswer;

        public NumericSurveyAnswer(StepResult result)
        {
            super(result);
            numericAnswer = (Integer) result.getResult();
        }
    }

    public static class TextSurveyAnswer extends SurveyAnswer
    {

        private final String textAnswer;

        public TextSurveyAnswer(StepResult result)
        {
            super(result);
            textAnswer = (String) result.getResult();
        }
    }

    public static class DateSurveyAnswer extends SurveyAnswer
    {

        private final String dateAnswer;

        public DateSurveyAnswer(StepResult result)
        {
            super(result);
            Long dateResult = (Long) result.getResult();
            dateAnswer = dateResult == null ? null : FormatHelper.DEFAULT_FORMAT.format(new Date(dateResult));
        }
    }
}
