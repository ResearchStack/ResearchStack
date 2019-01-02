package org.researchstack.foundation.components.common.ui.callbacks;

import org.jetbrains.annotations.Nullable;
import org.researchstack.foundation.core.interfaces.IResult;
import org.researchstack.foundation.core.interfaces.IStep;

public interface StepCallbacks {
    int ACTION_PREV = -1;
    int ACTION_NONE = 0;
    int ACTION_NEXT = 1;
    int ACTION_END = 2;

    void onSaveStep(int action, IStep step, @Nullable IResult result);

    @Deprecated
    void onCancelStep();
}
