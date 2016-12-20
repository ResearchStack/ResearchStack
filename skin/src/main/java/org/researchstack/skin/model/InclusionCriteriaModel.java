package org.researchstack.skin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InclusionCriteriaModel {

    @SerializedName("steps")
    public  List<Step> steps;


    public static class Step
    {
        @SerializedName("identifier")
        public String identifier;

        @SerializedName("type")
        public String type;

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
