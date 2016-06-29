package org.researchstack.skin.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TaskModel
{
    @SerializedName("identifier")
    public String identifier;

    public String guid;

    public String createdOn;

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

        @SerializedName("promptDetail")
        public String promptDetail;

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

        @SerializedName("optional")
        public boolean optional = true;
    }

    public static class ConstraintsModel implements Serializable
    {

        @SerializedName("dataType")
        public String dataType;

        // Determines whether more than one answer choice is accepted
        @SerializedName("allowMultiple")
        public boolean allowMultiple;

        @SerializedName("allowOther")
        public boolean allowOther;

        @SerializedName("type")
        public String type;

        // Each element is option within step
        @SerializedName("enumeration")
        public List<EnumerationModel> enumeration;

        @SerializedName("step")
        public String step;

        // Maximum accepted value for integer constraints
        // Maximum value available for slider constraints
        @SerializedName("maxValue")
        public int maxValue;

        // Minimum accepted value for integer constraints
        // Minimum value available for slider constraints
        @SerializedName("minValue")
        public int minValue;

        // Determines whether or not slider displays selected value
        @SerializedName("showValue")
        public boolean showValue;

        // Detemines appearance of slider from three choices
        // gradient, solid, or ticked
        @SerializedName("sliderView")
        public String sliderView;

        // Determines color of visual (for now, slider)
        // multiple colors separated by spaces used for gradient
        @SerializedName("color")
        public String color;

        // Determines text shown at minimum side of slider
        @SerializedName("minText")
        public String minText;

        // Determines text shown at maximum side of slider
        @SerializedName("maxText")
        public String maxText;

        // Determines image shown at minimum side of slider
        @SerializedName("minImage")
        public String minImage;

        // Determines image shown at maximum side of slider
        @SerializedName("maxImage")
        public String maxImage;

        // Determines if multiple lines of text are accepted for text constraints
        @SerializedName("multipleLines")
        public boolean multipleLines;

        // Determines rules for navigation of task
        @SerializedName("rules")
        public List<RuleModel> rules;

        @SerializedName("validation")
        public Validation validation;
    }

    public static class EnumerationModel implements Serializable
    {
        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public Object value;

        @SerializedName("label")
        public String label;
    }

    public static class RuleModel implements Serializable
    {
        @SerializedName("operator")
        public String operator;

        @SerializedName("skipTo")
        public String skipTo;

        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public Object value;

    }

    public static class Validation implements Serializable
    {
        @SerializedName("answer")
        public String answer;
    }

}
