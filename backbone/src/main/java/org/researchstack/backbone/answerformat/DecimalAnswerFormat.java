package org.researchstack.backbone.answerformat;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.TextUtils;

/**
 * This class defines the attributes for a decimal answer format that participants enter using a
 * numeric keyboard.
 * <p>
 * If you specify maximum or minimum values and the user enters a value outside the specified range,
 * the DecimalQuestionBody does not allow navigation until the participant provides a value that is
 * within the valid range.
 */
public class DecimalAnswerFormat extends AnswerFormat {
    private float minValue;
    private float maxValue;

    /**
     * Creates an answer format with the specified min and max values
     *
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value, or 0f for unlimited
     */
    public DecimalAnswerFormat(float minValue, float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Decimal;
    }

    /**
     * Returns the min value
     *
     * @return returns the min value
     */
    public float getMinValue() {
        return minValue;
    }

    /**
     * Returns the max value, or 0f for no maximum
     *
     * @return returns the max value, or 0f for no maximum
     */
    public float getMaxValue() {
        return maxValue;
    }

    public BodyAnswer validateAnswer(String inputString) {
        // If no answer is recorded
        if (inputString == null || TextUtils.isEmpty(inputString)) {
            return BodyAnswer.INVALID;
        } else {
            // Parse value from editText
            Float floatAnswer = Float.valueOf(inputString);
            if (floatAnswer < minValue) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_under, String.valueOf(getMinValue()));
            } else if (floatAnswer > maxValue) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_over, String.valueOf(getMaxValue()));
            }
        }

        return BodyAnswer.VALID;
    }
}
