package org.researchstack.backbone.ui.step.body;

import android.app.TimePickerDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.ResUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Static Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*


    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<Long> result;
    private DateAnswerFormat format;
    private Calendar calendar;
    private DateFormat dateformatter;
    private int defaultHour = 0;
    private int defaultMinute = 0;
    private boolean hasChosenDate;

    public DateQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (DateAnswerFormat) this.step.getAnswerFormat();

        this.calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (format.getStyle() == AnswerFormat.DateAnswerStyle.DateAndTime) {
            this.dateformatter = FormatHelper.getFormat(DateFormat.MEDIUM, DateFormat.MEDIUM);
        } else if (format.getStyle() == AnswerFormat.DateAnswerStyle.Date) {
            this.dateformatter = FormatHelper.getFormat(DateFormat.MEDIUM, FormatHelper.NONE);
        } else if (format.getStyle() == AnswerFormat.DateAnswerStyle.TimeOfDay) {
            this.dateformatter = FormatHelper.getFormat(FormatHelper.NONE, DateFormat.MEDIUM);
        }

        // First check the result and restore last picked date
        Long savedTimeInMillis = this.result.getResult();
        if (savedTimeInMillis != null) {
            calendar.setTimeInMillis(savedTimeInMillis);
            hasChosenDate = true;
        }

        // If no result, use default date if available
        else if (format.getDefaultDate() != null) {
            calendar.setTime(format.getDefaultDate());
            hasChosenDate = true;
        }

        // otherwise, make sure user has made a selection before moving on
        else {
            hasChosenDate = false;
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.rsb_item_date_view, parent, false);

        TextView title = view.findViewById(R.id.label);
        if (viewType == VIEW_TYPE_COMPACT) {
            title.setText(step.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        TextView textView = view.findViewById(R.id.value);
        textView.setSingleLine(true);
        if (step.getPlaceholder() != null) {
            textView.setHint(step.getPlaceholder());
        } else {
            if (format.getStyle() == AnswerFormat.DateAnswerStyle.Date) {
                textView.setHint(R.string.rsb_hint_step_body_date);
            } else if (format.getStyle() == AnswerFormat.DateAnswerStyle.TimeOfDay) {
                textView.setHint(R.string.rsb_hint_step_body_time);
            } else if (format.getStyle() == AnswerFormat.DateAnswerStyle.DateAndTime) {
                textView.setHint(R.string.rsb_hint_step_body_datetime);
            }
        }

        if (result.getResult() != null) {
            textView.setText(createFormattedResult());
        }

        textView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDialog(textView);
            }
        });

        textView.setOnClickListener(v -> {
            if (v.isFocused()) {
                showDialog(textView);
            }
        });

        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        return view;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            result.setResult(calendar.getTimeInMillis());
        }

        return result;
    }

    /**
     * @return {@link BodyAnswer#VALID} if result date is between min and max (inclusive) date set
     * within the Step.AnswerFormat
     */
    @Override
    public BodyAnswer getBodyAnswerState() {
        if (!hasChosenDate) {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_date_none);
        }

        return format.validateAnswer(calendar.getTime());
    }

    private void showDialog(TextView tv) {
        // need to find a material date picker, since it's not in the support library
        if (format.getStyle() == AnswerFormat.DateAnswerStyle.Date) {
            showDatePicker(tv);
        } else {
            if(calendar != null) {
                defaultHour = calendar.get(Calendar.HOUR);
                defaultMinute = calendar.get(Calendar.MINUTE);
            }
            if (format.getStyle() == AnswerFormat.DateAnswerStyle.TimeOfDay) {
                showTimePicker(tv);
            } else if (format.getStyle() == AnswerFormat.DateAnswerStyle.DateAndTime) {
                showDatePicker(tv);
            } else {
                throw new RuntimeException("DateAnswerStyle " + format.getStyle() + " is not recognised");
            }
        }
    }


    private void showDatePicker(TextView tv) {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTheme( ResUtils.resolveOrThrow(tv.getContext(), R.attr.materialCalendarTheme));

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if(format.getMinimumDate() != null) {
            constraintsBuilder.setStart(format.getMinimumDate().getTime());
        }
        if(format.getMaximumDate() != null) {
            constraintsBuilder.setEnd(format.getMaximumDate().getTime());
        }
        if(calendar != null) {
            if(format.getMinimumDate() != null && calendar.getTime().before(format.getMinimumDate())) {
                calendar.setTime(format.getMinimumDate());
            } else {
                if(format.getMaximumDate() != null &&
                        calendar.getTime().after(format.getMaximumDate())) {
                    calendar.setTime(format.getMaximumDate());
                }
            }
            builder.setSelection(calendar.getTimeInMillis());
            constraintsBuilder.setOpenAt(calendar.getTimeInMillis());
        }

        builder.setCalendarConstraints(constraintsBuilder.build());
        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(
                selection -> {
                    calendar.setTime(new Date(selection));
                    if (format.getStyle() == AnswerFormat.DateAnswerStyle.Date) {
                        hasChosenDate = true;
                        // Set result to our edit text
                        String formattedResult = createFormattedResult();
                        tv.setText(formattedResult);
                    } else {
                        showTimePicker(tv);
                    }
                    isStepEmpty.postValue(false);
                }
        );
        if (tv.getContext() instanceof FragmentActivity) {
            picker.show(((FragmentActivity) tv.getContext()).getSupportFragmentManager(), picker.toString());
        }
    }

    private void showTimePicker(TextView tv) {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(tv.getContext(),
                R.style.Theme_Backbone);
        TimePickerDialog timePickerDialog = new TimePickerDialog(contextWrapper,
                (currentTimePicker, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    hasChosenDate = true;
                    // Set result to our edit text
                    String formattedResult = createFormattedResult();
                    tv.setText(formattedResult);
                },
                defaultHour,
                defaultMinute,
                true);
        timePickerDialog.show();
    }

    private String createFormattedResult() {
        dateformatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateformatter.format(calendar.getTime());
    }

}
