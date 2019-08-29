package org.sagebionetworks.researchstack.backbone.step;

import android.os.Build;

import org.sagebionetworks.researchstack.backbone.result.TaskResult;
import org.sagebionetworks.researchstack.backbone.task.NavigableOrderedTask;
import org.sagebionetworks.researchstack.backbone.ui.step.layout.PermissionStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 */

public class PermissionsStep extends Step implements NavigableOrderedTask.NavigationSkipRule {

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

    @Override
    public boolean shouldSkipStep(TaskResult result, List<TaskResult> additionalTaskResults) {
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }
}
