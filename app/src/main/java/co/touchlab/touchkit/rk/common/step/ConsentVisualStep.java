package co.touchlab.touchkit.rk.common.step;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;

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
    public boolean isShowsProgress()
    {
        return false;
    }
}
