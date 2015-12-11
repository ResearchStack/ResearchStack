package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public class InstructionScene extends SceneImpl<Void>
{
    public InstructionScene(Context context, Step step, StepResult result)
    {
        super(context,
                step,
                result);
    }
}
