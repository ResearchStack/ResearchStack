package org.researchstack.backbone.step;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.utils.StepHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 12/31/16.
 *
 * This QuestionStep works by having a specific expected answer that must be correct or incorrect to
 * move to the skipToStepIdentifier, with the correct/incorrect status depending on skipIfPassed
 */

public class NavigationExpectedAnswerQuestionStep extends QuestionStep implements NavigableOrderedTask.NavigationRule {

    private static final String LOG_TAG = NavigationExpectedAnswerQuestionStep.class.getCanonicalName();

    String skipToStepIdentifier;
    boolean skipIfPassed;

    /** Expected answer for this QuestionStep, used by NavigableOrderedTask */
    private Object expectedAnswer;

    /* Default constructor needed for serilization/deserialization of object */
    NavigationExpectedAnswerQuestionStep() {
        super();
    }

    public NavigationExpectedAnswerQuestionStep(String identifier) {
        super(identifier);
    }

    public NavigationExpectedAnswerQuestionStep(String identifier, String title) {
        super(identifier, title);
    }

    public NavigationExpectedAnswerQuestionStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
    }

    public String getSkipToStepIdentifier() {
        return skipToStepIdentifier;
    }

    public void setSkipToStepIdentifier(String identifier) {
        skipToStepIdentifier = identifier;
    }

    public boolean getSkipIfPassed() {
        return skipIfPassed;
    }

    public void setSkipIfPassed(boolean skipIfPassed) {
        this.skipIfPassed = skipIfPassed;
    }

    /**
     * @param expectedAnswer the expected answer for this QuestionStep
     */
    public void setExpectedAnswer(Object expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    /**
     * @return expectedAnswer, which is usually null, but used with NavigableOrderedTask reads steps
     */
    public Object getExpectedAnswer() {
        return expectedAnswer;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        List<QuestionStep> stepList = new ArrayList<>();
        stepList.add(this);
        return StepHelper.navigationFormStepSkipIdentifier(
                skipToStepIdentifier, skipIfPassed, stepList, result, additionalTaskResults);
    }
}
