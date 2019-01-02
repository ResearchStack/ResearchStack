package org.researchstack.backbone.task;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The OrderedTask class implements all the methods in the {@link Task} protocol and represents a
 * task that assumes a fixed order for its steps.
 * <p>
 * In the ResearchStack framework, any simple sequential task, such as a survey or an active task,
 * can be represented as an ordered task.
 * <p>
 * If you want further custom conditional behaviors in a task, it can be easier to subclass
 * OrderedTask or NavigableOrderedTask and override particular {@link Task} methods than it
 * is to implement a new Task subclass directly. Override the methods {@link #getStepAfterStep} and
 * {@link #getStepBeforeStep}, and call super for all other methods.
 */
public class OrderedTask extends Task implements Serializable {

    protected List<Step> steps;

    /**
     * Returns an initialized ordered task using the specified identifier and array of steps.
     *
     * @param identifier The unique identifier for the task.
     * @param steps      An array of {@link Step} objects in the order in which they should be
     *                   presented.
     */
    public OrderedTask(String identifier, List<Step> steps) {
        super(identifier);
        this.steps = new ArrayList<>(steps);
    }

    /**
     * Returns an initialized ordered task using the specified identifier and array of steps.
     *
     * @param identifier The unique identifier for the task
     * @param steps      The {@link Step} objects in the order in which they should be presented.
     */
    public OrderedTask(String identifier, Step... steps) {
        this(identifier, Arrays.asList(steps));
    }

    /**
     * Returns the next step immediately after the passed in step in the list of steps, or null
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> after the passed in step, or null if at the end
     */
    @Override
    public Step getStepAfterStep(Step step, TaskResult result) {
        if (step == null) {
            return steps.get(0);
        }

        int nextIndex = steps.indexOf(step) + 1;

        if (nextIndex < steps.size()) {
            return steps.get(nextIndex);
        }

        return null;
    }

    /**
     * Returns the next step immediately before the passed in step in the list of steps, or null
     *
     * @param step   The reference step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> before the passed in step, or null if at the
     * start
     */
    @Override
    public Step getStepBeforeStep(Step step, TaskResult result) {
        int nextIndex = steps.indexOf(step) - 1;

        if (nextIndex >= 0) {
            return steps.get(nextIndex);
        }

        return null;
    }

    @Override
    public Step getStepWithIdentifier(String identifier) {
        for (Step step : steps) {
            if (identifier.equals(step.getIdentifier())) {
                return step;
            }
        }
        return null;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result) {
        int current = step == null ? -1 : steps.indexOf(step);
        return new TaskProgress(current, steps.size());
    }

    /**
     * Returns {@link Step#getStepTitle()} if it exists, otherwise returns string showing progress.
     *
     * @param context for fetching resources
     * @param step    the current step
     * @return the step title, or a progress string if it doesn't have one
     */
    @Override
    public String getTitleForStep(Context context, Step step) {
        String title = super.getTitleForStep(context, step);
        if (TextUtils.isEmpty(title)) {
            int currentIndex = steps.indexOf(step);
            title = context.getString(R.string.rsb_format_step_title,
                    currentIndex + 1,
                    steps.size());
        }
        return title;
    }

    /**
     * Validates that there are no duplicate identifiers in the list of steps
     *
     * @throws org.researchstack.backbone.task.Task.InvalidTaskException
     */
    @Override
    public void validateParameters() {
        Set<String> uniqueIds = new HashSet<>();
        for (Step step : steps) {
            uniqueIds.add(step.getIdentifier());
        }

        if (uniqueIds.size() != steps.size()) {
            throw new InvalidTaskException("OrderedTask has steps with duplicate ids");
        }
    }

    /**
     * Returns a copy of the list of steps.
     *
     * @return a copy of the ordered list of steps in the task
     */
    public List<Step> getSteps() {
        return new ArrayList<>(steps);
    }
}
