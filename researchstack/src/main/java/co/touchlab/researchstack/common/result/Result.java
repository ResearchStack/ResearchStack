package co.touchlab.researchstack.common.result;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Result implements Serializable
{

    private String                  identifier;

    //TODO: these should probably go?  Steps don't need this.
    private Date                    startDate;
    private Date                    endDate;

    private HashMap<String, Object> userInfo; //TODO Implement
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

    public HashMap<String, Object> getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(HashMap<String, Object> userInfo)
    {
        this.userInfo = userInfo;
    }

    public boolean isSaveable()
    {
        return saveable;
    }
}
