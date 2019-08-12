package org.researchstack.backbone.answerformat;

/**
 * Created by Priyanka.
 */
public class DropDownAnswerFormat extends AnswerFormat {
    private String[] list;
    private int step;

    public DropDownAnswerFormat(int step,String[] list) {
        this.step = step;
        this.list = list.clone();
    }


    @Override
    public QuestionType getQuestionType() {
        return Type.DropDown;
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
     * Returns a copy of the choice array
     *
     * @return a copy of the choices for this question
     */
    public String[] getList() {
        return list.clone();
    }

}
