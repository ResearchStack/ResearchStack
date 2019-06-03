package org.researchstack.backbone.task.factory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.ProfileInfoOption;

/**
 * Created by David Evans, 2019.
 *
 * Values that identify the limb(s) to be used in an active task.
 *
 */
public class LimbTaskOptions {

    public static final String SERIALIZED_NAME_LIMB_LEFT    = "left";
    public static final String SERIALIZED_NAME_LIMB_RIGHT   = "right";
    public static final String SERIALIZED_NAME_LIMB_BOTH    = "both";

    static Gson gson;

    public enum Limb {
        // Task should only test the left limb
        @SerializedName(SERIALIZED_NAME_LIMB_LEFT)
        LEFT,
        // Task should only test the right limb
        @SerializedName(SERIALIZED_NAME_LIMB_RIGHT)
        RIGHT,
        // Task should test both left and right limbs
        @SerializedName(SERIALIZED_NAME_LIMB_BOTH)
        BOTH;
    }

    public static Limb toLimbOption(String limbSerializedName) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(limbSerializedName, Limb.class);
    }
}
