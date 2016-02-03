package co.touchlab.researchstack.backbone.step;


import co.touchlab.researchstack.backbone.ui.step.layout.InstructionStepLayout;

public class InstructionStep extends Step
{
    public InstructionStep(String identifier, String title, String detailText)
    {
        super(identifier, title);
        setText(detailText);
    }

    @Override
    public Class getSceneClass()
    {
        return InstructionStepLayout.class;
    }
}
