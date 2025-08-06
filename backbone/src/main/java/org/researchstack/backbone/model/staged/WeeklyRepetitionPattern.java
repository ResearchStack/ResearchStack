package org.researchstack.backbone.model.staged;

/**
 * Created by mauriciosouto on 7/9/17.
 *
 * pattern: array of 7 Boolean values (representing Sunday to Saturday), with days for repetition marked as true
 *
 */

public class WeeklyRepetitionPattern extends RepetitionPattern {

    private boolean[] pattern;

    public boolean[] getPattern() {
        return pattern;
    }

    public void setPattern(boolean[] pattern) {
        this.pattern = pattern;
    }
}
