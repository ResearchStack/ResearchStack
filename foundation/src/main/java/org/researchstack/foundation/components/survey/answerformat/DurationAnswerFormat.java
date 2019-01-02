package org.researchstack.backbone.answerformat;

/**
 * Created by Dario Salvi on 29/04/2016.
 */
public class DurationAnswerFormat extends AnswerFormat {
    private String unit;
    private int step;

    public DurationAnswerFormat(int step, String unit) {
        this.step = step;
        this.unit = unit;
    }


    @Override
    public QuestionType getQuestionType() {
        return Type.Duration;
    }

    /**
     * Returns the step of the duration
     *
     * @return the step
     */
    public int getStep() {
        return step;
    }

    /**
     * Returns the unit of measurement
     *
     * @return returns the unit
     */
    public String egtUnit() {
        return unit;
    }

}
