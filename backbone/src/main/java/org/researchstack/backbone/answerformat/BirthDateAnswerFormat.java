package org.researchstack.backbone.answerformat;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.body.BodyAnswer;

import java.util.Calendar;
import java.util.Date;

public class BirthDateAnswerFormat extends DateAnswerFormat {
    private final int minAge;
    private final int maxAge;

    public BirthDateAnswerFormat(Date defaultDate, int minAge, int maxAge) {
        super(DateAnswerStyle.Date, defaultDate, dateFromAge(maxAge), dateFromAge(minAge));
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    private static Date dateFromAge(int age) {
        Calendar calendar = Calendar.getInstance();
        if (age != 0) {
            calendar.add(Calendar.YEAR, -age);
            return calendar.getTime();
        }

        return null;
    }

    @Override
    public BodyAnswer validateAnswer(Date resultDate) {
        Date minDate = getMinimumDate();
        Date maxDate = getMaximumDate();

        if (minDate != null && isOnOrBefore(resultDate, minDate)) {
            return new BodyAnswer(false, R.string.rsb_birth_date_too_old, String.valueOf(maxAge));
        }

        if (maxDate != null && !isOnOrBefore(resultDate, maxDate)) {
            return new BodyAnswer(false, R.string.rsb_birth_date_too_young, String.valueOf(minAge));
        }

        return BodyAnswer.VALID;
    }

    private boolean isOnOrBefore(Date inputDate, Date cutoffDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputDate);
        int year = calendar.get(Calendar.YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(cutoffDate);
        int cutoffYear = calendar.get(Calendar.YEAR);
        int cutoffDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        return year < cutoffYear || (year == cutoffYear && dayOfYear <= cutoffDayOfYear);
    }
}
