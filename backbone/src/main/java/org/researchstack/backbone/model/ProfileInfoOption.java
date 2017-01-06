package org.researchstack.backbone.model;

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

    String identifier;
    public String getIdentifier() { return identifier; }

    /** Only useful for when Enum == PASSWORD */
    boolean addConfirmPassword = false;
    public void setAddConfirmPassword(boolean addConfirmPassword) {
        this.addConfirmPassword = addConfirmPassword;
    }
    public boolean getAddConfirmPassword() {
        return addConfirmPassword;
    }

    ProfileInfoOption(String identifier) {
        this.identifier = identifier;
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
        List<ProfileInfoOption> options = new ArrayList<>();
        if (EMAIL.getIdentifier().equals(identifier)) {
            return EMAIL;
        } else if (PASSWORD.getIdentifier().equals(identifier)) {
            return PASSWORD;
        } else if (EXTERNAL_ID.getIdentifier().equals(identifier)) {
            return EXTERNAL_ID;
        } else if (NAME.getIdentifier().equals(identifier)) {
            return NAME;
        } else if (BIRTHDATE.getIdentifier().equals(identifier)) {
            return BIRTHDATE;
        } else if (GENDER.getIdentifier().equals(identifier)) {
            return GENDER;
        } else if (BLOOD_TYPE.getIdentifier().equals(identifier)) {
            return BLOOD_TYPE;
        } else if (FITZPATRICK_SKIN_TYPE.getIdentifier().equals(identifier)) {
            return FITZPATRICK_SKIN_TYPE;
        } else if (WHEEL_CHAIR_USE.getIdentifier().equals(identifier)) {
            return WHEEL_CHAIR_USE;
        } else if (HEIGHT.getIdentifier().equals(identifier)) {
            return HEIGHT;
        } else if (WEIGHT.getIdentifier().equals(identifier)) {
            return WEIGHT;
        } else if (WAKE_TIME.getIdentifier().equals(identifier)) {
            return WAKE_TIME;
        } else if (SLEEP_TIME.getIdentifier().equals(identifier)) {
            return SLEEP_TIME;
        }
        return null;
    }
}
