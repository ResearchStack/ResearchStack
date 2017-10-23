package org.researchstack.backbone.step.active;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.utils.StepHelper;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.List;

/**
 * Created by TheMDP on 2/5/17.
 *
 * Class enabled an ActiveStep to be navigable with a custom NavigationRule
 */

public class NavigationActiveStep extends ActiveStep implements NavigableOrderedTask.NavigationRule {

    private List<NavigableOrderedTask.ObjectEqualsNavigationRule> customRules;

    /* Default constructor needed for serilization/deserialization of object */
    NavigationActiveStep() {
        super();
    }

    public NavigationActiveStep(String identifier) {
        super(identifier);
    }

    public NavigationActiveStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
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
