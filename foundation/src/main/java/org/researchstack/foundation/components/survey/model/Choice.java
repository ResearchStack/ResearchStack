package org.researchstack.backbone.model;

import java.io.Serializable;

/**
 * Choice objects for use in {@link org.researchstack.backbone.answerformat.ChoiceAnswerFormat}.
 * They typically have an integer or string value, always with a string text representation of the
 * choice for the user.
 *
 * @param <T> the type of value for the choice, usually Integer or String
 */
public class Choice<T> implements Serializable {
    private String text;

    private T value;

    private String detailText;

    /**
     * Creates a choice object with the provided text and value, detailtext is null
     *
     * @param text  user-facing text representing the choice
     * @param value value of any type for this choice, type should match other choices in the step
     */
    public Choice(String text, T value) {
        this(text, value, null);
    }

    /**
     * Creates a choice object with the provided text, value, and detailtext
     *
     * @param text       user-facing text representing the choice
     * @param value      value of any type for this choice, type should match other choices in the
     *                   step
     * @param detailText extra detail text for the choice
     */
    public Choice(String text, T value, String detailText) {
        this.text = text;
        this.value = value;
        this.detailText = detailText;
    }

    /**
     * Return the user-facing text for this choice
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text that the user will see for this choice
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the value of type T for this choice
     *
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the value of type T for this choice
     *
     * @param value the value of this choice
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Gets the detail text for this choice, or null
     *
     * @return the detail text, or null if none
     */
    public String getDetailText() {
        return detailText;
    }

    /**
     * Sets the (optional) detail text for this choice
     *
     * @param detailText the detail text
     */
    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }
}
