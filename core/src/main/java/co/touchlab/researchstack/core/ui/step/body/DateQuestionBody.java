package co.touchlab.researchstack.core.ui.step.body;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.views.NoShowImeEditText;
import co.touchlab.researchstack.core.utils.FormatHelper;
import co.touchlab.researchstack.core.utils.ViewUtils;

public class DateQuestionBody implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Static Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private static final DateFormat DATE_FORMAT = FormatHelper.getFormat(DateFormat.SHORT,
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
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsc_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsc_margin_right);
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
        View compactView = inflater.inflate(R.layout.item_edit_text, parent, false);

        TextView label = (TextView) compactView.findViewById(R.id.text);
        label.setVisibility(View.VISIBLE);
        label.setText(step.getTitle());

        NoShowImeEditText editText = (NoShowImeEditText) compactView.findViewById(R.id.value);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                ViewUtils.hideSoftInputMethod(editText.getContext());

                showDialog(editText);
            }
        });

        editText.setIsTextEdittingEnalbed(false);

        return compactView;
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

    private void showDialog(EditText editText)
    {
        InputMethodManager imm = (InputMethodManager) editText.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        // TODO use same view as getBodyView() and just set the dialog's view to it?
        new DatePickerDialog(editText.getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);

                    // Set result to our edit text
                    String formattedResult = createFormattedResult();
                    editText.setText(formattedResult);

                    // Search for next focusable view request focus
                    View next = editText.getParent().focusSearch(editText, View.FOCUS_DOWN);
                    if(next != null)
                    {
                        next.requestFocus();
                    }
                    else
                    {
                        ViewUtils.hideSoftInputMethod(editText.getContext());
                    }
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
