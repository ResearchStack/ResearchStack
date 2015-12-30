package co.touchlab.researchstack.core.step;


import co.touchlab.researchstack.core.ui.step.layout.InstructionStepLayout;

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
