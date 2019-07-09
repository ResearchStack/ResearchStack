package org.researchstack.feature.authentication.pincode.step;

import org.researchstack.feature.authentication.pincode.ui.SignUpPinCodeCreationStepLayout;
import org.researchstack.foundation.core.models.step.UIStep;

public class PassCodeCreationStep extends UIStep {

    public int stateOrdinal = -1;

    public PassCodeCreationStep(String identifier, int title) {
        super(identifier);
        //todo joliu fix
//        setStepTitle(title);
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
