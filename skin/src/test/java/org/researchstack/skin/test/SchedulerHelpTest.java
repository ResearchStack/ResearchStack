package org.researchstack.skin.test;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.researchstack.skin.schedule.ScheduleHelper;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Dario Salvi on 19/12/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class SchedulerHelpTest {

    @Test
    public void testSimple() {
        DateTime start = new DateTime(2016, 1, 1, 0, 0); //1st Jan
        String chron = "0 5 * * *"; //5 AM everyday
        DateTime next = new DateTime(ScheduleHelper.nextSchedule(chron, null, null, start.toDate(), null));

        assertEquals(1, next.getDayOfMonth());
        assertEquals(1, next.getMonthOfYear());
        assertEquals(2016, next.getYear());
    }

    @Test
    public void testSimpleDelay() {
        DateTime start = new DateTime(2016, 1, 1, 0, 0); //1st Jan
        String chron = "0 5 * * *"; //5 AM everyday
        String delay = "P2D"; //2 days
        DateTime next = new DateTime(ScheduleHelper.nextSchedule(chron, delay, null, start.toDate(), null));

        assertEquals(3, next.getDayOfMonth());
        assertEquals(1, next.getMonthOfYear());
        assertEquals(2016, next.getYear());
    }

    @Test
    public void testNocronDelay() {
        DateTime start = new DateTime(2016, 1, 1, 0, 0); //1st Jan
        String delay = "P2D"; //2 days
        DateTime next = new DateTime(ScheduleHelper.nextSchedule(null, delay, null, start.toDate(), null));

        assertEquals(3, next.getDayOfMonth());
        assertEquals(1, next.getMonthOfYear());
        assertEquals(2016, next.getYear());
    }

    @Test
    public void testExecutedSimple() {
        DateTime start = new DateTime(2016, 1, 1, 0, 0); //1st Jan
        DateTime executed = new DateTime(2016, 1, 3, 13, 32); //3rd Jan
        String chron = "0 5 * * *"; //5 AM everyday
        DateTime next = new DateTime(ScheduleHelper.nextSchedule(chron, null, null, start.toDate(), executed.toDate()));

        assertEquals(4, next.getDayOfMonth());
        assertEquals(1, next.getMonthOfYear());
        assertEquals(2016, next.getYear());
    }

    @Test
    public void testExecutedNocronInterval() {
        DateTime start = new DateTime(2016, 1, 1, 0, 0); //1st Jan
        DateTime executed = new DateTime(2016, 1, 3, 13, 32); //3rd Jan
        String interval = "P2D"; //2 days
        DateTime next = new DateTime(ScheduleHelper.nextSchedule(null, null, null, start.toDate(), executed.toDate()));

        assertEquals(3, next.getDayOfMonth());
        assertEquals(1, next.getMonthOfYear());
        assertEquals(2016, next.getYear());
    }

    @Test
    public void testExecutedNotSoSimple() {
        DateTime start = new DateTime(2016, 1, 1, 0, 0); //1st Jan
        DateTime executed = new DateTime(2016, 1, 3, 13, 45); //3rd Jan
        String chron = "0 5 3,5 * *"; //5 AM every 3rd and 5th
        DateTime next = new DateTime(ScheduleHelper.nextSchedule(chron, null, null, start.toDate(), executed.toDate()));

        assertEquals(5, next.getDayOfMonth());
        assertEquals(1, next.getMonthOfYear());
        assertEquals(2016, next.getYear());
    }

    @Test
    public void testExecutedInterval() {
        DateTime start = new DateTime(2016, 1, 1, 0, 0); //1st Jan
        DateTime executed = new DateTime(2016, 1, 3, 13, 45); //3rd Jan
        String chron = "0 5 * * *"; //5 AM everyday
        String interval = "P2D"; //2 days
        DateTime next = new DateTime(ScheduleHelper.nextSchedule(chron, null, interval, start.toDate(), executed.toDate()));

        assertEquals(6, next.getDayOfMonth());
        assertEquals(1, next.getMonthOfYear());
        assertEquals(2016, next.getYear());
    }

    @Test
    public void testExpired() {
        DateTime twodaysago = new DateTime().minusDays(2);
        String expires1day = "P1D";
        String expires3days = "P3D";

        assertEquals(true, ScheduleHelper.isExpired(twodaysago.toDate(), expires1day));
        assertEquals(false, ScheduleHelper.isExpired(twodaysago.toDate(), expires3days));
    }

}
