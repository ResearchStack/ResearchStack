package co.touchlab.researchstack.core.step;
public class ConsentSignatureStep extends Step
{
    private String signatureDateFormat;

    public ConsentSignatureStep(String identifier)
    {
        super(identifier);
    }

    public void setSignatureDateFormat(String signatureDateFormat)
    {
        this.signatureDateFormat = signatureDateFormat;
    }

    public String getSignatureDateFormat()
    {
        return signatureDateFormat;
    }
}
