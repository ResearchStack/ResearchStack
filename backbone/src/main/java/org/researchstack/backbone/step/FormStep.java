package org.researchstack.backbone.step;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.FormAnswerFormat;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.ui.step.layout.FormStepLayout;
import org.researchstack.backbone.ui.step.layout.SurveyStepLayout;

import java.util.ArrayList;
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
    List<QuestionStep> formSteps;

    private String skipTitle;

    /* Default constructor needed for serialization/deserialization of object */
    public FormStep() {
        super();
    }

    public FormStep(String identifier, String title, String text) {
        super(identifier, title, new FormAnswerFormat());
        setText(text);
    }

    public FormStep(String identifier, String title, String text, List<QuestionStep> steps) {
        this(identifier, title, text);
        formSteps = steps;
    }

    /**
     * If true, the first question body layout with an edittext will receive focus on load
     * default is false and nothing will occur
     */
    private boolean autoFocusFirstEditText;

    public boolean isAutoFocusFirstEditText() {
        return autoFocusFirstEditText;
    }

    public void setAutoFocusFirstEditText(boolean autoFocusFirstEditText) {
        this.autoFocusFirstEditText = autoFocusFirstEditText;
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

    /**
     * @return The title of the skip button, default will be localized "Skip"
     */
    public String getSkipTitle() {
        return skipTitle;
    }

    /**
     * @param skipTitle The title of the skip button, default will be localized "Skip"
     */
    public void setSkipTitle(String skipTitle) {
        this.skipTitle = skipTitle;
    }

    @Override
    public Class getStepLayoutClass() {
        return FormStepLayout.class;
    }
}
