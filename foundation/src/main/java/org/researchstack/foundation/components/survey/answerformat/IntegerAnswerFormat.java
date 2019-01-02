package org.researchstack.backbone.answerformat;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.TextUtils;

/**
 * This class defines the attributes for an integer answer format that participants enter using a
 * numeric keyboard.
 * <p>
 * If you specify maximum or minimum values and the user enters a value outside the specified range,
 * the {@link org.researchstack.backbone.ui.step.body.IntegerQuestionBody} does not allow navigation
 * until the participant provides a value that is within the valid range.
 */
public class IntegerAnswerFormat extends AnswerFormat {
    private int maxValue;
    private int minValue;

    /**
     * Creates an integer answer format with the specified min and max values.
     *
     * @param minValue minimum allowed value
     * @param maxValue maximum allowed value, 0 if no max
     */
    public IntegerAnswerFormat(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Integer;
    }

    /**
     * Returns the maximum allowed value for the question, 0 if no max
     *
     * @return the max value, 0 if no max
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Returns the minimum allowed value for the question
     *
     * @return returns the minimum allowed value for the question
     */
    public int getMinValue() {
        return minValue;
    }

    public BodyAnswer validateAnswer(String inputString) {

        // If no answer is recorded
        if (TextUtils.isEmpty(inputString)) {
            return BodyAnswer.INVALID;
        } else {
            // Parse value from editText
            Integer intAnswer = Integer.valueOf(inputString);
            if (intAnswer < getMinValue()) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_under,
                        String.valueOf(getMinValue()));
            } else if (intAnswer > getMaxValue()) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_over,
                        String.valueOf(getMaxValue()));
            }

        }

        return BodyAnswer.VALID;
    }
}
