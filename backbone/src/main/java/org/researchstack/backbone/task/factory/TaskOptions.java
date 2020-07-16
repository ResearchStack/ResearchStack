package org.researchstack.backbone.task.factory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.ProfileInfoOption;

/**
 * Created by David Evans, 2019.
 *
 * Values that are used within the instructions of an active task.
 *
 */
public class TaskOptions {

    public static final String SERIALIZED_NAME_SIDE_LEFT    = "left";
    public static final String SERIALIZED_NAME_SIDE_RIGHT   = "right";
    public static final String SERIALIZED_NAME_SIDE_BOTH    = "both";

    static Gson gson;

    public enum Side {
        // Task should test the left side
        @SerializedName(SERIALIZED_NAME_SIDE_LEFT)
        LEFT,
        // Task should test the right side
        @SerializedName(SERIALIZED_NAME_SIDE_RIGHT)
        RIGHT,
        // Task should test both sides
        @SerializedName(SERIALIZED_NAME_SIDE_BOTH)
        BOTH;
    }

    public static Side toTaskOption(String SideSerializedName) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(SideSerializedName, Side.class);
    }
}
