package org.researchstack.backbone.step;

import org.researchstack.backbone.model.survey.NavigationStep;
import org.researchstack.backbone.task.Task;

import java.util.List;

/**
 * Created by TheMDP on 1/3/17.
 */

public class NavigationSubtaskStep extends SubtaskStep implements NavigationStep {

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

    @Override
    public String getSkipToStepIdentifier() {
        return skipToStepIdentifier;
    }

    @Override
    public void setSkipToStepIdentifier(String identifier) {
        skipToStepIdentifier = identifier;
    }

    @Override
    public boolean getSkipIfPassed() {
        return skipIfPassed;
    }

    @Override
    public void setSkipIfPassed(boolean skipIfPassed) {
        this.skipIfPassed = skipIfPassed;
    }
}
