package org.researchstack.backbone.storage.database.staged.records;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.researchstack.backbone.model.staged.StagedActivityState;
import org.researchstack.backbone.model.staged.StagedEvent;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mauriciosouto on 18/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class StagedEventRecordTest {

    @Test
    public void testStagedEventRecord() {
        OrderedTask task = new OrderedTask("TEST_TASK_ID", new ArrayList<Step>());

        StagedEvent event = new StagedEvent();
        event.setId(1);
        event.setActivityId("1234");
        event.setStatus(StagedActivityState.New);
        event.setEventStartDate(new Date());
        event.setEventEndDate(new Date());
        event.setLastStepId("4321");
        event.setTask(task);

        StagedEventRecord record = StagedEventRecord.toRecord(event);

        Assert.assertEquals(record.id, event.getId());
        Assert.assertEquals(record.stagedActivityId, event.getActivityId());
        Assert.assertEquals(record.status, event.getStatus());
        Assert.assertEquals(record.eventStartDate, event.getEventStartDate());
        Assert.assertEquals(record.eventEndDate, event.getEventEndDate());
        Assert.assertEquals(record.taskId, "TEST_TASK_ID");
        Assert.assertEquals(record.lastStepId, "4321");
        Assert.assertNotNull(record.task);

        event = StagedEventRecord.toStagedEvent(record, null);

        Assert.assertEquals(record.id, event.getId());
        Assert.assertEquals(record.stagedActivityId, event.getActivityId());
        Assert.assertEquals(record.status, event.getStatus());
        Assert.assertEquals(record.eventStartDate, event.getEventStartDate());
        Assert.assertEquals(record.eventEndDate, event.getEventEndDate());
        Assert.assertEquals(event.getTask().getIdentifier(), "TEST_TASK_ID");
        Assert.assertEquals(event.getLastStepId(), "4321");
    }

}
