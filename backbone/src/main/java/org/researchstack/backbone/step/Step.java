package org.researchstack.backbone.step;

import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Step is the base class for the steps that can compose a task for presentation in an {@link
 * org.researchstack.backbone.ui.task.TaskActivity} object. Each Step object represents one logical
 * piece of data entry or activity in a larger task.
 * <p>
 * A step can be a question, an active test, or a simple instruction. An Step subclass is usually
 * paired with an {@link org.researchstack.backbone.ui.step.layout.StepLayout} subclass that
 * displays the step.
 * <p>
 * To use a step, instantiate an Step object and populate its properties. Add the step to a task,
 * such as an {@link org.researchstack.backbone.task.OrderedTask} object, and then present the task
 * using a {@link org.researchstack.backbone.ui.task.TaskActivity}.
 * <p>
 * To implement a new type of step, subclass Step and add your additional properties. Separately,
 * subclass StepLayout and implement your user interface.
 */
public class Step implements Serializable, Cloneable {
    private String identifier;
    private Class stepLayoutClass;
    private int stepTitle;
    private boolean optional = true;
    private boolean hidden = false;
    private String title;
    private String text;
    private String question;
    private int colorPrimary;
    private int colorPrimaryDark;
    private int colorSecondary;
    private int principalTextColor;
    private int secondaryTextColor;
    private int actionFailedColor;
    private String hiddenDefaultValue = null;
    private boolean isCompletionStep = false;

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
     * Sets whether the step can be skipped
     *
     * @param optional true if the step can be skipped by the user, false otherwise.
     * @see #isOptional()
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * A boolean value indicating whether the user will see this step or not.
     * <p>
     * The default value of this property is <code>false</code>. When the value is
     * <code>true</code>, the step is not display and the hiddenDefaultValue is used as value for
     * this step.
     * <p>
     * This property may not be meaningful for all steps;
     *
     * @return a boolean indicating whether the step is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets whether the step is a hidden step
     *
     * @param hidden a boolean indicating whether the step is a hidden step
     * @see #isHidden()
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * A boolean value indicating whether the step is a isCompletionStep step. If the task is a non
     * branching task, the task needs to stop after this step is completed. If the task is
     * branching, and the isCompletionStep step is the trigger for the branch, then the task shall
     * continue to the next step. If the task is branching, and the isCompletionStep step is not the
     * trigger for the branch, then task needs to stop.
     * <p>
     *
     * @return a boolean indicating whether the step is a isCompletionStep step
     */
    public boolean isCompletionStep() {
        return isCompletionStep;
    }

    /**
     * Sets whether the step is a isCompletionStep step
     *
     * @param isCompletionStep true when the step is a completion step, false otherwise.
     * @see #isCompletionStep()
     */
    public void isCompletionStep(boolean isCompletionStep) {
        this.isCompletionStep = isCompletionStep;
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
     * Question text to display for the step in a localized string.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Sets the question text for the step.
     *
     * @param question the question text for the step
     * @see #getQuestion()
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Returns the class that each step Fragment should instantiate to display this step.
     * <p>
     * This method is used within the framework so that steps can define their step view controller
     * pairing.
     * <p>
     * Outside the framework, developers should instantiate the required view controller in their
     * {@link org.researchstack.backbone.ui.task.TaskActivity} delegate to override the activity's default.
     *
     * @return the class of the {@link org.researchstack.backbone.ui.step.layout.StepLayout} for
     * this step
     */
    public Class getStepLayoutClass() {
        return stepLayoutClass;
    }

    public int getDestinationId() {
        return 0;
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

    /**
     * Sets the theme for the step.
     *
     * @param colorPrimary       the primary color for the step
     * @param colorPrimaryDark   the primary dark color for the step
     * @param colorSecondary     the accent color for the step
     * @param principalTextColor the principal text color for the step
     * @param secondaryTextColor the secondary text color for the step
     * @param actionFailedColor  the action failed color for the step
     */
    public void setStepTheme(int colorPrimary, int colorPrimaryDark, int colorSecondary,
                             int principalTextColor, int secondaryTextColor, int actionFailedColor) {
        this.colorPrimary = colorPrimary;
        this.colorPrimaryDark = colorPrimaryDark;
        this.colorSecondary = colorSecondary;
        this.principalTextColor = principalTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.actionFailedColor = actionFailedColor;
    }

    /**
     * Gets the the primary color for the step
     */
    public int getPrimaryColor() {
        return colorPrimary;
    }

    /**
     * Gets the the primary dark color for the step
     */
    public int getColorPrimaryDark() {
        return colorPrimaryDark;
    }

    /**
     * Gets the the accent color for the step
     */
    public int getColorSecondary() {
        return colorSecondary;
    }

    /**
     * Gets the the principal text color for the step
     */
    public int getPrincipalTextColor() {
        return principalTextColor;
    }

    /**
     * Gets the the secondary text color for the step
     */
    public int getSecondaryTextColor() {
        return secondaryTextColor;
    }

    /**
     * Gets the the action failed color for the step
     */
    public int getActionFailedColor() {
        return actionFailedColor;
    }

    public void setHiddenDefaultValue(String defaultValue) {
        this.hiddenDefaultValue = defaultValue;
    }

    /**
     * Gets the default value use when the step is hide
     */
    public String getHiddenDefaultValue() {
        return hiddenDefaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Step step = (Step) o;
        return Objects.equals(identifier, step.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    /**
     * Deep copies a Step, except for the {@link #stepLayoutClass}, which is going to point to the
     * same reference. Steps cannot mutate to a different class type, and the {@link Class} object
     * is used read-only (via getter), so this is acceptable. There is a setter, only used in
     * Consent step types, which do not make use of this clone interface.
     * <p>
     * If this Step class is re-written, this exception should be removed and the references be
     * totally immutable when possible.
     *
     * @return a new instance of a {@link Step} object, populated with the values of the previous
     * one.
     *
     * @throws CloneNotSupportedException if some type doesn't implement Cloneable.
     */
    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        Step cloned = (Step) super.clone();

        cloned.identifier = identifier;
        cloned.stepLayoutClass = stepLayoutClass; //copied by reference (public getter only)

        return cloned;
    }
}
