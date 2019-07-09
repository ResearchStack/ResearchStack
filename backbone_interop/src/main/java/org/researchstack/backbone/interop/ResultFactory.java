package org.researchstack.backbone.interop;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.foundation.core.interfaces.IResult;

public interface ResultFactory {
    StepResult create(IResult result);
    IResult create (StepResult result);
}
