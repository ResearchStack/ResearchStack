package org.researchstack.backbone.step.tracked;

import java.io.Serializable;

/**
 * Created by TheMDP on 3/21/17.
 */

public class TrackedDataObject implements Serializable {

    public TrackedDataObject() {
        super();
    }

    /**
     * Is this data object being tracked with follow-up questions?
     */
    private boolean tracking;

    /**
     * Frequency of taking/doing (if applicable)
     */
    private int frequency;

    /**
     * Whether or not the frequency range should be used. Default = false
     */
    private boolean usesFrequencyRange;

    /**
     * Localized text to display as the full descriptor. Default = identifier.
     */
    private String text;

    /**
     * Localized shortened text to display when used in a sentence. Default = identifier.
     */
    private String shortText;

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isUsesFrequencyRange() {
        return usesFrequencyRange;
    }

    public void setUsesFrequencyRange(boolean usesFrequencyRange) {
        this.usesFrequencyRange = usesFrequencyRange;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }
}
