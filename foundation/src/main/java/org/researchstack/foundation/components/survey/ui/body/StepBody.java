package org.researchstack.foundation.components.survey.ui.body;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.researchstack.foundation.core.models.result.StepResult;

public interface StepBody {
    int VIEW_TYPE_DEFAULT = 0;

    int VIEW_TYPE_COMPACT = 1;

    View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent);

    StepResult getStepResult(boolean skipped);

    BodyAnswer getBodyAnswerState();

}
