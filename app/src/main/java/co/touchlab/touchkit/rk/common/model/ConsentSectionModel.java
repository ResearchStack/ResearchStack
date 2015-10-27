package co.touchlab.touchkit.rk.common.model;
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
}
