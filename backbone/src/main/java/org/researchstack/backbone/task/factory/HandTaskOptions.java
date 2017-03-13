package org.researchstack.backbone.task.factory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.ProfileInfoOption;

/**
 * Created by TheMDP on 2/24/17.
 *
 * Values that identify the hand(s) to be used in an active task.
 *
 * By default, the participant will be asked to use their most affected hand.
 */
public class HandTaskOptions {

    public static final String SERIALIZED_NAME_HAND_LEFT    = "left";
    public static final String SERIALIZED_NAME_HAND_RIGHT   = "right";
    public static final String SERIALIZED_NAME_HAND_BOTH    = "both";

    static Gson gson;

    public enum Hand {
        // Task should only test the left hand
        @SerializedName(SERIALIZED_NAME_HAND_LEFT)
        LEFT,
        // Task should only test the right hand
        @SerializedName(SERIALIZED_NAME_HAND_RIGHT)
        RIGHT,
        // Task should test both left and right hands
        @SerializedName(SERIALIZED_NAME_HAND_BOTH)
        BOTH;
    }

    public static Hand toHandOption(String handSerializedName) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(handSerializedName, Hand.class);
    }
}
