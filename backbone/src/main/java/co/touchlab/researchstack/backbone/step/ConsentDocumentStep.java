package co.touchlab.researchstack.backbone.step;
import co.touchlab.researchstack.backbone.R;
import co.touchlab.researchstack.backbone.ui.step.layout.ConsentDocumentStepLayout;

public class ConsentDocumentStep extends Step
{
    private String html;

    private String confirmMessage;

    public ConsentDocumentStep(String identifier)
    {
        super(identifier);
    }

    @Override
    public int getStepTitle()
    {
        return R.string.rsb_consent;
    }

    @Override
    public Class getStepLayoutClass()
    {
        return ConsentDocumentStepLayout.class;
    }

    public String getConsentHTML()
    {
        return html;
    }

    public void setConsentHTML(String html)
    {
        this.html = html;
    }

    public String getConfirmMessage()
    {
        return confirmMessage;
    }

    public void setConfirmMessage(String confirmMessage)
    {
        this.confirmMessage = confirmMessage;
    }
}
