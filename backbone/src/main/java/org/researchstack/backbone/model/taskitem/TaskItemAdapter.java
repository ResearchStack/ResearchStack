package org.researchstack.backbone.model.taskitem;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TheMDP on 3/7/17.
 */

public class TaskItemAdapter implements JsonDeserializer<TaskItem> {
    @Override
    public TaskItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject =  json.getAsJsonObject();

        JsonElement typeJson = jsonObject.get(TaskItem.TASK_TYPE_GSON);
        TaskItemType itemType = context.deserialize(typeJson, TaskItemType.class);

        // This was a custom activity task item type
        String customTypeString = null;
        if (itemType == null) {
            itemType = TaskItemType.CUSTOM;
            customTypeString = typeJson.getAsString();
        }

        switch (itemType) {
            case WALKING:
            case SHORT_WALK:
            case VOICE:
            case TAPPING:
            case MOOD_SURVEY:
            case TREMOR:
            case MEMORY:
                ActiveTaskItem activeTaskItem = context.deserialize(json, ActiveTaskItem.class);

                // Custom de-serialization of open-ended taskOptions field
                JsonElement taskOptionsJson = jsonObject.get(ActiveTaskItem.GSON_TASK_OPTIONS_NAME);
                if (taskOptionsJson != null) {
                    
                }

                return activeTaskItem;
            case CUSTOM:
                CustomTaskItem item = context.deserialize(json, getCustomClass(customTypeString));
                item.setTaskType(itemType); // need to set CUSTOM type for surveyItem, since it is a special case
                item.customSurveyItemIdentifier = customTypeString;
                item.setRawJson(json.getAsString());
                return item;
        }

        TaskItem item = context.deserialize(json, BaseTaskItem.class);
        item.setTaskType(itemType);
        return item;
    }

    /**
     * This can be overridden by subclasses to provide custom survey item deserialization
     * the default deserialization is always a CustomTaskItem.class
     * @param customType used to map to different types of survey items
     * @return type of survey item to create from the custom class
     */
    public Class<? extends CustomTaskItem> getCustomClass(String customType) {
        return CustomTaskItem.class;
    }
}
