package org.sagebionetworks.researchstack.backbone.ui.callbacks;

import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.step.Step;

public interface StepCallbacks {
    int ACTION_PREV = -1;
    int ACTION_NONE = 0;
    int ACTION_NEXT = 1;
    int ACTION_END = 2;
    int ACTION_REFRESH = 3;  // step layout should be refreshed

    void onSaveStep(int action, Step step, StepResult result);

    @Deprecated
    void onCancelStep();
}
