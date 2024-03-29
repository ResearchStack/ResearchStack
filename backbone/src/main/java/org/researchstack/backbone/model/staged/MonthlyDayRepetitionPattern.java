package org.researchstack.backbone.model.staged;

import java.util.List;

/**
 * Created by mauriciosouto on 7/9/17.
 *
 * pattern: array of tuples of type (i, j).
 * The i value represents the week of the month (must be between 1 and 5, where 1 is the first week, and 5 is the last week.
 * In months with only four weeks, the value 4 and 5 refer to the same week).
 * The j value represents the day of the week (0 to 6, representing Sunday to Saturday).
 * If the user wanted to do something on the first Thursday of the month and the third Wednesday of the month, the array would be [(1, 5),(3,4)]
 *
 */

public class MonthlyDayRepetitionPattern extends RepetitionPattern {

    private List<WeekDay> pattern;

    public List<WeekDay> getPattern() {
        return pattern;
    }

    public void setPattern(List<WeekDay> pattern) {
        this.pattern = pattern;
    }
}
