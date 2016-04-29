package org.researchstack.backbone.answerformat;
import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.FormatHelper;

import java.util.Date;

/**
 * The DateAnswerFormat class represents the answer format for questions that require users to enter
 * a date, or a date and time.
 */
public class DateAnswerFormat extends AnswerFormat
{

    private DateAnswerStyle style;

    private Date defaultDate;

    private Date minimumDate;

    private Date maximumDate;

    public DateAnswerFormat(DateAnswerStyle style)
    {
        this.style = style;
    }

    public DateAnswerFormat(DateAnswerStyle style, Date defaultDate, Date minimumDate, Date maximumDate)
    {
        this.style = style;
        this.defaultDate = defaultDate;
        this.minimumDate = minimumDate;
        this.maximumDate = maximumDate;
    }

    /**
     * Returns the style of date entry.
     *
     * @return the style of the date entry, Date or Date and Time
     */
    public DateAnswerStyle getStyle()
    {
        return style;
    }

    /**
     * Returns the date to use as the default.
     * <p>
     * The date is displayed in the user's time zone. When the value of this property is
     * <code>null</code>, the current time is used as the default.
     *
     * @return the default date for the date picker presented to the user, or null
     */
    public Date getDefaultDate()
    {
        return defaultDate;
    }

    /**
     * Returns the minimum allowed date.
     * <p>
     * When the value of this property is <code>null</code>, there is no minimum.
     *
     * @return returns the minimum allowed date, or null
     */
    public Date getMinimumDate()
    {
        return minimumDate;
    }

    /**
     * The maximum allowed date.
     * <p>
     * When the value of this property is <code>null</code>, there is no maximum.
     *
     * @return returns the maximum allowed date, or null
     */
    public Date getMaximumDate()
    {
        return maximumDate;
    }

    @Override
    public QuestionType getQuestionType()
    {
        int def = Type.DateAndTime.ordinal();
        int style = getStyle().ordinal();
        return Type.values()[def + style];
    }

    public BodyAnswer validateAnswer(Date resultDate)
    {
        Date minDate = getMinimumDate();
        Date maxDate = getMaximumDate();

        if(minDate != null && resultDate.getTime() < minDate.getTime())
        {
            return new BodyAnswer(false,
                    R.string.rsb_invalid_answer_date_under,
                    FormatHelper.SIMPLE_FORMAT_DATE.format(minDate));
        }

        if(maxDate != null && resultDate.getTime() > maxDate.getTime())
        {
            return new BodyAnswer(false,
                    R.string.rsb_invalid_answer_date_over,
                    FormatHelper.SIMPLE_FORMAT_DATE.format(maxDate));
        }

        return BodyAnswer.VALID;
    }

}
