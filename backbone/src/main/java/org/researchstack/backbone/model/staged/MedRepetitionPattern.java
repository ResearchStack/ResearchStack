package org.researchstack.backbone.model.staged;

import java.io.Serializable;

/**
 * Created by mauriciosouto on 7/9/17.
 *
 * Abstract class to support repetition on Staged Activities
 *
 * There are four concrete subclasses of this class:
 * - MedDailyRepetitionPattern
 * - MedWeeklyRepetitionPattern
 * - MedMonthlyDateRepetitionPatttern
 * - MedMonthlyDayRepetitionPattern.
 * Each has the attribute unitsToSkip, which indicates the number of units (days, weeks, months) to skip before repeating
 *
 */

public abstract class MedRepetitionPattern implements Serializable {

    private int unitsToSkip;

    public int getUnitsToSkip() {
        return unitsToSkip;
    }

    public void setUnitsToSkip(int unitsToSkip) {
        this.unitsToSkip = unitsToSkip;
    }
}
