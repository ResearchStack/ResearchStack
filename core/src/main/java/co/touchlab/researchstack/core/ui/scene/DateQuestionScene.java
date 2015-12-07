package co.touchlab.researchstack.core.ui.scene;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;

public class DateQuestionScene extends SceneImpl<String>
{

    public DateQuestionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
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

        Calendar today = Calendar.getInstance();
        int initYear = today.get(Calendar.YEAR);
        int initMonth = today.get(Calendar.MONTH);
        int initDay = today.get(Calendar.DAY_OF_MONTH);

        if (answerFormat.getDefaultDate() != null)
        {
            Calendar defDate = Calendar.getInstance();
            defDate.setTime(answerFormat.getDefaultDate());

            initYear = defDate.get(Calendar.YEAR);
            initMonth = defDate.get(Calendar.MONTH);
            initDay = defDate.get(Calendar.DAY_OF_MONTH);
        }

        datePicker.init(initYear, initMonth, initDay, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            StepResult<String> result = getStepResult();
            result.setResultForIdentifier(StepResult.DEFAULT_KEY, calendar.toString());
            setStepResult(result);
        });

        return datePicker;
    }

}
