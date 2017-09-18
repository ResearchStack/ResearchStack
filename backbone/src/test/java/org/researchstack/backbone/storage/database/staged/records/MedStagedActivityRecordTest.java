package org.researchstack.backbone.storage.database.staged.records;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.researchstack.backbone.model.staged.MedStagedActivity;
import org.researchstack.backbone.model.staged.MedStagedActivityState;
import org.researchstack.backbone.model.staged.MedStagedActivityType;

/**
 * Created by mauriciosouto on 18/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MedStagedActivityRecordTest {

    @Test
    public void testMedStagedActivityRecord() {
        MedStagedActivity activity = new MedStagedActivity();
        activity.setId("12345");
        activity.setType(MedStagedActivityType.ACTIVE_TASK);
        activity.setStatus(MedStagedActivityState.INITIAL);
        activity.setTitle("Title");
        activity.setText("Text");
        activity.setInstructions("Instructions");
        activity.setTintColor(1);
        activity.setResultResettable(true);

        MedStagedActivityRecord record = MedStagedActivityRecord.toRecord(activity);
        Assert.assertEquals(record.stagedActivityId, activity.getId());
        Assert.assertEquals(record.type, activity.getType());
        Assert.assertEquals(record.status, activity.getStatus());
        Assert.assertEquals(record.title, activity.getTitle());
        Assert.assertEquals(record.text, activity.getText());
        Assert.assertEquals(record.instructions, activity.getInstructions());
        Assert.assertEquals(record.tintColor, activity.getTintColor());
        Assert.assertEquals(record.resultResettable, activity.isResultResettable());

        activity = MedStagedActivityRecord.toMedStagedActivity(record);
        Assert.assertEquals(record.stagedActivityId, activity.getId());
        Assert.assertEquals(record.type, activity.getType());
        Assert.assertEquals(record.status, activity.getStatus());
        Assert.assertEquals(record.title, activity.getTitle());
        Assert.assertEquals(record.text, activity.getText());
        Assert.assertEquals(record.instructions, activity.getInstructions());
        Assert.assertEquals(record.tintColor, activity.getTintColor());
        Assert.assertEquals(record.resultResettable, activity.isResultResettable());
    }

}
