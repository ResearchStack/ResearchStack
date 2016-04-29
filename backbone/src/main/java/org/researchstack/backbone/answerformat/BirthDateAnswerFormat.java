package org.researchstack.backbone.answerformat;
import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.body.BodyAnswer;

import java.util.Calendar;
import java.util.Date;

public class BirthDateAnswerFormat extends DateAnswerFormat
{
    private final int minAge;
    private final int maxAge;

    private static Date dateFromAge(int age)
    {
        Calendar calendar = Calendar.getInstance();
        if(age != 0)
        {
            calendar.add(Calendar.YEAR, - age);
            return calendar.getTime();
        }

        return null;
    }

    public BirthDateAnswerFormat(Date defaultDate, int minAge, int maxAge)
    {
        super(DateAnswerStyle.Date, defaultDate, dateFromAge(maxAge), dateFromAge(minAge));
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    @Override
    public BodyAnswer validateAnswer(Date resultDate)
    {
        Date minDate = getMinimumDate();
        Date maxDate = getMaximumDate();

        if(minDate != null && resultDate.getTime() > minDate.getTime())
        {
            return new BodyAnswer(false, R.string.rsb_birth_date_too_old, String.valueOf(maxAge));
        }

        if(maxDate != null && resultDate.getTime() > maxDate.getTime())
        {
            return new BodyAnswer(false, R.string.rsb_birth_date_too_young, String.valueOf(minAge));
        }

        return BodyAnswer.VALID;
    }
}
