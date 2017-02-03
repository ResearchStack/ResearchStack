package org.researchstack.backbone.step;

import android.support.annotation.Nullable;

import org.researchstack.backbone.ui.step.layout.FingerprintStepLayout;
import org.researchstack.backbone.ui.step.layout.InstructionStepLayout;

/**
 * Created by TheMDP on 2/1/17.
 */

public class FingerprintStep extends InstructionStep {

    private boolean isCreationStep = false;

    /* Default constructor needed for serilization/deserialization of object */
    FingerprintStep() {
        super();
    }

    /**
     * @param identifier for the step
     * @param title for the step
     * @param detailText for the step
     * @param isCreationStep if true, the user will register their fingerprint, if false,
     *                       the user will have to verify their already registered fingerprint
     */
    public FingerprintStep(String identifier, @Nullable String title, @Nullable String detailText, boolean isCreationStep) {
        super(identifier, title, detailText);
        setOptional(false);
        this.isCreationStep = isCreationStep;
    }

    public boolean isCreationStep() {
        return isCreationStep;
    }

    @Override
    public Class getStepLayoutClass() {
        return FingerprintStepLayout.class;
    }
}
