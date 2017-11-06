package org.researchstack.backbone.model.staged;

import java.util.List;

/**
 * Created by mauriciosouto on 7/9/17.
 *
 * pattern: array of integers in the range of 1, 2,..., 28. Represents the date(s) in each month when repetition occurs.
 * So, if something repeats on the 7th and 18th of the month, the array will be [7, 18].
 *
 */

public class MonthlyDateRepetitionPattern extends RepetitionPattern {

    private List<Integer> pattern;

    public List<Integer> getPattern() {
        return pattern;
    }

    public void setPattern(List<Integer> pattern) {
        this.pattern = pattern;
    }
}
