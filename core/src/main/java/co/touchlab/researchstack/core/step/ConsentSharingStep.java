package co.touchlab.researchstack.core.step;
import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.ui.scene.ConsentSharingScene;

public class ConsentSharingStep extends QuestionStep
{
    private String localizedLearnMoreHTMLContent;

    public ConsentSharingStep(String identifier)
    {
        super(identifier);
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.consent;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentSharingScene.class;
    }

    public String getLocalizedLearnMoreHTMLContent()
    {
        return localizedLearnMoreHTMLContent;
    }

    public void setLocalizedLearnMoreHTMLContent(String localizedLearnMoreHTMLContent)
    {
        this.localizedLearnMoreHTMLContent = localizedLearnMoreHTMLContent;
    }
}
