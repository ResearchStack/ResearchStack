package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TaskModel {
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

    public static class StepModel {
        @SerializedName("identifier")
        public String identifier;

        @SerializedName("title")
        public String title;

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

        @SerializedName("beforeRules")
        public List<RuleModel> beforeRules;

        @SerializedName("afterRules")
        public List<RuleModel> afterRules;
    }

    public static class ConstraintsModel implements Serializable {

        @SerializedName("dataType")
        public String dataType;

        @SerializedName("durationUnit")
        public String durationUnit;

        @SerializedName("allowMultiple")
        public boolean allowMultiple;

        @SerializedName("allowOther")
        public boolean allowOther;

        @SerializedName("type")
        public String type;

        @SerializedName("enumeration")
        public List<EnumerationModel> enumeration;

        @SerializedName("step")
        public int step;

        /**
         * @property minLength specified for TextFields character limits
         */
        @SerializedName("minLength")
        public int minLength;

        /**
         * @property maxLength specified for TextFields character limits
         */
        @SerializedName("maxLength")
        public int maxLength;

        /**
         * @property patternPlaceholder for TextFields or IntegerFields to display as the hint
         */
        @SerializedName("patternPlaceholder")
        public String patternPlaceholder;

        @SerializedName("maxValue")
        public int maxValue;

        @SerializedName("minValue")
        public int minValue;

        @SerializedName("multipleLines")
        public boolean multipleLines;

        @SerializedName("rules")
        public List<RuleModel> rules;

        @SerializedName("validation")
        public Validation validation;
    }

    public static class EnumerationModel implements Serializable {
        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public Object value;

        @SerializedName("label")
        public String label;
    }

    public static class RuleModel implements Serializable {
        @SerializedName("operator")
        public String operator;

        @SerializedName("skipTo")
        public String skipTo;

        /**
         * @property assignDataGroup data group to assign if operator condition is met
         */
        @SerializedName("assignDataGroup")
        public String assignDataGroup;

        /**
         * @property dataGroups should be analyzed in conjunction with the operator value "all"
         */
        @SerializedName("dataGroups")
        public List<String> dataGroups;

        /**
         * @property endSurvey specifies if we should end the survey if the operator condition is met
         */
        @SerializedName("endSurvey")
        public Boolean endSurvey;

        /**
         * @property displayUnless specifies if we should display the step unless the operator condition is met
         */
        @SerializedName("displayUnless")
        public Boolean displayUnless;

        /**
         * @property displayIf specifies if we should display the step only if the operator condition is met
         */
        @SerializedName("displayIf")
        public Boolean displayIf;

        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public Object value;
    }

    public static class Validation implements Serializable {
        @SerializedName("answer")
        public String answer;
    }

}
