package org.researchstack.foundation.components.common.ui.callbacks;

import org.jetbrains.annotations.Nullable;
import org.researchstack.foundation.core.interfaces.IResult;
import org.researchstack.foundation.core.interfaces.IStep;

public class StepCallbacks {
    public static final int ACTION_PREV = -1;
    public static final int ACTION_NONE = 0;
    public static final int ACTION_NEXT = 1;
    public static final int ACTION_END = 2;

    public void onSaveStep(int action, IStep step, @Nullable IResult result) {
        throw new UnsupportedOperationException();
    }
    @Deprecated
    public void onCancelStep() {
        throw new UnsupportedOperationException();
    }
}
