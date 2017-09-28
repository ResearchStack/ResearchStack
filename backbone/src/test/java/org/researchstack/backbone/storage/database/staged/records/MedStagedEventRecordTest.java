package org.researchstack.backbone.storage.database.staged.records;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.researchstack.backbone.model.staged.MedStagedActivityState;
import org.researchstack.backbone.model.staged.MedStagedEvent;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mauriciosouto on 18/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MedStagedEventRecordTest {

    @Test
    public void testMedStagedEventRecord() {
        OrderedTask task = new OrderedTask("TEST_TASK_ID", new ArrayList<Step>());

        MedStagedEvent event = new MedStagedEvent();
        event.setActivityId("1234");
        event.setStatus(MedStagedActivityState.INITIAL);
        event.setEventStartDate(new Date());
        event.setEventEndDate(new Date());
        event.setTask(task);

        MedStagedEventRecord record = MedStagedEventRecord.toRecord(event);

        Assert.assertEquals(record.stagedActivityId, event.getActivityId());
        Assert.assertEquals(record.status, event.getStatus());
        Assert.assertEquals(record.eventStartDate, event.getEventStartDate());
        Assert.assertEquals(record.eventEndDate, event.getEventEndDate());
        Assert.assertEquals(record.taskId, "TEST_TASK_ID");
        Assert.assertNotNull(record.task);

        event = MedStagedEventRecord.toMedStagedEvent(record, null);

        Assert.assertEquals(record.stagedActivityId, event.getActivityId());
        Assert.assertEquals(record.status, event.getStatus());
        Assert.assertEquals(record.eventStartDate, event.getEventStartDate());
        Assert.assertEquals(record.eventEndDate, event.getEventEndDate());
        Assert.assertEquals(event.getTask().getIdentifier(), "TEST_TASK_ID");
    }

}
