package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.TappingIntervalStepLayout;

/**
 * Created by TheMDP on 2/23/17.
 */

public class TappingIntervalStep extends ActiveStep {

    /* Default constructor needed for serialization/deserialization of object */
    TappingIntervalStep() {
        super();
    }

    public TappingIntervalStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public TappingIntervalStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setShouldShowDefaultTimer(false);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass() {
        return TappingIntervalStepLayout.class;
    }
}
