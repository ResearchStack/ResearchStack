package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.SignUpPinCodeCreationStepLayout;

public class PassCodeCreationStep extends Step {

    public int stateOrdinal = -1;

    public PassCodeCreationStep(String identifier, int title) {
        super(identifier);
        setStepTitle(title);
    }

    public int getStateOrdinal() {
        return stateOrdinal;
    }

    public void setStateOrdinal(int stateOrdinal) {
        this.stateOrdinal = stateOrdinal;
    }

    @Override
    public Class getStepLayoutClass() {
        return SignUpPinCodeCreationStepLayout.class;
    }
}
