package org.researchstack.backbone.ui.step.body;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateQuestionBody implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Static Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private static final DateFormat DATE_FORMAT = FormatHelper.getFormat(DateFormat.MEDIUM,
            FormatHelper.NONE);

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep     step;
    private StepResult<Long> result;
    private DateAnswerFormat format;
    private Calendar         calendar;

    private boolean hasChosenDate;

    public DateQuestionBody(Step step, StepResult result)
    {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (DateAnswerFormat) this.step.getAnswerFormat();
        this.calendar = Calendar.getInstance();

        // First check the result and restore last picked date
        Long savedTimeInMillis = this.result.getResult();
        if(savedTimeInMillis != null)
        {
            calendar.setTimeInMillis(savedTimeInMillis);
            hasChosenDate = true;
        }

        // If no result, use default date if available
        else if(format.getDefaultDate() != null)
        {
            calendar.setTime(format.getDefaultDate());
            hasChosenDate = true;
        }

        // otherwise, make sure user has made a selection before moving on
        else
        {
            hasChosenDate = false;
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.rsb_item_date_view, parent, false);

        TextView title = (TextView) view.findViewById(R.id.label);
        if (viewType == VIEW_TYPE_COMPACT)
        {
            title.setText(step.getTitle());
        }
        else
        {
            title.setVisibility(View.GONE);
        }

        TextView textView = (TextView) view.findViewById(R.id.value);
        textView.setSingleLine(true);
        textView.setHint(R.string.rsb_hint_step_body_date);

        if(result.getResult() != null)
        {
            textView.setText(createFormattedResult());
        }

        textView.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                showDialog(textView);
            }
        });

        textView.setOnClickListener(v -> {
            if(v.isFocused())
            {
                showDialog(textView);
            }
        });

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    @Override
    public StepResult getStepResult(boolean skipped)
    {
        if(skipped)
        {
            result.setResult(null);
        }
        else
        {
            result.setResult(calendar.getTimeInMillis());
        }

        return result;
    }

    /**
     * @return {@link BodyAnswer#VALID} if result date is between min and max (inclusive) date set
     * within the Step.AnswerFormat
     */
    @Override
    public BodyAnswer getBodyAnswerState()
    {
        Date minDate = format.getMinimumDate();
        Date maxDate = format.getMaximumDate();
        Date resultDate = calendar.getTime();

        if(! hasChosenDate)
        {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_default);
        }

        if(minDate != null && resultDate.getTime() < minDate.getTime())
        {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_date_under);
        }

        if(maxDate != null && resultDate.getTime() > maxDate.getTime())
        {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_date_over);
        }

        return BodyAnswer.VALID;
    }

    private void showDialog(TextView tv)
    {
        // need to find a material date picker, since it's not in the support library
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(tv.getContext(),
                R.style.Theme_Backbone);
        new DatePickerDialog(contextWrapper,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    hasChosenDate = true;

                    // Set result to our edit text
                    String formattedResult = createFormattedResult();
                    tv.setText(formattedResult);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String createFormattedResult()
    {
        return DATE_FORMAT.format(calendar.getTime());
    }

}
