package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Created by TheMDP on 12/31/16.
 *
 * Generic Type "T" of survey item must also implement Serializable,
 * Otherwise you will see a runtime exception
 */

public class SurveyItem<T extends Serializable> implements Serializable {

    public static final String IDENTIFIER_GSON = "identifier";
    @SerializedName(IDENTIFIER_GSON)
    public String identifier;

    public static final String TYPE_GSON = "type";
    @SerializedName(TYPE_GSON)
    public SurveyItemType type;

    @SerializedName("title")
    public String title;

    @SerializedName("text")
    public String text;

    @SerializedName("footnote")
    public String footnote;

    @SerializedName("items")
    public List<T> items;

    /**
     * This holds the original Json Object that was used to create this object
     * Only is set if this object was created with the SurveyItemAdapter
     */
    private transient String rawJson;

    /**
     * This is simply used to keep track of state for the SurveyItemFactory, and will not be serialized
     */
    private String customSurveyItemType;

    /* Default constructor needed for serilization/deserialization of object */
    SurveyItem() {
        super();
    }

    public static class SurveyItemTypeComparator implements Comparator<SurveyItemType> {

        @Override
        public int compare(SurveyItemType lhs, SurveyItemType rhs) {

            return 0;
        }
    }

    public String getTypeIdentifier() {
        if (isCustomStep()) {
            return customSurveyItemType;
        }
        return type.getValue();
    }

    public String getIdentifier() {
        if (identifier == null) {
            return getTypeIdentifier();
        }
        return identifier;
    }

    @Override
    public int hashCode() {
        if (identifier == null) {
            return super.hashCode();
        }
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SurveyItem)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        SurveyItem rhs = (SurveyItem) obj;

        if (identifier == null || rhs.identifier == null) {
            return false;
        }

        return identifier.equals(rhs.identifier);
    }

    protected void setCustomTypeValue(String value) {
        customSurveyItemType = value;
    }

    public String getCustomTypeValue() {
        return customSurveyItemType;
    }

    public void setRawJson(String json) {
        this.rawJson = json;
    }

    public String getRawJson() {
        return rawJson;
    }

    /**
     * @return true if the survey item type is custom, false otherwise
     */
    public boolean isCustomStep() {
        return customSurveyItemType != null;
    }
}
