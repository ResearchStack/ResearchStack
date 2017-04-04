package org.researchstack.backbone.model.taskitem;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.model.taskitem.factory.TaskItemFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TheMDP on 3/7/17.
 *
 * This class is the deserializer for TaskItem classes
 * It looks at the "taskType" field, attempts to map it to this library's pre-defined types
 * and if it does not find it, creates a custom task item
 * the class of the custom task item can easily be controlled by overriding this
 * adapter, and overriding the method getCustomClass
 *
 * To go even further and change the mapping of the custom tak item to a custom step,
 * you should override TaskItemFactory's method public Task createCustomTask method
 * which is the go to for converting a task item to a Task
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
            // Some JSON can be malformed and there is no taskType, only taskIdentifier
            // In that case let's try and still parse it by setting the task type as the task identifier
            if (typeJson != null) {
                customTypeString = typeJson.getAsString();
            } else { // use "taskIdentifier" string
                customTypeString = jsonObject.get(TaskItem.TASK_IDENTIFIER_GSON).getAsString();
            }
        }

        TaskItem item = null;

        switch (itemType) {
            case WALKING:
            case SHORT_WALK:
            case VOICE:
            case TAPPING:
            case MOOD_SURVEY:
            case TREMOR:
            case MEMORY:
                item = context.deserialize(json, ActiveTaskItem.class);
                break;
            case CUSTOM:
                item = context.deserialize(json, getCustomClass(customTypeString, json));
                item.setTaskType(itemType); // need to set CUSTOM type for surveyItem, since it is a special case
                item.setCustomTypeValue(customTypeString);
                break;
        }

        if (item == null) {
            item = context.deserialize(json, BaseTaskItem.class);
            item.setTaskType(itemType);
        }

        item.setRawJson(json.toString());

        return item;
    }

    /**
     * This can be overridden by subclasses to provide custom survey item deserialization
     * the default deserialization is always a CustomTaskItem.class
     * @param customType used to map to different types of survey items
     * @param json if customType is not enough, you can use the JsonElement to determine how to parse
     *             it's contents by peeking at it's variables
     * @return type of survey item to create from the custom class
     */
    public Class<? extends TaskItem> getCustomClass(String customType, JsonElement json) {
        return BaseTaskItem.class;
    }
}
