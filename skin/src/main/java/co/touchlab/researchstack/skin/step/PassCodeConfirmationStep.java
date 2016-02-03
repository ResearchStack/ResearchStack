package co.touchlab.researchstack.skin.step;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.ui.layout.SignUpPinCodeConfirmationStepLayout;

public class PassCodeConfirmationStep extends Step
{
    private String pin;

    public PassCodeConfirmationStep(String identifier)
    {
        super(identifier);
    }

    @Override
    public int getStepTitle()
    {
        return R.string.passcode;
    }

    @Override
    public Class getStepLayoutClass()
    {
        return SignUpPinCodeConfirmationStepLayout.class;
    }

    public void setPin(String pin)
    {
        this.pin = pin;
    }

    public String getPin()
    {
        return pin;
    }
}
