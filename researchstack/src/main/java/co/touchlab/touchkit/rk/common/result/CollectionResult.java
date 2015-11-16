package co.touchlab.touchkit.rk.common.result;

import java.util.HashMap;
import java.util.Map;

import co.touchlab.touchkit.rk.dev.DevUtils;

public class CollectionResult extends Result
{

    public Map<String, Result> results;

    public CollectionResult(String identifier)
    {
        super(identifier);
        this.results = new HashMap<>();
    }

    public Map<String, Result> getResults()
    {
        return results;
    }

    public void setResults(Map<String, Result> results)
    {
        this.results = results;
    }

    public Result getResultForIdentifier(String identifier)
    {
        DevUtils.throwUnsupportedOpException();
        return null;
    }
}
