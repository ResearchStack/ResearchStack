package co.touchlab.touchkit.rk.common.result;

import android.os.Bundle;
import android.os.Parcel;

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

    public CollectionResult(Parcel in)
    {
        super(in);
        Bundle bundle = in.readBundle();
        results = new HashMap<>();
        for (String key : bundle.keySet())
        {
            results.put(key,
                    (Result) bundle.getParcelable(key));
        }
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

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest,
                flags);
        Bundle bundle = new Bundle();
        for(Map.Entry<String, Result> entry : results.entrySet())
        {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
        dest.writeBundle(bundle);
    }
}
