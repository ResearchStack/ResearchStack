package co.touchlab.touchkit.rk.common.result;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradleymcdermott on 10/9/15.
 */
public class StepResult<T> extends Result
{
    public Map<String, T> results;

    public StepResult(String identifier)
    {
        super(identifier);
        this.results = new HashMap<>();
    }

    public Map<String, T> getResults()
    {
        return results;
    }

    public void setResults(Map<String, T> results)
    {
        this.results = results;
    }

    public T getResultForIdentifier(String identifier)
    {
        return results.get(identifier);
    }

    public T setResultForIdentifier(String identifier,  T result)
    {
        return results.put(identifier, result);
    }
}
