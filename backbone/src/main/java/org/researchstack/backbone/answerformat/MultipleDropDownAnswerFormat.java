package org.researchstack.backbone.answerformat;

/**
 * Created by Priyanka.
 */
public class MultipleDropDownAnswerFormat extends AnswerFormat {
    private String[] list1;
    private String[] list2;
    private String[] list3;
    private int step;

    public MultipleDropDownAnswerFormat(int step, String[] list1, String[] list2, String[] list3) {
        this.step = step;
        this.list1 = list1.clone();
        this.list2 = list2.clone();
        this.list3 = list3.clone();
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
    public String[] getList1() {
        return list1.clone();
    }

    /**
     * Returns a copy of the choice array
     *
     * @return a copy of the choices for this question
     */
    public String[] getList2() {
        return list2.clone();
    }

    /**
     * Returns a copy of the choice array
     *
     * @return a copy of the choices for this question
     */
    public String[] getList3() {
        return list3.clone();
    }

}
