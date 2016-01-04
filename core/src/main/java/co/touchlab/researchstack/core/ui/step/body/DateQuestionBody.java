package co.touchlab.researchstack.core.ui.step.body;

import android.app.DatePickerDialog;
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
import co.touchlab.researchstack.core.utils.FormatHelper;

public class DateQuestionBody implements StepBody
{
    private QuestionStep step;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601,
            Locale.getDefault());
    private DateAnswerFormat format;
    private Calendar         calendar;
    private DatePicker       datePicker;
    private String identifier = StepResult.DEFAULT_KEY;

    public DateQuestionBody()
    {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        datePicker = (DatePicker) inflater.inflate(R.layout.item_date_picker, parent, false);

        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        format = (DateAnswerFormat) step.getAnswerFormat();

        datePicker.setCalendarViewShown(false);

        if(format.getMinimumDate() != null)
        {
            datePicker.setMinDate(format.getMinimumDate().getTime());
        }

        if(format.getMaximumDate() != null)
        {
            datePicker.setMaxDate(format.getMaximumDate().getTime());
        }

        initCalendar();

        int initYear = calendar.get(Calendar.YEAR);
        int initMonth = calendar.get(Calendar.MONTH);
        int initDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(initYear, initMonth, initDay, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
        });

        return datePicker;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        View formItemView = inflater.inflate(R.layout.scene_form_item, parent, false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        TextView textView = (TextView) formItemView.findViewById(R.id.value);

        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        format = (DateAnswerFormat) step.getAnswerFormat();

        initCalendar();

        RxView.clicks(textView).subscribe(o -> {
            showDialog(textView);
        });

        return formItemView;
    }

    @Override
    public StepResult getStepResult()
    {
        StepResult<String> result = new StepResult<>(identifier);
        result.setResult(createFormattedResult());
        return result;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        //Set initial state
        String savedFrmtdDate = (String) result.getResult();
        if(! TextUtils.isEmpty(savedFrmtdDate))
        {
            Date savedDate = getDateFromString(savedFrmtdDate);
            calendar.setTime(savedDate);
            if(datePicker != null)
            {
                datePicker.updateDate(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
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

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    private void showDialog(TextView textView)
    {
        // TODO use same view as initView() and just set the dialog's view to it?
        new DatePickerDialog(textView.getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    String formattedResult = createFormattedResult();
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
        catch(ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String createFormattedResult()
    {
        return dateFormat.format(calendar.getTime());
    }

    private void initCalendar()
    {
        calendar = Calendar.getInstance();

        if(format.getDefaultDate() != null)
        {
            Date dfltDate = format.getDefaultDate();
            calendar.setTime(dfltDate);
        }
    }
}
