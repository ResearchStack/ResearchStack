package org.researchstack.backbone.step;

import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.ui.step.layout.ConsentVisualStepLayout;

/**
 * The {@link ConsentVisualStep} class represents a step in the visual consent sequence.
 * <p>
 * In the ResearchStack framework, an {@link ConsentVisualStep} object is used to present a simple
 * graphic to help study participants understand the content of an informed consent document.
 */
public class ConsentVisualStep extends Step {
    private ConsentSection section;

    public ConsentVisualStep(String identifier) {
        super(identifier);
    }

    @Override
    public int getDestinationId() {
        return R.id.rsb_consent_visual_step_fragment;
    }

    @Override
    public int getStepTitle() {
        return org.researchstack.backbone.R.string.rsb_consent;
    }

    @Override
    public Class getStepLayoutClass() {
        return ConsentVisualStepLayout.class;
    }

    /**
     * Returns the ConsentSection associated with this step.
     *
     * @return the consent section for this step
     */
    public ConsentSection getSection() {
        return section;
    }

    /**
     * Sets the ConsentSection for this step.
     * <p>
     * This includes everything needed by the {@link ConsentVisualStepLayout} to render the step.
     *
     * @param section the consent section
     */
    public void setSection(ConsentSection section) {
        this.section = section;
    }

    /**
     * Retrieve the string to use for the next button.
     *
     * @return The res id to use for the next button.
     */
    public int getNextButtonString() {
        return R.string.rsb_next;
    }
}
