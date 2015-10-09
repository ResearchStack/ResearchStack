package co.touchlab.touchkit.rk.common.step;
import android.os.Parcel;
import android.os.Parcelable;

import co.touchlab.touchkit.rk.dev.DevUtils;

public class InstructionStep extends Step implements Parcelable
{

    private String detailText;

    private int imageResourceId;

    public InstructionStep(String identifier, String title, String detailText)
    {
        super(identifier, title);
        this.detailText = detailText;
    }

    public InstructionStep(Parcel in)
    {
        super(in);
        this.detailText = in.readString();
        this.imageResourceId = in.readInt();
    }

    @Override
    public Class getStepFragment()
    {
        DevUtils.throwUnsupportedOpException();
        return null;
    }

    public int getImageResourceId()
    {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId)
    {
        this.imageResourceId = imageResourceId;
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
        super.writeToParcel(dest, flags);
        dest.writeString(getDetailText());
        dest.writeInt(getImageResourceId());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public InstructionStep createFromParcel(Parcel in)
        {
            return new InstructionStep(in);
        }

        public InstructionStep[] newArray(int size)
        {
            return new InstructionStep[size];
        }
    };
}
