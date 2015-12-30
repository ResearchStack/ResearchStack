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
    View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step);

    View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step);

    StepResult getStepResult();

    void prefillResult(StepResult result);

    boolean isAnswerValid();

    // TODO how do we make this better? the step body needs the identifier for its StepResult
    // TODO in form steps and uses StepResult.DEFAULT_KEY in normal question steps
    String getIdentifier();

    void setIdentifier(String identifier);
}
