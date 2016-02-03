package co.touchlab.researchstack.skin.schedule;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.touchlab.researchstack.BuildConfig;

/**
 * Created by kgalligan on 11/27/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ScheduleHelperTest
{
    DateFormat dfSimple = new SimpleDateFormat("hh:mm:ss MM/dd/yyyy a");
    DateFormat df       = new SimpleDateFormat("hh:mm:ss EEE, MMM dd yyyy a");

    public static Date nextSchedule(String cronString, Date lastExecution)
    {
        DateTime now = new DateTime(lastExecution);
        CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
        Cron cron = cronParser.parse(cronString);

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        DateTime nextExecution = executionTime.nextExecution(now);

        return nextExecution.toDate();
    }

    @Test
    public void testBasicCron() throws ParseException
    {
        printDate("3:15:00 11/20/2015 PM");
        printDate("3:15:00 11/27/2015 PM");
        //        printDate("3:15:00 11/29/2015 PM");
        //        printDate("3:15:00 11/30/2015 PM");
        //        printDate("3:15:00 12/01/2015 PM");
        //        printDate("3:15:00 12/02/2015 PM");
        //        printDate("3:15:00 12/29/2015 PM");
        //        printDate("3:15:00 12/30/2015 PM");
        //        printDate("3:15:00 12/31/2015 PM");
    }

    private void printDate(String startDate) throws ParseException
    {
        Date now = dfSimple.parse(startDate);
        System.out.println("Starting: " + df.format(now));
        printNextDate(now, "0 6 * * 0");//Sunday
        printNextDate(now, "0 6 * * 1");
        printNextDate(now, "0 6 * * 2");
        printNextDate(now, "0 6 * * 3");
        printNextDate(now, "0 6 * * 4");
        printNextDate(now, "0 6 * * 5");
        printNextDate(now, "0 6 * * 6");
    }

    private void printNextDate(Date now, String cronString)
    {
        Date date = nextSchedule(cronString, now);
        System.out.println("Next time: " + df.format(date));
    }
}
