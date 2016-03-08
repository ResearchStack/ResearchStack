package org.researchstack.backbone.ui.step.body;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
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

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int viewType;

    public DateQuestionBody(Step step, StepResult result)
    {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
        this.format = (DateAnswerFormat) this.step.getAnswerFormat();
        this.calendar = Calendar.getInstance();

        // First check the result and restore last picked date
        Long savedTimeInMillis = this.result.getResult();
        if(savedTimeInMillis != null)
        {
            calendar.setTimeInMillis(savedTimeInMillis);
        }

        // If no result, use default date
        else if(format.getDefaultDate() != null)
        {
            calendar.setTime(format.getDefaultDate());
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        this.viewType = viewType;

        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        if(viewType == VIEW_TYPE_DEFAULT)
        {
            return initViewDefault(inflater, parent);
        }
        else if(viewType == VIEW_TYPE_COMPACT)
        {
            return initViewCompact(inflater, parent);
        }
        else
        {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initViewDefault(LayoutInflater inflater, ViewGroup parent)
    {
        DatePicker datePicker = (DatePicker) inflater.inflate(R.layout.item_date_picker,
                parent,
                false);
        datePicker.setCalendarViewShown(false);

        if(format.getMinimumDate() != null)
        {
            datePicker.setMinDate(format.getMinimumDate().getTime());
        }

        if(format.getMaximumDate() != null)
        {
            datePicker.setMaxDate(format.getMaximumDate().getTime());
        }

        int initYear = calendar.get(Calendar.YEAR);
        int initMonth = calendar.get(Calendar.MONTH);
        int initDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(initYear, initMonth, initDay, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
        });

        return datePicker;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent)
    {
        View formItemView = inflater.inflate(R.layout.item_text_view_compact, parent, false);

        TextView title = (TextView) formItemView.findViewById(R.id.label);
        title.setText(step.getTitle());

        TextView textView = (TextView) formItemView.findViewById(R.id.value);
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

        return formItemView;
    }


    @Override
    public StepResult getStepResult()
    {
        result.setResult(calendar.getTimeInMillis());
        return result;
    }

    /**
     * @return true if result date is >= min and <= max date set within the Step.AnswerFormat
     */
    @Override
    public boolean isAnswerValid()
    {
        // TODO possible to start with no selection?
        Date minDate = format.getMinimumDate();
        Date maxDate = format.getMaximumDate();

        Date resultDate = calendar.getTime();

        if(minDate != null && resultDate.getTime() < minDate.getTime())
        {
            return false;
        }

        if(maxDate != null && resultDate.getTime() > maxDate.getTime())
        {
            return false;
        }

        return true;
    }

    private void showDialog(TextView tv)
    {
        new DatePickerDialog(tv.getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);

                    // Set result to our edit text
                    String formattedResult = createFormattedResult();
                    tv.setText(formattedResult);

                    // Search for next focusable view request focus
                    // View next = tv.getParent().focusSearch(tv, View.FOCUS_DOWN);
                    // if(next != null)
                    // {
                    //     next.requestFocus();
                    // }
                    // else
                    // {
                    //     ViewUtils.hideSoftInputMethod(tv.getContext());
                    // }
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
