package org.researchstack.skin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Deprecated // No longer needed with new OnboardingManager
public class InclusionCriteriaModel {

    public static final String INELIGIBLE_INSTRUCTION_IDENTIFIER = "ineligibleInstruction";
    public static final String ELIGIBLE_INSTRUCTION_IDENTIFIER = "eligibleInstruction";
    @SerializedName("steps")
    public  List<Step> steps;


    public static class Step
    {
        @SerializedName("identifier")
        public String identifier;

        @SerializedName("type")
        public StepType type;

        @SerializedName("text")
        public String text;

        @SerializedName("detailText")
        public String detailText;

        @SerializedName("image")
        public String image;

        @SerializedName("nextIdentifier")
        public String nextIdentifier;

        @SerializedName("skipIdentifier")
        public String skipIdentifier;

        @SerializedName("skipIfPassed")
        public boolean skipIfPassed;

        @SerializedName("items")
        public List<Item> items;

    }

    public enum StepType {

        @SerializedName("instruction")
        INSTRUCTION("instruction"),
        @SerializedName("compound")
        COMPOUND("compound"),
        @SerializedName("toggle")
        TOGGLE("toggle"),
        @SerializedName("share")
        SHARE("share");

        StepType(String type) {
            type = type;
        }

        String type;
        public String getType() {
            return type;
        }
    }

    public static class Item
    {
        @SerializedName("identifier")
        public String identifier;

        @SerializedName("type")
        public String type;

        @SerializedName("text")
        public String text;

        @SerializedName("expectedAnswer")
        public boolean expectedAnswer;
    }
}
