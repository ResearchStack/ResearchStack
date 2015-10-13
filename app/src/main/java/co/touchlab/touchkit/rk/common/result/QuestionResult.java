package co.touchlab.touchkit.rk.common.result;

import android.os.Parcel;

/**
 * Created by bradleymcdermott on 10/9/15.
 */
public class QuestionResult extends Result
{
    private Boolean answer = null;

    public QuestionResult(String identifier)
    {
        super(identifier);
    }

    public QuestionResult(Parcel in)
    {
        super(in);
        answer = in.readInt() == 1;
    }

    public void setAnswer(Boolean answer)
    {
        this.answer = answer;
    }

    public Boolean getAnswer()
    {
        return answer;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest,
                flags);
        dest.writeInt(answer ? 1 : 0);
    }

    public static final Creator<QuestionResult> CREATOR = new Creator<QuestionResult>()
    {
        @Override
        public QuestionResult createFromParcel(Parcel in)
        {
            return new QuestionResult(in);
        }

        @Override
        public QuestionResult[] newArray(int size)
        {
            return new QuestionResult[size];
        }
    };
}
