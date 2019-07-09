package org.researchstack.foundation.components.survey.ui.body;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.researchstack.foundation.R;
import org.researchstack.foundation.components.survey.answerformat.DurationAnswerFormat;
import org.researchstack.foundation.components.survey.step.QuestionStep;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;

/**
 * Implementation of the question type "duration".
 * <p>
 * This is an example of the JSON used in the definition of the question
 * "constraints":{
 * "durationUnit":"minutes",
 * "step":15,
 * "dataType":"duration",
 * "type":"DurationConstraints"
 * }
 * the "durationUnit" and "step" are not used in this implementation.
 * <p>
 * The answer to this question (the StepResult) is given as the total number of minutes.
 */
public class DurationQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    //Total number of minutes
    private StepResult<Integer> result;
    private DurationAnswerFormat format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int viewType;
    private Spinner hoursSpinner;
    private Spinner minutesSpinner;

    public DurationQuestionBody(Step step, StepResult<Integer> result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
        this.format = (DurationAnswerFormat) this.step.getAnswerFormat();
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        this.viewType = viewType;

        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsf_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsf_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent) {
        if ((viewType == VIEW_TYPE_DEFAULT) || (viewType == VIEW_TYPE_COMPACT)) {
            return initView(inflater, parent);
        } else {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initView(LayoutInflater inflater, ViewGroup parent) {
        View v = inflater.inflate(R.layout.rsf_item_edit_duration, parent, false);

        String[] hoursStrs = new String[24];
        for (int i = 0; i < 24; i++) hoursStrs[i] = String.valueOf(i);
        ArrayAdapter<String> hoursChoices = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_spinner_item, hoursStrs);
        hoursChoices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner = (Spinner) v.findViewById(R.id.hours);
        hoursSpinner.setAdapter(hoursChoices);

        String[] minutesStrs = new String[60];
        for (int i = 0; i < 60; i++) minutesStrs[i] = String.valueOf(i);
        ArrayAdapter<String> minutesChoices = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_spinner_item, minutesStrs);
        minutesChoices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesSpinner = (Spinner) v.findViewById(R.id.minutes);
        minutesSpinner.setAdapter(minutesChoices);

        Integer result = this.result.getResult();
        if (result != null) {
            hoursSpinner.setSelection(result / 60);
            minutesSpinner.setSelection(result % 60);
        }

        return v;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            int hours = hoursSpinner.getSelectedItemPosition();
            int minutes = minutesSpinner.getSelectedItemPosition();
            result.setResult((hours * 60) + minutes);
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        //with a spinner the value is always valid,
        //unless we want to enforce that the user presses the spinner at least once

        return BodyAnswer.VALID;
    }

}
