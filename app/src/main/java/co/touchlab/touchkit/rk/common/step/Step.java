package co.touchlab.touchkit.rk.common.step;

import android.os.Parcel;
import android.os.Parcelable;

import co.touchlab.touchkit.rk.common.task.Task;

public class Step implements Parcelable
{

    private String identifier;

    private boolean restorable;

    private boolean optional;

    private String title;

    private String text;

    private Task task;

    public Step(String identifier)
    {
        this.identifier = identifier;
    }

    public Step(Parcel in)
    {
        identifier = in.readString();
        restorable = in.readInt() == 1;
        optional = in.readInt() == 1;
        title = in.readString();
        text = in.readString();
        task = in.readParcelable(Task.class.getClassLoader());
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public boolean isRestorable()
    {
        return restorable;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public void setOptional(boolean optional)
    {
        this.optional = optional;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Task getTask()
    {
        return task;
    }

    public void setTask(Task task)
    {
        this.task = task;
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
        dest.writeInt(restorable ? 1 : 0);
        dest.writeInt(optional ? 1 : 0);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeParcelable(task, 0);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Step createFromParcel(Parcel in)
        {
            return new Step(in);
        }

        public Step[] newArray(int size)
        {
            return new Step[size];
        }
    };
}
