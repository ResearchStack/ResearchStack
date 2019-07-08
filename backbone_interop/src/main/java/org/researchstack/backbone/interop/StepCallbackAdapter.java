package org.researchstack.backbone.interop;

import org.researchstack.backbone.ui.callbacks.StepCallbacks;

public interface StepCallbackAdapter {
    StepCallbacks create (org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks stepCallbacks);
}
