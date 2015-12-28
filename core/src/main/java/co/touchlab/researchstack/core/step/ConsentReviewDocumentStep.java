package co.touchlab.researchstack.core.step;
import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.ui.scene.ConsentReviewDocumentScene;

public class ConsentReviewDocumentStep extends Step
{
    private String html;

    private String confirmMessage;

    public ConsentReviewDocumentStep(String identifier)
    {
        super(identifier);
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.rsc_consent;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentReviewDocumentScene.class;
    }

    public void setConsentHTML(String html)
    {
        this.html = html;
    }

    public String getConsentHTML()
    {
        return html;
    }

    public void setConfirmMessage(String confirmMessage)
    {
        this.confirmMessage = confirmMessage;
    }

    public String getConfirmMessage()
    {
        return confirmMessage;
    }
}
