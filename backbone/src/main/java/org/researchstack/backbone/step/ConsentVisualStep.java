package org.researchstack.backbone.step;

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

    /**
     * The index of the section within all the visual consent sections
     */
    private int sectionIndex;
    /**
     * The total count of the visual consent sections
     */
    private int sectionCount;

    @Deprecated
    private String nextButtonString;

    /* Default constructor needed for serilization/deserialization of object */
    protected ConsentVisualStep() {
        super();
    }

    public ConsentVisualStep(String identifier, int index, int count) {
        super(identifier);
        setSectionIndex(index);
        setSectionCount(count);
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

    @Deprecated
    public String getNextButtonString() {
        return nextButtonString;
    }

    @Deprecated
    public void setNextButtonString(String nextButtonString) {
        this.nextButtonString = nextButtonString;
    }

    public int getSectionIndex() {
        return sectionIndex;
    }

    public void setSectionIndex(int sectionIndex) {
        this.sectionIndex = sectionIndex;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }
}
