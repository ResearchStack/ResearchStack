package org.researchstack.skin.schedule;
import android.support.annotation.NonNull;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.researchstack.backbone.utils.TextUtils;

import java.util.Date;


public class ScheduleHelper
{
    private ScheduleHelper() {}

    /**
     *
     * @param cronString the cron string that describes the periodicity
     * @param delay delay in ISO 8601 since the scheduling was actually started the first time (after onboarding)
     * @param interval minimal amount of time between two executions, complements the chron information
     * @param usestart when the app was started first, should be after onboarding and never null
     * @param lastExecution last time the task was completed, can be null
     * @return
     */
    public static Date nextSchedule(String cronString, String delay, String interval, @NonNull Date usestart, Date lastExecution)
    {
        DateTime start = new DateTime(usestart);
        if(lastExecution != null) {
            start = new DateTime(lastExecution);
            if(!TextUtils.isEmpty(interval)) {
                Period intervalp = Period.parse(interval);
                start = start.plus(intervalp);
            }
        } else if(!TextUtils.isEmpty(delay)) {
            Period delayp = Period.parse(delay);
            start = start.plus(delayp);
        }
        if(!TextUtils.isEmpty(cronString)){
            CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
            Cron cron = cronParser.parse(cronString);
            ExecutionTime executionTime = ExecutionTime.forCron(cron);

            DateTime nextExecution = executionTime.nextExecution(start);

            return nextExecution.toDate();
        }
        else return start.toDate();
    }
}
