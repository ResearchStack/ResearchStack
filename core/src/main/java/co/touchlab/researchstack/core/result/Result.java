package co.touchlab.researchstack.core.result;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable
{

    private String                  identifier;

    //TODO: these should probably go?  Steps don't need this.
    private Date                    startDate;
    private Date                    endDate;

    private boolean                 saveable; //TODO Implement

    public Result(String identifier)
    {
        this.identifier = identifier;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public boolean isSaveable()
    {
        return saveable;
    }
}
