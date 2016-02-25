package co.touchlab.researchstack.backbone.step;


import co.touchlab.researchstack.backbone.ui.step.layout.InstructionStepLayout;

public class InstructionStep extends Step
{
    public InstructionStep(String identifier, String title, String detailText)
    {
        super(identifier, title);
        setText(detailText);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass()
    {
        return InstructionStepLayout.class;
    }
}
