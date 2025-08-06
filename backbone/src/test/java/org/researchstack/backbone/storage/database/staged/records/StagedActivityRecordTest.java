package org.researchstack.backbone.storage.database.staged.records;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.researchstack.backbone.model.staged.StagedActivity;
import org.researchstack.backbone.model.staged.StagedActivityState;
import org.researchstack.backbone.model.staged.StagedActivityType;

/**
 * Created by mauriciosouto on 18/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class StagedActivityRecordTest {

    @Test
    public void testStagedActivityRecord() {
        StagedActivity activity = new StagedActivity();
        activity.setId("12345");
        activity.setType(StagedActivityType.ACTIVE_TASK);
        activity.setStatus(StagedActivityState.New);
        activity.setTitle("Title");
        activity.setText("Text");
        activity.setInstructions("Instructions");
        activity.setTintColor(1);
        activity.setResultResettable(true);

        StagedActivityRecord record = StagedActivityRecord.toRecord(activity);
        Assert.assertEquals(record.stagedActivityId, activity.getId());
        Assert.assertEquals(record.type, activity.getType());
        Assert.assertEquals(record.status, activity.getStatus());
        Assert.assertEquals(record.title, activity.getTitle());
        Assert.assertEquals(record.text, activity.getText());
        Assert.assertEquals(record.instructions, activity.getInstructions());
        Assert.assertEquals(record.tintColor, activity.getTintColor());
        Assert.assertEquals(record.resultResettable, activity.isResultResettable());

        activity = StagedActivityRecord.toStagedActivity(record);
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
