package org.researchstack.backbone.model.taskitem;

/**
 * Created by TheMDP on 3/7/17.
 *
 * Only needed because TaskItemAdapter needs a base concrete implementation
 * to avoid infinite loops of de-serializing TaskItem
 */

public class BaseTaskItem extends TaskItem {
    public BaseTaskItem() {
        super();
    }
}
