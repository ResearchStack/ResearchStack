package org.researchstack.backbone.ui.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import org.researchstack.backbone.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by spiria on 22/3/18.
 */

public class MonthYearPickerDialog extends AlertDialog implements DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Date mDateValue;
    private int mYear = -1;
    private int mMonth = -1;

    private Date mMaxDate = null;
    private int mMaxYear = -1;
    private int mMaxMonth = 12;

    private Date mMinDate = null;
    private int mMinYear = 1900;
    private int mMinMonth = 1;

    private MonthYearPickerDialog.OnDateSetListener mDateSetListener;
    private View dialogView;

    public MonthYearPickerDialog(Context context) {
        super(context);

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        dialogView = inflater.inflate(R.layout.rsb_layout_month_year_picker, null);
        setView(dialogView);

        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.rsb_ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.rsb_cancel), this);

        final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.picker_year);

        Calendar cal = Calendar.getInstance();
        mMaxYear = cal.get(Calendar.YEAR) + 100;
        if (mMaxDate != null) {
            cal.setTime(mMaxDate);
            mMaxYear = cal.get(Calendar.YEAR);
            mMaxMonth = cal.get(Calendar.MONTH) + 1;
        }
        yearPicker.setMaxValue(mMaxYear);
        monthPicker.setMaxValue(mMaxMonth);

        if (mMinDate != null) {
            cal.setTime(mMinDate);
            mMinYear = cal.get(Calendar.YEAR);
            mMinMonth = cal.get(Calendar.MONTH) + 1;
        }
        yearPicker.setMinValue(mMinYear);

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateMonthPickerLimits(newVal);
            }
        });

        if (mYear > 0) {
            yearPicker.setValue(mYear);
        } else if (mMaxDate != null){
            yearPicker.setValue(mMaxYear);
        } else {
            Calendar c = Calendar.getInstance();
            yearPicker.setValue(c.get(Calendar.YEAR));
        }

        updateMonthPickerLimits(yearPicker.getValue());

        if (mMonth > 0) {
            monthPicker.setValue(mMonth);
        } else if (mMaxDate != null){
            monthPicker.setValue(mMaxMonth);
        } else {
            Calendar c = Calendar.getInstance();
            monthPicker.setValue(c.get(Calendar.MONTH) + 1);
        }
    }

    public void setOnDateSetListener(MonthYearPickerDialog.OnDateSetListener listener) {
        mDateSetListener = listener;
    }

    public void setCurrentDate(Date currentDate) {
        mDateValue = currentDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDateValue);
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH) + 1;
        if (dialogView != null) {
            final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.picker_month);
            final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.picker_year);
            yearPicker.setValue(mYear);
            monthPicker.setValue(mMonth);
        }
    }

    public void setMaxDate(Date maxDate) {
        this.mMaxDate = maxDate;
        Calendar cal = Calendar.getInstance();
        if (mMaxDate != null) {
            cal.setTime(mMaxDate);
            mMaxYear = cal.get(Calendar.YEAR);
            mMaxMonth = cal.get(Calendar.MONTH) + 1;
        } else {
            mMaxYear = cal.get(Calendar.YEAR) + 100;
            mMaxMonth = 12;
        }

        if (dialogView != null) {
            final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.picker_month);
            final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.picker_year);
            yearPicker.setMaxValue(mMaxYear);
            monthPicker.setMaxValue(mMaxMonth);
        }
        clearDateIfInvalid();
    }

    public void setMinDate(Date minDate) {
        this.mMinDate = minDate;
        Calendar cal = Calendar.getInstance();
        if (mMinDate != null) {
            cal.setTime(mMinDate);
            mMinYear = cal.get(Calendar.YEAR);
            mMinMonth = cal.get(Calendar.MONTH) + 1;
        } else {
            mMinYear = 1900;
            mMinMonth = 1;
        }
        if (dialogView != null) {
            final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.picker_month);
            final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.picker_year);
            yearPicker.setMinValue(mMinYear);
            monthPicker.setMinValue(mMinMonth);
        }
        clearDateIfInvalid();
    }

    public void setPickerState(Date minDate, Date maxDate, Date currentDate) {
        setCurrentDate(currentDate);
        setMinDate(minDate);
        setMaxDate(maxDate);
        if (dialogView != null && mYear > 0) {
            updateMonthPickerLimits(mYear);
        }
    }

    public void clearDate() {
        mDateValue = null;
        mMonth = -1;
        mYear = -1;
    }

    public void clearDateIfInvalid() {
        if (mMinDate != null && mDateValue != null) {
            if (compareDate(mDateValue, mMinDate) < 0) {
                clearDate();
            }
        }
        if (mMaxDate != null && mDateValue != null) {
            if (compareDate(mDateValue, mMaxDate) > 0) {
                clearDate();
            }
        }
    }

    private int compareDate(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Integer month1 = calendar.get(Calendar.MONTH);
        Integer year1 = calendar.get(Calendar.YEAR);

        calendar.setTime(date2);
        Integer month2 = calendar.get(Calendar.MONTH);
        Integer year2 = calendar.get(Calendar.YEAR);

        if (year1.equals(year2)) {
            if (month1.equals(month2)) {
                return 0;
            } else {
                return month1.compareTo(month2);
            }
        } else {
            return year1.compareTo(year2);
        }
    }

    private void updateMonthPickerLimits(int year) {
        final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.picker_month);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        if (year == mMaxYear && year == mMinYear) {
            monthPicker.setMinValue(mMinMonth);
            monthPicker.setMaxValue(mMaxMonth);
        } else if (year == mMaxYear) {
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(mMaxMonth);
        } else if (year == mMinYear) {
            monthPicker.setMinValue(mMinMonth);
            monthPicker.setMaxValue(12);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.picker_month);
                    final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.picker_year);

                    mYear = yearPicker.getValue();
                    mMonth = monthPicker.getValue();

                    dialogView.clearFocus();
                    mDateSetListener.onDateSet(dialogView, mYear, mMonth - 1, 1);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);

        mDateValue = cal.getTime();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH) + 1;
    }

    public interface OnDateSetListener {
        /**
         * @param view the picker associated with the dialog
         * @param year the selected year
         * @param month the selected month (0-11 for compatibility with
         *              {@link Calendar#MONTH})
         * @param dayOfMonth th selected day of the month (1-31, depending on
         *                   month)
         */
        void onDateSet(View view, int year, int month, int dayOfMonth);
    }
}
