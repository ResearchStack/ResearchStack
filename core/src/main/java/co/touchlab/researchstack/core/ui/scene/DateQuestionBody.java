package co.touchlab.researchstack.core.ui.scene;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

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
    private StepResult<String> stepResult;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(FormatUtils.DATE_FORMAT_ISO_8601, Locale.getDefault());

    public DateQuestionBody()
    {
    }

    private StepResult<String> createStepResult(String identifier)
    {
        return new StepResult<>(identifier);
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult result)
    {
        if (result == null)
        {
            result = createStepResult(StepResult.DEFAULT_KEY);
        }

        stepResult = (StepResult<String>) result;

        DatePicker datePicker = (DatePicker) inflater.inflate(R.layout.item_date_picker, parent, false);
        DateAnswerFormat answerFormat = (DateAnswerFormat) step.getAnswerFormat();

        datePicker.setCalendarViewShown(false);

        if (answerFormat.getMinimumDate() != null)
        {
            datePicker.setMinDate(answerFormat.getMinimumDate().getTime());
        }

        if (answerFormat.getMaximumDate() != null)
        {
            datePicker.setMaxDate(answerFormat.getMaximumDate().getTime());
        }

        Calendar calendar = Calendar.getInstance();

        //Set initial state
        String savedFrmtdDate = stepResult.getResult();
        if (!TextUtils.isEmpty(savedFrmtdDate))
        {
            Date savedDate = getDateFromString(savedFrmtdDate);
            calendar.setTime(savedDate);
        }
        else if (answerFormat.getDefaultDate() != null)
        {
            Date dfltDate = answerFormat.getDefaultDate();
            calendar.setTime(dfltDate);
        }

        int initYear = calendar.get(Calendar.YEAR);
        int initMonth = calendar.get(Calendar.MONTH);
        int initDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(initYear, initMonth, initDay, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar resultCalendar = Calendar.getInstance();
            resultCalendar.set(Calendar.YEAR, year);
            resultCalendar.set(Calendar.MONTH, monthOfYear);
            resultCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String resultFormattedDate = dateFormat.format(resultCalendar.getTime());
            stepResult.setResult(resultFormattedDate);
        });

        return datePicker;
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

    @Override
    public StepResult getStepResult()
    {
        return stepResult;
    }

    @Override
    public boolean isAnswerValid()
    {
        // TODO validate actual date with answer format
//        Date date = getDateFromString(stepResult.getResult());
        return stepResult.getResult() != null;
    }
}
