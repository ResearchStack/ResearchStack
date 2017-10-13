package org.researchstack.backbone.step;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.model.navigable.StepNavigationRule;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

/**
 * Created by mauriciosouto on 11/10/17.
 */

public class MedNavigationQuestionStep extends QuestionStep implements NavigableOrderedTask.NavigationRule {

    private StepNavigationRule rule;

    MedNavigationQuestionStep() {
        super();
    }

    public MedNavigationQuestionStep(String identifier) {
        super(identifier);
    }

    public MedNavigationQuestionStep(String identifier, String title) {
        super(identifier, title);
    }

    public MedNavigationQuestionStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
    }

    public MedNavigationQuestionStep(String identifier, String title, AnswerFormat format, StepNavigationRule rule) {
        super(identifier, title, format);
        this.rule = rule;
    }

    public StepNavigationRule getRule() {
        return rule;
    }

    public void setRule(StepNavigationRule rule) {
        this.rule = rule;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        if (rule != null) {
            return rule.nextStepIdentifier(result, additionalTaskResults);
        }
        return null;
    }
}
