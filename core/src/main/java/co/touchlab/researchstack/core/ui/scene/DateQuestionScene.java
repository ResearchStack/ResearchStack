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

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.utils.FormatUtils;

public class DateQuestionScene extends SceneImpl<String>
{

    public DateQuestionScene(Context context)
    {
        super(context);
    }

    public DateQuestionScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DateQuestionScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        SimpleDateFormat format = new SimpleDateFormat(
                FormatUtils.DATE_FORMAT_ISO_8601);

        DatePicker datePicker = (DatePicker) inflater.inflate(R.layout.item_date_picker, parent, false);
        DateAnswerFormat answerFormat = (DateAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();

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
        String savedFrmtdDate = getStepResult().getResultForIdentifier(StepResult.DEFAULT_KEY);
        if (!TextUtils.isEmpty(savedFrmtdDate))
        {
            try
            {
                Date savedDate = format.parse(savedFrmtdDate);
                calendar.setTime(savedDate);
            }
            catch(ParseException e)
            {
                throw new RuntimeException(e);
            }
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

            String resultFormattedDate = format.format(resultCalendar.getTime());
            getStepResult().setResult(resultFormattedDate);
        });

        return datePicker;
    }

}
