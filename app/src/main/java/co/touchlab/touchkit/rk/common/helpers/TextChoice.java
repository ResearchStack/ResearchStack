package co.touchlab.touchkit.rk.common.helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class TextChoice implements Parcelable
{

    private String text;

    private boolean value;

    private String detailText;

    public TextChoice(String text, boolean value, String detailText)
    {
        this.text = text;
        this.value = value;
        this.detailText = detailText;
    }

    public TextChoice(Parcel in)
    {
        text = in.readString();
        value = in.readInt() == 1;
        detailText = in.readString();
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public boolean getValue()
    {
        return value;
    }

    public void setValue(boolean value)
    {
        this.value = value;
    }

    public String getDetailText()
    {
        return detailText;
    }

    public void setDetailText(String detailText)
    {
        this.detailText = detailText;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(text);
        dest.writeInt(value ? 1 : 0);
        dest.writeString(detailText);
    }

    public static final Creator<TextChoice> CREATOR = new Creator<TextChoice>()
    {
        @Override
        public TextChoice createFromParcel(Parcel in)
        {
            return new TextChoice(in);
        }

        @Override
        public TextChoice[] newArray(int size)
        {
            return new TextChoice[size];
        }
    };
}
