package org.researchstack.feature.consent.step;

import org.researchstack.feature.consent.R;
import org.researchstack.feature.consent.model.ConsentSection;
import org.researchstack.feature.consent.ui.layout.ConsentVisualStepLayout;
import org.researchstack.foundation.core.models.step.Step;

/**
 * The {@link ConsentVisualStep} class represents a step in the visual consent sequence.
 * <p>
 * In the ResearchStack framework, an {@link ConsentVisualStep} object is used to present a simple
 * graphic to help study participants understand the content of an informed consent document.
 */
public class ConsentVisualStep extends Step {
    private ConsentSection section;

    @Deprecated
    private String nextButtonString;

    public ConsentVisualStep(String identifier) {
        super(identifier);
    }

    @Override
    public int getStepTitle() {
        return R.string.rsfc_consent;
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

    @Deprecated
    public String getNextButtonString() {
        return nextButtonString;
    }

    @Deprecated
    public void setNextButtonString(String nextButtonString) {
        this.nextButtonString = nextButtonString;
    }
}
