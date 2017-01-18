package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.PasscodeCreationStepLayout;
import org.researchstack.backbone.ui.step.layout.SignUpPinCodeCreationStepLayout;

/**
 * Created by TheMDP on 1/4/17.
 */

public class PasscodeStep extends Step {

    public int stateOrdinal = - 1;

    /* Default constructor needed for serilization/deserialization of object */
    PasscodeStep() {
        super();
    }

    public PasscodeStep(String identifier, String title, String text) {
        super(identifier, title);
        setText(text);
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
    public Class getStepLayoutClass() {
        return PasscodeCreationStepLayout.class;
    }
}
