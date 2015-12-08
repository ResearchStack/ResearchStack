package co.touchlab.researchstack.core.result;

import java.util.HashMap;
import java.util.Map;


/**
 * Where {@link T} is defined as the following:
 * <ul>
 *  <li>{@link String}</li>
 *  <li>{@link Integer}</li>
 *  <li>{@link Float}</li>
 *  <li>{@link Boolean}</li>
 * </ul>
 */
public class StepResult<T> extends Result
{
    /**
     * When StepREsult only has a single value, pair that value with the following key
     */
    public static final String DEFAULT_KEY = "answer";

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

    public T removeResultForIdentifier(String identifier)
    {
        return results.remove(identifier);
    }

    public boolean isEmpty()
    {
        return results.isEmpty();
    }
}
