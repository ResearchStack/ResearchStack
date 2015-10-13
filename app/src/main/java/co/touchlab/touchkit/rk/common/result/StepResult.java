package co.touchlab.touchkit.rk.common.result;

import android.os.Bundle;
import android.os.Parcel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradleymcdermott on 10/9/15.
 */
public class StepResult extends Result
{
    public Map<String, QuestionResult> results;

    public StepResult(String identifier)
    {
        super(identifier);
        this.results = new HashMap<>();
    }

    protected StepResult(Parcel in)
    {
        super(in);
        Bundle bundle = in.readBundle(QuestionResult.class.<Boolean>getClassLoader());
        results = new HashMap<>();
        for (String key : bundle.keySet())
        {
            results.put(key,
                    (QuestionResult) bundle.getParcelable(key));
        }
    }

    public Map<String, QuestionResult> getResults()
    {
        return results;
    }

    public void setResults(Map<String, QuestionResult> results)
    {
        this.results = results;
    }

    public QuestionResult getResultForIdentifier(String identifier)
    {
        return results.get(identifier);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest,
                flags);
        Bundle bundle = new Bundle();
        for(Map.Entry<String, QuestionResult> entry : results.entrySet())
        {
            bundle.putParcelable(entry.getKey(),
                    entry.getValue());
        }
        dest.writeBundle(bundle);
    }

    public static final Creator<StepResult> CREATOR = new Creator<StepResult>()
    {
        @Override
        public StepResult createFromParcel(Parcel in)
        {
            return new StepResult(in);
        }

        @Override
        public StepResult[] newArray(int size)
        {
            return new StepResult[size];
        }
    };
}
