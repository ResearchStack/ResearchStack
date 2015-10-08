package co.touchlab.touchkit.rk.common.result;
import java.util.List;

import co.touchlab.touchkit.rk.dev.DevUtils;

public class CollectionResult extends Result
{

    public List<Result> results;

    public CollectionResult(String identifier)
    {
        super(identifier);
    }

    public List<Result> getResults()
    {
        return results;
    }

    public void setResults(List<Result> results)
    {
        this.results = results;
    }

    public Result getResultForIdentifier(String identifier)
    {
        DevUtils.throwUnsupportedOpException();
        return null;
    }

}
