package org.researchstack.backbone.step.active;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

/**
 * Created by TheMDP on 2/5/17.
 *
 * Class enabled an ActiveStep to be navigable with a custom NavigationRule
 */

public class NavigationActiveStep extends ActiveStep implements NavigableOrderedTask.NavigationRule {

    private NavigableOrderedTask.NavigationRule customRule;

    public NavigationActiveStep(String identifier) {
        super(identifier);
    }

    public NavigationActiveStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        if (customRule != null) {
            return customRule.nextStepIdentifier(result, additionalTaskResults);
        }
        return null;
    }

    public NavigableOrderedTask.NavigationRule getCustomRule() {
        return customRule;
    }

    public void setCustomRule(NavigableOrderedTask.NavigationRule customRule) {
        this.customRule = customRule;
    }
}
