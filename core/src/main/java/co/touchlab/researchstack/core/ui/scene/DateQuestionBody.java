package co.touchlab.researchstack.core.ui.scene;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.utils.FormatUtils;

public class DateQuestionBody implements StepBody
{
    private QuestionStep step;
    private StepResult<String> stepResult;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(FormatUtils.DATE_FORMAT_ISO_8601,
            Locale.getDefault());
    private DateAnswerFormat format;
    private Calendar calendar;

    public DateQuestionBody()
    {
    }

    private StepResult<String> createStepResult(String identifier)
    {
        return new StepResult<>(identifier);
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        DatePicker datePicker = (DatePicker) inflater.inflate(R.layout.item_date_picker,
                parent,
                false);

        if (result == null)
        {
            result = createStepResult(identifier);
        }

        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        stepResult = (StepResult<String>) result;
        format = (DateAnswerFormat) step.getAnswerFormat();

        datePicker.setCalendarViewShown(false);

        if (format.getMinimumDate() != null)
        {
            datePicker.setMinDate(format.getMinimumDate()
                    .getTime());
        }

        if (format.getMaximumDate() != null)
        {
            datePicker.setMaxDate(format.getMaximumDate()
                    .getTime());
        }

        initCalendar();

        int initYear = calendar.get(Calendar.YEAR);
        int initMonth = calendar.get(Calendar.MONTH);
        int initDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(initYear,
                initMonth,
                initDay,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year,
                            monthOfYear,
                            dayOfMonth);
                    String resultFormattedDate = createFormattedResult();
                    stepResult.setResult(resultFormattedDate);
                });

        return datePicker;
    }

    @NonNull
    private String createFormattedResult()
    {
        return dateFormat.format(calendar.getTime());
    }

    private void initCalendar()
    {
        calendar = Calendar.getInstance();

        //Set initial state
        String savedFrmtdDate = stepResult.getResult();
        if (!TextUtils.isEmpty(savedFrmtdDate))
        {
            Date savedDate = getDateFromString(savedFrmtdDate);
            calendar.setTime(savedDate);
        }
        else if (format.getDefaultDate() != null)
        {
            Date dfltDate = format.getDefaultDate();
            calendar.setTime(dfltDate);
        }
    }

    @Override
    public View initializeCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        View formItemView = inflater.inflate(R.layout.scene_form_item,
                parent,
                false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        TextView textView = (TextView) formItemView.findViewById(R.id.value);

        if (result == null)
        {
            result = createStepResult(identifier);
        }

        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        stepResult = (StepResult<String>) result;
        format = (DateAnswerFormat) step.getAnswerFormat();

        initCalendar();

        if (stepResult.getResult() != null)
        {
            textView.setText(stepResult.getResult());
        }

        RxView.clicks(textView)
                .subscribe(o -> {
                    showDialog(textView);
                });

        return formItemView;
    }

    private void showDialog(TextView textView)
    {
        new DatePickerDialog(textView.getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year,
                            monthOfYear,
                            dayOfMonth);
                    String formattedResult = createFormattedResult();
                    stepResult.setResult(formattedResult);
                    textView.setText(formattedResult);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private Date getDateFromString(String savedFrmtdDate)
    {
        try
        {
            return dateFormat.parse(savedFrmtdDate);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StepResult getStepResult()
    {
        return stepResult;
    }

    /**
     * @return true if result date is >= min and <= max date set within the Step.AnswerFormat
     */
    @Override
    public boolean isAnswerValid()
    {
        // Make sure we have a result
        String formattedDate = stepResult.getResult();
        if (TextUtils.isEmpty(formattedDate))
        {
            return false;
        }

        Date minDate = format.getMinimumDate();
        Date maxDate = format.getMaximumDate();

        Date resultDate = getDateFromString(formattedDate);

        return resultDate.getTime() >= minDate.getTime() &&
                resultDate.getTime() <= maxDate.getTime();
    }
}
