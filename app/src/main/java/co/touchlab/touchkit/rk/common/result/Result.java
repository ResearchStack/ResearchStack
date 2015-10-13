package co.touchlab.touchkit.rk.common.result;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;

public class Result implements Parcelable
{

    private String                  identifier;
    private Date                    startDate;
    private Date                    endDate;
    private HashMap<String, Object> userInfo; //TODO Implement
    private boolean                 saveable; //TODO Implement

    public Result(String identifier)
    {
        this.identifier = identifier;
        this.startDate = new Date();
        this.endDate = new Date();
    }

    protected Result(Parcel in)
    {
        identifier = in.readString();
        startDate = new Date(in.readLong());
        endDate = new Date(in.readLong());
        saveable = in.readByte() != 0;
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

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Result)
        {
            Result result = (Result) o;
            return identifier.equals(result.getIdentifier()) &&
                    startDate.equals(result.getStartDate()) &&
                    endDate.equals(result.getEndDate());
        }
        return false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(identifier);
        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());
        dest.writeByte((byte) (saveable ? 1 : 0));
    }

    public static final Creator<Result> CREATOR = new Creator<Result>()
    {
        @Override
        public Result createFromParcel(Parcel in)
        {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size)
        {
            return new Result[size];
        }
    };
}
