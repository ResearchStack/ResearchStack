package org.researchstack.backbone.schedule;

import java.util.Date;

@Deprecated
public class ScheduleHelper {
    private ScheduleHelper() {
    }

    public static Date nextSchedule(String cronString, Date lastExecution) {
        return new Date();
    }
}
