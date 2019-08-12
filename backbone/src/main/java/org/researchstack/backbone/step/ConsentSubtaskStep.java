package org.researchstack.backbone.step;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

/**
 * Created by TheMDP on 1/14/17.
 */

public class ConsentSubtaskStep extends SubtaskStep implements NavigableOrderedTask.NavigationSkipRule {

    /* Default constructor needed for serilization/deserialization of object */
    ConsentSubtaskStep() {
        super();
    }

    public ConsentSubtaskStep(String identifier) {
        super(identifier);
    }

    public ConsentSubtaskStep(String identifier, String title) {
        super(identifier, title);
    }

    public ConsentSubtaskStep(String identifier, List<Step> steps) {
        super(identifier, steps);
    }

    @Override
    public boolean shouldSkipStep(TaskResult result, List<TaskResult> additionalTaskResults) {
        return DataProvider.getInstance().isConsented();
    }
}
