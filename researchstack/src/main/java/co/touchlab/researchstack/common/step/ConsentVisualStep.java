package co.touchlab.researchstack.common.step;
import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.model.ConsentDocument;
import co.touchlab.researchstack.ui.scene.ConsentVisualScene;

public class ConsentVisualStep extends Step
{
    private ConsentDocument document;

    public ConsentVisualStep(String identifier, ConsentDocument document)
    {
        super(identifier);
        this.document = document;
    }

    public ConsentDocument getDocument()
    {
        return document;
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.consent;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentVisualScene.class;
    }

    @Override
    public boolean isShowsProgress()
    {
        return false;
    }
}
