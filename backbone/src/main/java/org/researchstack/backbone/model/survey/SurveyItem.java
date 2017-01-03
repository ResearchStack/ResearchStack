package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.SurveyFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 12/31/16.
 */

public class SurveyItem<T> {

    @SerializedName("identifier")
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

    @SerializedName("skipIdentifier")
    public String skipIdentifier;

    @SerializedName("skipIfPassed")
    public boolean skipIfPassed;

    // TODO: implement this?
    public String rulePredicate;  // this is an NSPredicate on iOS, how do we convert?

    // TODO: what is this?
    Map<String, Object> options;

    public static class SurveyItemTypeComparator implements Comparator<SurveyItemType> {

        @Override
        public int compare(SurveyItemType lhs, SurveyItemType rhs) {

            return 0;
        }
    }
}
