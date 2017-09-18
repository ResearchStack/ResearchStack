package org.researchstack.backbone.storage.database.staged.records;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.researchstack.backbone.model.staged.MedStagedActivity;
import org.researchstack.backbone.model.staged.MedStagedActivityState;
import org.researchstack.backbone.model.staged.MedStagedActivityType;
import org.researchstack.backbone.model.staged.MedStagedEvent;

import java.util.Date;

/**
 * Created by mauriciosouto on 18/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MedStagedEventRecordTest {

    @Test
    public void testMedStagedEventRecord() {
        MedStagedEvent event = new MedStagedEvent();
        event.setActivity("1234");
        event.setStatus(MedStagedActivityState.INITIAL);
        event.setEventStartDate(new Date());
        event.setEventEndDate(new Date());

        MedStagedEventRecord record = MedStagedEventRecord.toRecord(event);

        Assert.assertEquals(record.stagedActivityId, event.getActivity());
        Assert.assertEquals(record.status, event.getStatus());
        Assert.assertEquals(record.eventStartDate, event.getEventStartDate());
        Assert.assertEquals(record.eventEndDate, event.getEventEndDate());

        event = MedStagedEventRecord.toMedStagedEvent(record);

        Assert.assertEquals(record.stagedActivityId, event.getActivity());
        Assert.assertEquals(record.status, event.getStatus());
        Assert.assertEquals(record.eventStartDate, event.getEventStartDate());
        Assert.assertEquals(record.eventEndDate, event.getEventEndDate());
    }

}
