package co.touchlab.researchstack.core.ui.scene;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

/**
 * Created by bradleymcdermott on 12/22/15.
 */
public interface StepBody
{
    View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult stepResult);

    StepResult getStepResult();

    boolean isAnswerValid();
}
