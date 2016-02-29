package co.touchlab.researchstack.skin.step;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.skin.ui.layout.SignUpPinCodeCreationStepLayout;

public class PassCodeCreationStep extends Step
{

    public int stateOrdinal = -1;

    public PassCodeCreationStep(String identifier, int title)
    {
        super(identifier);
        setStepTitle(title);
    }

    public int getStateOrdinal()
    {
        return stateOrdinal;
    }

    public void setStateOrdinal(int stateOrdinal)
    {
        this.stateOrdinal = stateOrdinal;
    }

    @Override
    public Class getStepLayoutClass()
    {
        return SignUpPinCodeCreationStepLayout.class;
    }
}
