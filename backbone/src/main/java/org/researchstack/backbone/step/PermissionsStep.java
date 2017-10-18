package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.PermissionStepLayout;

/**
 * Created by TheMDP on 1/4/17.
 */

public class PermissionsStep extends Step {

    /* Default constructor needed for serilization/deserialization of object */
    PermissionsStep() {
        super();
    }

    public PermissionsStep(String identifier, String title, String text) {
        super(identifier, title);
        setText(text);
    }

    @Override
    public Class getStepLayoutClass() {
        return PermissionStepLayout.class;
    }
}
