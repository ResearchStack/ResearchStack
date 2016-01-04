package co.touchlab.researchstack.core.answerformat;
import java.util.Calendar;
import java.util.Date;

public class DateAnswerFormat extends AnswerFormat
{

    /**
     * The style of date entry.
     */
    private DateAnswerStyle style;

    /**
     * The date to use as the default.
     * <p>
     * The date is displayed in the user's time zone.
     * When the value of this property is `nil`, the current time is used as the default.
     */
    private Date defaultDate;

    /**
     * The minimum allowed date.
     * <p>
     * When the value of this property is `nil`, there is no minimum.
     */
    private Date minimumDate;

    /**
     * The maximum allowed date.
     * <p>
     * When the value of this property is `nil`, there is no maximum.
     */
    private Date maximumDate;

    /**
     * The calendar to use in the picker.
     * <p>
     * When the value of this property is `nil`, the picker uses the default calendar for the current locale.
     */
    private Calendar calendar;

    public DateAnswerFormat(DateAnswerStyle style)
    {
        this.style = style;
    }

    public DateAnswerFormat(DateAnswerStyle style, Date defaultDate, Date minimumDate, Date maximumDate, Calendar calendar)
    {
        this.style = style;
        this.defaultDate = defaultDate;
        this.minimumDate = minimumDate;
        this.maximumDate = maximumDate;
        this.calendar = calendar;
    }

    public DateAnswerStyle getStyle()
    {
        return style;
    }

    public Date getDefaultDate()
    {
        return defaultDate;
    }

    public Date getMinimumDate()
    {
        return minimumDate;
    }

    public Date getMaximumDate()
    {
        return maximumDate;
    }

    public Calendar getCalendar()
    {
        return calendar != null ? calendar : Calendar.getInstance();
    }

    @Override
    public QuestionType getQuestionType()
    {
        int def = QuestionType.DateAndTime.ordinal();
        int style = getStyle().ordinal();
        return QuestionType.values()[def + style];
    }

    //    - (BOOL)isEqual:(id)object {
    //      BOOL isParentSame = [super isEqual:object];
    //
    //      __typeof(self) castObject = object;
    //      return (isParentSame &&
    //            ORKEqualObjects(self.defaultDate, castObject.defaultDate) &&
    //            ORKEqualObjects(self.minimumDate, castObject.minimumDate) &&
    //            ORKEqualObjects(self.maximumDate, castObject.maximumDate) &&
    //            ORKEqualObjects(self.calendar, castObject.calendar) &&
    //            (_style == castObject.style));
    //    }

    //    - (NSUInteger)hash {
    //        // Don't bother including everything - style is the main item.
    //        return [super hash] & [self.defaultDate hash] ^ _style;
    //    }

    //    - (NSDateFormatter *)resultDateFormatter {
    //        NSDateFormatter *dfm = nil;
    //        switch (self.questionType) {
    //            case ORKQuestionTypeDate: {
    //                dfm = ORKResultDateFormatter();
    //                break;
    //            }
    //            case ORKQuestionTypeTimeOfDay: {
    //                dfm = ORKResultTimeFormatter();
    //                break;
    //            }
    //            case ORKQuestionTypeDateAndTime: {
    //                dfm = ORKResultDateTimeFormatter();
    //                break;
    //            }
    //            default:
    //                break;
    //        }
    //
    //        dfm = [dfm copy];
    //        dfm.calendar = [NSCalendar calendarWithIdentifier:NSCalendarIdentifierGregorian];
    //        return dfm;
    //    }

    //    - (NSString *)stringFromDate:(NSDate *)date {
    //        NSDateFormatter *dfm = [self resultDateFormatter];
    //        return [dfm stringFromDate:date];
    //    }

    //    - (NSDate *)dateFromString:(NSString *)string {
    //        NSDateFormatter *dfm = [self resultDateFormatter];
    //        return [dfm dateFromString:string];
    //    }

    //    - (ORKQuestionType) questionType {
    //        return ORKQuestionTypeDateAndTime + _style;
    //    }

    //    + (BOOL)supportsSecureCoding {
    //        return YES;
    //    }


}
