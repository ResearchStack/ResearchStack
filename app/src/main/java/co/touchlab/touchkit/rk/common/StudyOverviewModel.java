package co.touchlab.touchkit.rk.common;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class StudyOverviewModel
{

    @SerializedName("disease_name")
    private String diseaseName;

    @SerializedName("from_date")
    private Date fromDate;

    @SerializedName("to_date")
    private Date toDate;

    @SerializedName("logo_name")
    private String logoName;

    @SerializedName("questions")
    private List<Question> questions;

    public List<Question> getQuestions()
    {
        return questions;
    }

    public static class Question
    {
        @SerializedName("title")
        String title;

        @SerializedName("details")
        String details;

        @SerializedName("show_consent")
        String showConsent;

        @SerializedName("icon_image")
        String iconImage;

        @SerializedName("tint_color")
        String tintColor;

        @SerializedName("video_name")
        String videoName;

        public String getTitle()
        {
            return title;
        }

        public String getDetails()
        {
            return details;
        }

        public String getShowConsent()
        {
            return showConsent;
        }

        public String getIconImage()
        {
            return iconImage;
        }

        public String getTintColor()
        {
            return tintColor;
        }

        public String getVideoName()
        {
            return videoName;
        }
    }

}
