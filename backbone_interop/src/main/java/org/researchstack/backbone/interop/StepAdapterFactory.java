package org.researchstack.backbone.interop;

import org.researchstack.backbone.step.Step;
import org.researchstack.foundation.core.interfaces.IStep;

public interface StepAdapterFactory {
    Step create(IStep step);
    IStep create(Step step);
}
