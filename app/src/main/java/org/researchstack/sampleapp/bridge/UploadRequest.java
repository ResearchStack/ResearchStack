package org.researchstack.sampleapp.bridge;
import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;


@DatabaseTable
public class UploadRequest
{
    @DatabaseField(generatedId = true)
    public int    id;
    @DatabaseField
    public String name;
    @DatabaseField
    public long   contentLength;
    @DatabaseField
    public String contentMd5;
    @DatabaseField
    public String contentType;
    @DatabaseField(canBeNull = true)
    public String bridgeId;

    public UploadRequest()
    {

    }

    public UploadRequest(String name, long contentLength, String contentMd5, String contentType)
    {
        this.name = name;
        this.contentLength = contentLength;
        this.contentMd5 = contentMd5;
        this.contentType = contentType;
    }
}
