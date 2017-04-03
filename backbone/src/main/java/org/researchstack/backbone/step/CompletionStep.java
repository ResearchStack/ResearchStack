package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.InstructionStepLayout;
import org.researchstack.backbone.utils.ResUtils;

/**
 * Created by TheMDP on 12/31/16.
 */

public class CompletionStep extends InstructionStep {

    /* Default constructor needed for serilization/deserialization of object */
    public CompletionStep() {
        super();
        commonInit();
    }

    public CompletionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setImage(ResUtils.ANIMATED_CHECK_MARK_DELAYED);
        setIsImageAnimated(true);
    }

    @Override
    public Class getStepLayoutClass() {
        return InstructionStepLayout.class;
    }
}
