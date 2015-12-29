package co.touchlab.researchstack.core.ui.scene;

import android.support.annotation.Nullable;
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
    View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier);

    View initializeCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier);

    StepResult getStepResult();

    boolean isAnswerValid();
}
