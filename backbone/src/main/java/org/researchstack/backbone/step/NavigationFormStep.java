package org.researchstack.backbone.step;

import android.util.Log;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.utils.StepHelper;

import java.util.List;

/**
 * Created by TheMDP on 1/3/17.
 */

public class NavigationFormStep extends FormStep implements NavigableOrderedTask.NavigationRule {

    private static final String LOG_TAG = NavigationFormStep.class.getSimpleName();

    private String skipToStepIdentifier;
    private boolean skipIfPassed;

    /* Default constructor needed for serilization/deserialization of object */
    NavigationFormStep() {
        super();
    }

    public NavigationFormStep(String identifier, String title, String text) {
        super(identifier, title, text);
    }

    public NavigationFormStep(String identifier, String title, String text, List<QuestionStep> steps) {
        super(identifier, title, text, steps);
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
        return StepHelper.navigationFormStepSkipIdentifier(
                skipToStepIdentifier, skipIfPassed, formSteps, result, additionalTaskResults);
    }
}
