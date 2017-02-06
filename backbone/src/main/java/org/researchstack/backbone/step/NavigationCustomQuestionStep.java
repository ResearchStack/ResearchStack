package org.researchstack.backbone.step;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

/**
 * Created by TheMDP on 2/5/17.
 *
 * The NavigationCustomQuestionStep class allows the developer to implement any custom navigation rule
 * by allowing them to pass in the interface themselves for determining the nextStepIdentifier
 */

public class NavigationCustomQuestionStep extends QuestionStep implements NavigableOrderedTask.NavigationRule {
    private static final String LOG_TAG = NavigationExpectedAnswerQuestionStep.class.getCanonicalName();

    private NavigableOrderedTask.NavigationRule customRule;

    /* Default constructor needed for serilization/deserialization of object */
    NavigationCustomQuestionStep() {
        super();
    }

    public NavigationCustomQuestionStep(String identifier) {
        super(identifier);
    }

    public NavigationCustomQuestionStep(String identifier, String title) {
        super(identifier, title);
    }

    public NavigationCustomQuestionStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        if (customRule == null) {
            return null;
        }
        return customRule.nextStepIdentifier(result, additionalTaskResults);
    }

    public NavigableOrderedTask.NavigationRule getCustomRule() {
        return customRule;
    }

    public void setCustomRule(NavigableOrderedTask.NavigationRule customRule) {
        this.customRule = customRule;
    }
}
