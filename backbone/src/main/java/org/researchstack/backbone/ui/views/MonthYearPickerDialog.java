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
import java.util.TimeZone;

/**
 * Created by spiria on 22/3/18.
 */

public class MonthYearPickerDialog extends AlertDialog implements DialogInterface.OnClickListener {

    private Date mDateValue;
    private Date mMaxDate = null;
    private Date mMinDate = null;
    private View mDialogView;

    public MonthYearPickerDialog(Context context) {
        super(context);

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        mDialogView = inflater.inflate(R.layout.rsb_layout_month_year_picker, null);
        setView(mDialogView);

        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.rsb_ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.rsb_cancel), this);

        final NumberPicker monthPicker = (NumberPicker) mDialogView.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) mDialogView.findViewById(R.id.picker_year);

        Calendar cal = Calendar.getInstance();
        int maxYear = cal.get(Calendar.YEAR) + 100;
        if (mMaxDate != null) {
            cal.setTime(mMaxDate);
            maxYear = cal.get(Calendar.YEAR);
        }
        yearPicker.setMaxValue(maxYear);
        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(1);
        int minYear = 1900;
        if (mMinDate != null) {
            cal.setTime(mMinDate);
            minYear = cal.get(Calendar.YEAR);
        }
        yearPicker.setMinValue(minYear);

        Calendar calendar = Calendar.getInstance();
        if (mDateValue != null) {
            calendar.setTime(mDateValue);
        }
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
    }

    public void setCurrentDate(Date currentDate) {
        mDateValue = currentDate;
        if (mDialogView != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mDateValue);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            final NumberPicker monthPicker = (NumberPicker) mDialogView.findViewById(R.id.picker_month);
            final NumberPicker yearPicker = (NumberPicker) mDialogView.findViewById(R.id.picker_year);
            yearPicker.setValue(year);
            monthPicker.setValue(month);
        }
    }

    public void setMaxDate(Date maxDate) {
        this.mMaxDate = maxDate;
        if (mDialogView != null) {
            Calendar cal = Calendar.getInstance();
            int maxYear = cal.get(Calendar.YEAR) + 100;
            if (mMaxDate != null) {
                cal.setTime(mMaxDate);
                maxYear = cal.get(Calendar.YEAR);
            }
            final NumberPicker yearPicker = (NumberPicker) mDialogView.findViewById(R.id.picker_year);
            yearPicker.setMaxValue(maxYear);
        }
        clearDateIfInvalid();
    }

    public void setMinDate(Date minDate) {
        this.mMinDate = minDate;
        clearDateIfInvalid();
    }

    public void setPickerState(Date minDate, Date maxDate, Date currentDate) {
        setCurrentDate(currentDate);
        setMinDate(minDate);
        setMaxDate(maxDate);
    }

    public void clearDate() {
        mDateValue = null;
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

    public int compareDate(Date date1, Date date2) {
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }
}
