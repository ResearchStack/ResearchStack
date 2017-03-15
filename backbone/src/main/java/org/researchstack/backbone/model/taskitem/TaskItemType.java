package org.researchstack.backbone.model.taskitem;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 3/7/17.
 */

public enum  TaskItemType {

    // Custom Task
    CUSTOM(null),

    // Active Tasks...
    // Tapping Task
    @SerializedName("tapping")
    TAPPING        ("tapping"),

    // Memory Task
    @SerializedName("memory")
    MEMORY         ("memory"),

    // Voice Task
    @SerializedName("voice")
    VOICE          ("voice"),

    // Walking Task
    @SerializedName("walking")
    WALKING        ("walking"),

    // Short Walking Task
    @SerializedName("shortWalk")
    SHORT_WALK     ("shortWalk"),

    // Tremor Task
    @SerializedName("tremor")
    TREMOR         ("tremor"),

    // Mood Survey Task
    @SerializedName("moodSurvey")
    MOOD_SURVEY    ("moodSurvey");

    TaskItemType(String rawValue) {
        value = rawValue;
    }

    private String value;
    String getValue() {
        return value;
    }
}
