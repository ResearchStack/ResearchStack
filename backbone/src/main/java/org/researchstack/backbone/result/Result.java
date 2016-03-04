package org.researchstack.backbone.result;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable
{
    private String identifier;

    private Date startDate;

    private Date endDate;

    // TODO Implement private boolean saveable;

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

}
