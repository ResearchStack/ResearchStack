package co.touchlab.researchstack.core.step;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.ui.scene.ConsentVisualScene;

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
