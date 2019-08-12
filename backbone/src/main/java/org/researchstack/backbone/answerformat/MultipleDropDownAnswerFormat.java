package org.researchstack.backbone.answerformat;

import java.util.ArrayList;

/**
 * Created by Priyanka.
 */
public class MultipleDropDownAnswerFormat extends AnswerFormat {
    private ArrayList<String[]> list;
    private int step;

    public MultipleDropDownAnswerFormat(int step, ArrayList<String[]> list) {
        this.step = step;
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }


    @Override
    public QuestionType getQuestionType() {
        return Type.MultipleDropDown;
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
    public ArrayList<String[]> getList() {
        return list;
    }

}
