package co.touchlab.researchstack.core.step;


import co.touchlab.researchstack.core.ui.scene.InstructionScene;

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
        return InstructionScene.class;
    }
}
