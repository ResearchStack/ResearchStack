package org.researchstack.backbone.step;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.utils.StepHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 1/3/17.
 */

public class NavigationSubtaskStep extends SubtaskStep implements NavigableOrderedTask.NavigationRule {

    String skipToStepIdentifier;
    boolean skipIfPassed;

    /* Default constructor needed for serilization/deserialization of object */
    NavigationSubtaskStep() {
        super();
    }

    public NavigationSubtaskStep(String identifier) {
        super(identifier);
    }

    public NavigationSubtaskStep(String identifier, String title) {
        super(identifier, title);
    }

    public NavigationSubtaskStep(String identifier, List<Step> steps) {
        super(identifier, steps);
    }

    public NavigationSubtaskStep(Task task) {
        super(task);
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

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        List<QuestionStep> stepList = new ArrayList<>();
        for (String id : result.getResults().keySet()) {
            Step step = getStepWithIdentifier(id);
            if (step != null && step instanceof QuestionStep) {
                stepList.add((QuestionStep) step);
            }
        }
        return StepHelper.navigationFormStepSkipIdentifier(
                skipToStepIdentifier, skipIfPassed, stepList, result, additionalTaskResults);
    }
}
