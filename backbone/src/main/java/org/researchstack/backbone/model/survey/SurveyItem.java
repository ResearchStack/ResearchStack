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

    static final String IDENTIFIER_GSON = "identifier";
    @SerializedName(IDENTIFIER_GSON)
    public String identifier;

    static final String TYPE_GSON = "type";
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
}
