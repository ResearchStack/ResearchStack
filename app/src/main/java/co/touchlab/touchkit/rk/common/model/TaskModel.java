package co.touchlab.touchkit.rk.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskModel
{
    @SerializedName("identifier")
    public String identifier;

    @SerializedName("type")
    public String type;

    @SerializedName("name")
    public String name;

    @SerializedName("elements")
    public List<StepModel> elements;

    public static class StepModel
    {
        @SerializedName("identifier")
        public String identifier;

        @SerializedName("prompt")
        public String prompt;

        @SerializedName("checkbox")
        public String checkbox;

        @SerializedName("uiHint")
        public String uiHint;


        @SerializedName("guid")
        public String guid;

        @SerializedName("type")
        public String type;

        @SerializedName("constraints")
        public ConstraintsModel constraints;
    }

    public static class ConstraintsModel
    {

        @SerializedName("dataType")
        public String dataType;

        @SerializedName("allowMultiple")
        public boolean allowMultiple;

        @SerializedName("allowOther")
        public boolean allowOther;

        @SerializedName("type")
        public String type;

        @SerializedName("enumeration")
        public List<EnumerationModel> enumeration;

        @SerializedName("step")
        public String step;

        @SerializedName("maxValue")
        public int maxValue;

        @SerializedName("minValue")
        public int minValue;

        @SerializedName("rules")
        public List<RuleModel> rules;
    }

    public static class EnumerationModel
    {
        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public int value;

        @SerializedName("label")
        public String label;
    }

    public static class RuleModel
    {
        @SerializedName("operator")
        public String operator;

        @SerializedName("skipTo")
        public String skipTo;

        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public int value;

    }
}
