package co.touchlab.touchkit.rk.common.step;
import android.os.Parcel;

import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.dev.DevUtils;

public class QuestionStep extends Step
{
    private AnswerFormat              answerFormat;

    private String placeholder;

    private QuestionStep(String identifier, String title)
    {
        super(identifier, title);
        setOptional(true);
        setUseSurveyMode(true);
    }

    public QuestionStep(String identifier, String title, AnswerFormat format)
    {
        super(identifier, title);
        this.answerFormat = format;
    }

    public QuestionStep(Parcel in)
    {
        super(in);
        answerFormat = in.readParcelable(AnswerFormat.class.getClassLoader());
    }

    @Override
    public Class getStepFragment()
    {
        DevUtils.throwUnsupportedOpException();
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(answerFormat, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public AnswerFormat getAnswerFormat() {
        return answerFormat;
    }

    public String getPlaceholder()
    {
        return placeholder;
    }

    public void setPlaceholder(String placeholder)
    {
        this.placeholder = placeholder;
    }

    public AnswerFormat.QuestionType getQuestionType()
    {
        return answerFormat.getQuestionType();
    }

    public static final Creator<QuestionStep> CREATOR = new Creator<QuestionStep>()
    {
        @Override
        public QuestionStep createFromParcel(Parcel in)
        {
            return new QuestionStep(in);
        }

        @Override
        public QuestionStep[] newArray(int size)
        {
            return new QuestionStep[size];
        }
    };
}
