package org.researchstack.backbone.model.staged;

import java.util.Date;

/**
 * Created by mauriciosouto on 7/9/17.
 *
 * Represent the schedule selected of an Staged Activity
 *
 * - startDate: start date for the staged schedule
 * - endDate: end date for the staged schedule. Can be null/nil.
 * - duration: the length of time a particular instance of content will remain available after it is created. Can be null/nil, meaning no expiration
 * - durationType: units for duration (days, weeks, months). Can be null/nil
 * - repeats: class of type MedRepetitionPattern. Can be null/nil if the activity doesn't repeat.
 *
 */

public class MedStagedSchedule {

    public static MedStagedSchedule schedule(Date startDate) {
        return scheduleInternal(startDate, null, 0, null, null);
    }

    public static MedStagedSchedule limitedSchedule(Date startDate, int duration, MedStagedDurationType durationType) {
        return scheduleInternal(startDate, null, duration, durationType, null);
    }

    public static MedStagedSchedule repeatingSchedule(Date startDate, Date endDate, MedRepetitionPattern pattern) {
        return scheduleInternal(startDate, endDate, 0, null, pattern);
    }

    public static MedStagedSchedule repeatingLimitedSchedule(Date startDate, Date endDate,
                                                             int duration, MedStagedDurationType durationType,
                                                             MedRepetitionPattern pattern) {
        return scheduleInternal(startDate, endDate, duration, durationType, pattern);
    }

    private static MedStagedSchedule scheduleInternal(Date startDate, Date endDate,
                                                      int duration, MedStagedDurationType durationType,
                                                      MedRepetitionPattern repeats) {
        MedStagedSchedule stagedSchedule = new MedStagedSchedule();
        stagedSchedule.startDate = startDate;
        stagedSchedule.endDate = endDate;
        stagedSchedule.duration = duration;
        stagedSchedule.durationType = durationType;
        stagedSchedule.repeats = repeats;
        return stagedSchedule;
    }

    private Date startDate;
    private Date endDate;
    private int duration;
    private MedStagedDurationType durationType;
    private MedRepetitionPattern repeats;

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getDuration() {
        return duration;
    }

    public MedStagedDurationType getDurationType() {
        return durationType;
    }

    public MedRepetitionPattern getRepeats() {
        return repeats;
    }
}
