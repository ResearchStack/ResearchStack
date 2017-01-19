package org.researchstack.backbone.step;

import org.researchstack.backbone.answerformat.FormAnswerFormat;

import java.util.Arrays;
import java.util.List;

/**
 * The FormStep class is a concrete subclass of {@link Step}, used for presenting multiple questions
 * on a single scrollable page.
 * <p>
 * To use FormStep, instantiate the object, give it a list of {@link QuestionStep}s, and include it
 * in a task.
 * <p>
 * The result of a form step is an {@link org.researchstack.backbone.result.StepResult} object that
 * includes a child StepResult object for each form item.
 */
public class FormStep extends QuestionStep {
    private List<QuestionStep> formSteps;

    public FormStep(String identifier, String title, String text) {
        super(identifier, title, new FormAnswerFormat());
        setText(text);
    }

    /**
     * Returns the list of items in the form.
     *
     * @return a list of QuestionSteps in the form
     */
    public List<QuestionStep> getFormSteps() {
        return formSteps;
    }

    public void setFormSteps(QuestionStep... formSteps) {
        setFormSteps(Arrays.asList(formSteps));
    }

    public void setFormSteps(List<QuestionStep> formSteps) {
        this.formSteps = formSteps;
    }
}
