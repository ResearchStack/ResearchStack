package org.researchstack.backbone.step;

/**
 * Created by TheMDP on 1/4/17.
 */

public class PasscodeStep extends Step {

    /* Default constructor needed for serilization/deserialization of object */
    PasscodeStep() {
        super();
    }

    public PasscodeStep(String identifier, String title, String text) {
        super(identifier, title);
        setText(text);
    }

    @Override
    public Class getStepLayoutClass() {
        // TODO: need custom CreatePasscodeStepLayout, one exists, but is in Skin module
        return super.getStepLayoutClass();
    }
}
