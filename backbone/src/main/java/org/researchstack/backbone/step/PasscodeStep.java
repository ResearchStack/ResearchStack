package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.FingerprintStepLayout;
import org.researchstack.backbone.ui.step.layout.PasscodeCreationStepLayout;
import org.researchstack.backbone.ui.step.layout.SignUpPinCodeCreationStepLayout;

/**
 * Created by TheMDP on 1/4/17.
 */

public class PasscodeStep extends InstructionStep {

    private boolean useFingerprint = false;

    public int stateOrdinal = - 1;

    /* Default constructor needed for serilization/deserialization of object */
    PasscodeStep() {
        super();
    }

    public PasscodeStep(String identifier, String title, String text) {
        super(identifier, title, text);
    }

    public int getStateOrdinal()
    {
        return stateOrdinal;
    }

    public void setStateOrdinal(int stateOrdinal)
    {
        this.stateOrdinal = stateOrdinal;
    }

    /**
     * @return true if UI will be FingerprintStepLayout, false if UI will be PasscodeCreationStepLayout
     */
    public boolean getUseFingerprint()
    {
        return useFingerprint;
    }

    /**
     * @param useFingerprint true if UI will be FingerprintStepLayout, false if UI will be PasscodeCreationStepLayout
     */
    public void setUseFingerprint(boolean useFingerprint)
    {
        this.useFingerprint = useFingerprint;
    }

    @Override
    public Class getStepLayoutClass() {
        if (!useFingerprint) {
            return PasscodeCreationStepLayout.class;
        } else {
            return FingerprintStepLayout.class;
        }
    }
}
