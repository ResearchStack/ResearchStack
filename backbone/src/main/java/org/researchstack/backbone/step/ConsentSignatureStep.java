package org.researchstack.backbone.step;
public class ConsentSignatureStep extends Step
{
    private String signatureDateFormat;

    public ConsentSignatureStep(String identifier)
    {
        super(identifier);
    }

    public String getSignatureDateFormat()
    {
        return signatureDateFormat;
    }

    public void setSignatureDateFormat(String signatureDateFormat)
    {
        this.signatureDateFormat = signatureDateFormat;
    }
}
