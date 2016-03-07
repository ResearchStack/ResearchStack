package org.researchstack.backbone.step;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.ui.step.layout.ConsentVisualStepLayout;

public class ConsentVisualStep extends Step
{
    private ConsentSection section;

    @Deprecated //TODO ViewTaskActivity should be handling this
    private String nextButtonString;

    public ConsentVisualStep(String identifier)
    {
        super(identifier);
    }

    @Override
    public boolean isShowsProgress()
    {
        return false;
    }

    @Override
    public int getStepTitle()
    {
        return org.researchstack.backbone.R.string.rsb_consent;
    }

    @Override
    public Class getStepLayoutClass()
    {
        return ConsentVisualStepLayout.class;
    }

    public ConsentSection getSection()
    {
        return section;
    }

    public void setSection(ConsentSection section)
    {
        this.section = section;
    }

    @Deprecated
    public String getNextButtonString()
    {
        return nextButtonString;
    }

    @Deprecated
    public void setNextButtonString(String nextButtonString)
    {
        this.nextButtonString = nextButtonString;
    }
}
