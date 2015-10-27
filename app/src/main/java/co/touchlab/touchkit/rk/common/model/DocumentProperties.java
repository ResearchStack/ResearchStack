package co.touchlab.touchkit.rk.common.model;
import com.google.gson.annotations.SerializedName;

public class DocumentProperties
{
    @SerializedName("htmlDocument")
    public String htmlDocument;

    @SerializedName("investigatorShortDescription")
    public String investigatorShortDescription;

    @SerializedName("investigatorLongDescription")
    public String investigatorLongDescription;

    @SerializedName("htmlContent")
    public String htmlContent;
}
