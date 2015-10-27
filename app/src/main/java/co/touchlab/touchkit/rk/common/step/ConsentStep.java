package co.touchlab.touchkit.rk.common.step;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;

public class ConsentStep extends Step
{
    private ConsentDocument document;

    public ConsentStep(String identifier, ConsentDocument document)
    {
        super(identifier);
        this.document = document;
    }

    public ConsentDocument getDocument()
    {
        return document;
    }

    @Override
    public boolean equals(Object o)
    {
        return document.equals(o);
    }

    @Override
    public boolean isShowsProgress()
    {
        return false;
    }
}
