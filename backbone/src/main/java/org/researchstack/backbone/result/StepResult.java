package org.researchstack.backbone.result;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Where {@link T} is defined as the following:
 * <ul>
 * <li>{@link String}</li>
 * <li>{@link Integer}</li>
 * <li>{@link Float}</li>
 * <li>{@link Boolean}</li>
 * </ul>
 */
public class StepResult <T> extends Result
{
    /**
     * When StepResult only has a single value, pair that value with the following key
     */
    public static final String DEFAULT_KEY = "answer";

    private Map<String, T> results;

    private AnswerFormat answerFormat;

    public StepResult(Step step)
    {
        this(step.getIdentifier());

        if(step instanceof QuestionStep)
        {
            answerFormat = ((QuestionStep) step).getAnswerFormat();
        }
        setStartDate(new Date());
        // this will be updated when the result is set
        setEndDate(new Date());
    }

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

    public T getResult()
    {
        return getResultForIdentifier(DEFAULT_KEY);
    }

    public void setResult(T result)
    {
        setResultForIdentifier(DEFAULT_KEY, result);
        setEndDate(new Date());
    }

    public T getResultForIdentifier(String identifier)
    {
        return results.get(identifier);
    }

    public T setResultForIdentifier(String identifier, T result)
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

    public AnswerFormat getAnswerFormat()
    {
        return answerFormat;
    }
}
