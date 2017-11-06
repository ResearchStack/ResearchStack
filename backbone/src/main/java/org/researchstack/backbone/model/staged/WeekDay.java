package org.researchstack.backbone.model.staged;

import java.io.Serializable;

/**
 * Created by mauriciosouto on 7/9/17.
 *
 * Simple representation of a tuples week-day
 *
 * Week represents the week of the month, must be between 1 and 5 where 1 is the first week, and 5 is the last week.
 * In months with only four weeks, the value 4 and 5 refer to the same week
 *
 * Day represents the day of the week and must be between 0 and 6 from Sunday to Saturday.
 *
 */

public class WeekDay implements Serializable {

    public int week;
    public int day;
}
