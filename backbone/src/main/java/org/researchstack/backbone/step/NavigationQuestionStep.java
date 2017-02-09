package org.researchstack.backbone.step;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.List;

/**
 * Created by TheMDP on 2/5/17.
 *
 * The NavigationQuestionStep class allows the developer to implement any custom navigation rule
 * by allowing them to pass in the interface themselves for determining the nextStepIdentifier
 */

public class NavigationQuestionStep extends QuestionStep implements NavigableOrderedTask.NavigationRule {

    private List<NavigableOrderedTask.ObjectEqualsNavigationRule> customRules;

    /* Default constructor needed for serilization/deserialization of object */
    NavigationQuestionStep() {
        super();
    }

    public NavigationQuestionStep(String identifier) {
        super(identifier);
    }

    public NavigationQuestionStep(String identifier, String title) {
        super(identifier, title);
    }

    public NavigationQuestionStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
    }

    public void setCustomRules(List<NavigableOrderedTask.ObjectEqualsNavigationRule> customRules) {
        this.customRules = customRules;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        if (customRules != null) {
            for (NavigableOrderedTask.ObjectEqualsNavigationRule rule : customRules) {
                String nextIdentifier = rule.nextStepIdentifier(result, additionalTaskResults);
                if (nextIdentifier != null) {
                    return nextIdentifier;
                }
            }
        }
        return null;
    }
}
