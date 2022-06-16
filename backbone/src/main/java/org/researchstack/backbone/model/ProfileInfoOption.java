package org.researchstack.backbone.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 *
 * Used by several differnt Step types to designate re-usable QuestionStep types
 * that collect user profile info
 */

public enum  ProfileInfoOption {
    @SerializedName("email")
    EMAIL("email"),
    @SerializedName("password")
    PASSWORD("password"),
    @SerializedName("externalID")
    EXTERNAL_ID("externalID"),
    @SerializedName("name")
    NAME("name"),
    @SerializedName("birthdate")
    BIRTHDATE("birthdate"),
    @SerializedName("gender")
    GENDER("gender"),
    @SerializedName("bloodType")
    BLOOD_TYPE("bloodType"),
    @SerializedName("fitzpatrickSkinType")
    FITZPATRICK_SKIN_TYPE("fitzpatrickSkinType"),
    @SerializedName("wheelchairUse")
    WHEEL_CHAIR_USE("wheelchairUse"),
    @SerializedName("height")
    HEIGHT("height"),
    @SerializedName("weight")
    WEIGHT("weight"),
    @SerializedName("wakeTime")
    WAKE_TIME("wakeTime"),
    @SerializedName("sleepTime")
    SLEEP_TIME("sleepTime");

    static Gson gson;

    private final String identifier;

    ProfileInfoOption(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static List<ProfileInfoOption> toProfileInfoOptions(List<String> identifiers) {
        List<ProfileInfoOption> options = new ArrayList<>();
        for(String identifier : identifiers) {
            ProfileInfoOption option = toProfileInfoOption(identifier);
            if (option != null) {
                options.add(option);
            }
        }
        return options;
    }

    public static ProfileInfoOption toProfileInfoOption(String identifier) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(identifier, ProfileInfoOption.class);
    }
}
