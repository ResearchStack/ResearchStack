package org.researchstack.backbone.answerformat;

import android.text.InputType;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.TextUtils;
import org.w3c.dom.Text;

/**
 * This class defines the attributes for an integer answer format that participants enter using a
 * numeric keyboard.
 * <p>
 * If you specify maximum or minimum values and the user enters a value outside the specified range,
 * the {@link org.researchstack.backbone.ui.step.body.IntegerQuestionBody} does not allow navigation
 * until the participant provides a value that is within the valid range.
 */
public class IntegerAnswerFormat extends TextAnswerFormat {
    private int maxValue;
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
    private int minValue;
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /* Default constructor needed for serilization/deserialization of object */
    public IntegerAnswerFormat() {
        super();
        commonInit();
    }

    /**
     * Creates an integer answer format with the specified min and max values.
     *
     * @param minValue minimum allowed value
     * @param maxValue maximum allowed value, 0 if no max
     */
    public IntegerAnswerFormat(int minValue, int maxValue) {
        this();
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    private void commonInit() {
        setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Integer;
    }

    /**
     * Returns the maximum allowed value for the question, Integer.MAX_VALUE if maxValue is 0
     *
     * @return the max value, Integer.MAX_VALUE if maxValue is 0
     */
    public int getMaxValue() {
        return (maxValue == 0) ? Integer.MAX_VALUE : maxValue;
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
