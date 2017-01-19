package org.researchstack.backbone.step;

import org.researchstack.backbone.task.Task;

import java.io.Serializable;

/**
 * Step is the base class for the steps that can compose a task for presentation in an {@link
 * org.researchstack.backbone.ui.ViewTaskActivity} object. Each Step object represents one logical
 * piece of data entry or activity in a larger task.
 * <p>
 * A step can be a question, an active test, or a simple instruction. An Step subclass is usually
 * paired with an {@link org.researchstack.backbone.ui.step.layout.StepLayout} subclass that
 * displays the step.
 * <p>
 * To use a step, instantiate an Step object and populate its properties. Add the step to a task,
 * such as an {@link org.researchstack.backbone.task.OrderedTask} object, and then present the task
 * using a ViewTaskActivity.
 * <p>
 * To implement a new type of step, subclass Step and add your additional properties. Separately,
 * subclass StepLayout and implement your user interface.
 */
public class Step implements Serializable {
    private String identifier;

    private Class stepLayoutClass;

    private int stepTitle;

    private boolean optional = true;

    private String title;

    private String text;

    // The following fields are in RK but not implemented in ResearchStack
    // These options can be developed as needed or removed if we find they are not necessary
    private boolean restorable;
    private Task task;
    private boolean shouldTintImages;
    private boolean showsProgress;
    private boolean allowsBackNavigation;
    private boolean useSurveyMode;

    /**
     * Returns a new step initialized with the specified identifier.
     *
     * @param identifier The unique identifier of the step.
     */
    public Step(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns a new step initialized with the specified identifier and title.
     *
     * @param identifier The unique identifier of the step.
     * @param title      The primary text to display for this step.
     */
    public Step(String identifier, String title) {
        this.identifier = identifier;
        this.title = title;
    }

    /**
     * A short string that uniquely identifies the step within the task.
     * <p>
     * The identifier is reproduced in the results of a step. In fact, the only way to link a result
     * (a {@link org.researchstack.backbone.result.StepResult} object) to the step that generated it
     * is to look at the value of <code>identifier</code>. To accurately identify step results, you
     * need to ensure that step identifiers are unique within each task.
     * <p>
     * In some cases, it can be useful to link the step identifier to a unique identifier in a
     * database; in other cases, it can make sense to make the identifier human readable.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * A boolean value indicating whether the user can skip the step without providing an answer.
     * <p>
     * The default value of this property is <code>true</code>. When the value is
     * <code>false</code>, the Skip button does not appear on this step.
     * <p>
     * This property may not be meaningful for all steps; for example, an active step might not
     * provide a way to skip, because it requires a timer to finish.
     *
     * @return a boolean indicating whether the step is skippable
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Sets whether the step is skippable
     *
     * @param optional
     * @see #isOptional()
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * The primary text to display for the step in a localized string.
     * <p>
     * This text is also used as the label when a step is shown in the more compact version in a
     * {@link FormStep}.
     *
     * @return the primary text for the question
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the primary text to display for the step in a localized string.
     *
     * @param title the primary text for the question.
     * @see #getTitle()
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Additional text to display for the step in a localized string.
     * <p>
     * The additional text is displayed in a smaller font below <code>title</code>. If you need to
     * display a long question, it can work well to keep the title short and put the additional
     * content in the <code>text</code> property.
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the additional text for the step.
     *
     * @param text the detail text for the step
     * @see #getText()
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the int id for the title to display in the action bar (optional).
     *
     * @return the id for the title to display in the action bar
     */
    public int getStepTitle() {
        return stepTitle;
    }

    /**
     * Gets the int id for the title to display in the action bar (optional).
     *
     * @param stepTitle the Android resource id for the title
     */
    public void setStepTitle(int stepTitle) {
        this.stepTitle = stepTitle;
    }

    /**
     * Returns the class that the {@link org.researchstack.backbone.ui.ViewTaskActivity} should
     * instantiate to display this step.
     * <p>
     * This method is used within the framework so that steps can define their step view controller
     * pairing.
     * <p>
     * Outside the framework, developers should instantiate the required view controller in their
     * ViewTaskActivity delegate to override the ViewTaskActivity's default.
     *
     * @return the class of the {@link org.researchstack.backbone.ui.step.layout.StepLayout} for
     * this step
     */
    public Class getStepLayoutClass() {
        return stepLayoutClass;
    }

    /**
     * Sets the class that should be used to display this step
     *
     * @param stepLayoutClass the {@link org.researchstack.backbone.ui.step.layout.StepLayout} class
     *                        to be used to display this step
     */
    public void setStepLayoutClass(Class stepLayoutClass) {
        this.stepLayoutClass = stepLayoutClass;
    }
}
