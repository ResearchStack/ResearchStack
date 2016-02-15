package co.touchlab.researchstack.skin.step;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.skin.ui.layout.SignUpPinCodeCreationStepLayout;

public class PassCodeCreationStep extends Step
{

    public PassCodeCreationStep(String identifier, int title)
    {
        super(identifier);
        setStepTitle(title);
    }

    @Override
    public Class getStepLayoutClass()
    {
        return SignUpPinCodeCreationStepLayout.class;
    }
}
