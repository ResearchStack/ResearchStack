package org.researchstack.backbone.step;

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
    public Class getStepLayoutClass()
    {
        // TODO: need custom PermissionStepLayout, one exists, but is in Skin module
        return super.getStepLayoutClass();
    }
}
