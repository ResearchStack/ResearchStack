package co.touchlab.touchkit.rk.common.model;
import com.google.gson.annotations.SerializedName;

public class DocumentProperties
{
    @SerializedName("htmlDocument")
    private String htmlDocument;

    @SerializedName("investigatorShortDescription")
    private String investigatorShortDescription;

    @SerializedName("investigatorLongDescription")
    private String investigatorLongDescription;

    @SerializedName("htmlContent")
    private String htmlContent;

    public String getHtmlDocument()
    {
        return htmlDocument;
    }

    public void setHtmlDocument(String htmlDocument)
    {
        this.htmlDocument = htmlDocument;
    }

    public String getInvestigatorShortDescription()
    {
        return investigatorShortDescription;
    }

    public void setInvestigatorShortDescription(String investigatorShortDescription)
    {
        this.investigatorShortDescription = investigatorShortDescription;
    }

    public String getInvestigatorLongDescription()
    {
        return investigatorLongDescription;
    }

    public void setInvestigatorLongDescription(String investigatorLongDescription)
    {
        this.investigatorLongDescription = investigatorLongDescription;
    }

    public String getHtmlContent()
    {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent)
    {
        this.htmlContent = htmlContent;
    }
}
