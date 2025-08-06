package org.researchstack.backbone.schedule;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import org.joda.time.DateTime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;


public class ScheduleHelper {
    private ScheduleHelper() {
    }

    public static Date nextSchedule(String cronString, Date lastExecution) {
        DateTime now = new DateTime(lastExecution);
        CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
        Cron cron = cronParser.parse(cronString);

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Instant instant = Instant.ofEpochMilli(now.getMillis());
            ZoneId zoneId = ZoneId.of(now.getZone().getID());
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);

            Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(zdt);
            if(nextExecution.isPresent()){
                return Date.from(nextExecution.get().toInstant());
            }
        }
        return lastExecution;
    }
}
