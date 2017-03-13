package org.researchstack.backbone.model.taskitem;

/**
 * Created by TheMDP on 3/7/17.
 *
 * A CustomTaskItem can be used to serialize your own TaskItem with a custom identifier and type,
 * See TaskItemFactory and TaskItemAdapter on how to inject your own task when a CustomTaskItem
 * is found during deserialization
 */

public class CustomTaskItem extends TaskItem {

    protected String customSurveyItemIdentifier;

    protected String rawJson;

    /* Default constructor needed for serialization/deserialization of object */
    CustomTaskItem() {
        super();
    }

    @Override
    public String getTaskTypeIdentifier() {
        return customSurveyItemIdentifier;
    }

    /**
     * @return raw JSON from the TaskItem, can be used to extract more info when creating the task
     */
    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }
}
