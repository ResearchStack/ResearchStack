package co.touchlab.researchstack.core.ui.scene;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;

public class DateQuestionScene extends SceneImpl<String>
{

    public DateQuestionScene(Context context, Step step, StepResult result)
    {
        super(context, step, result);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        SimpleDateFormat format = new SimpleDateFormat(
                StorageManager.DATE_FORMAT_ISO_8601);

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

            // TODO Move the following out of here -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
            // it should be set in a "finalizeStepResult" method that is called when
            // onNextClicked is called.
            StepResult<String> result = getStepResult();
            String resultFormattedDate = format.format(resultCalendar.getTime());
            result.setResultForIdentifier(StepResult.DEFAULT_KEY, resultFormattedDate);
            setStepResult(result);
        });

        return datePicker;
    }

}
