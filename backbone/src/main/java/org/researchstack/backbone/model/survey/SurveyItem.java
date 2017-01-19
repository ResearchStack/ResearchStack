package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 12/31/16.
 * <p>
 * Generic Type "T" of survey item must also implement Serializable,
 * Otherwise you will see a runtime exception
 */

public class SurveyItem<T> implements Serializable {

    static final String TYPE_GSON = "type";
    @SerializedName("identifier")
    public String identifier;
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

    @SerializedName("skipIdentifier")
    public String skipIdentifier;

    @SerializedName("skipIfPassed")
    public boolean skipIfPassed;

    // TODO: implement this?
    public String rulePredicate;  // this is an NSPredicate on iOS, how do we convert?

    // TODO: what is this?
    Map<String, Object> options;

    /* Default constructor needed for serilization/deserialization of object */
    SurveyItem() {
        super();
    }

    public String getTypeIdentifier() {
        return type.getValue();
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

    public static class SurveyItemTypeComparator implements Comparator<SurveyItemType> {

        @Override
        public int compare(SurveyItemType lhs, SurveyItemType rhs) {

            return 0;
        }
    }
}
