package co.touchlab.researchstack.core.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConsentSectionModel
{

    @SerializedName("documentProperties")
    DocumentProperties properties;

    @SerializedName("sections")
    List<ConsentSection> sections;

    public List<ConsentSection> getSections()
    {
        return sections;
    }

    public DocumentProperties getDocumentProperties()
    {
        return properties;
    }
}
