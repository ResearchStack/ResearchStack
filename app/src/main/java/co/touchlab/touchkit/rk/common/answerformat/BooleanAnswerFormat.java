package co.touchlab.touchkit.rk.common.answerformat;
import android.os.Parcel;

import co.touchlab.touchkit.rk.common.helpers.TextChoice;

public class BooleanAnswerFormat extends TextChoiceAnswerFormat
{

    public BooleanAnswerFormat() {
        super(ChoiceAnswerStyle.SingleChoice, new TextChoice[] {
                new TextChoice("Yes", true, null), new TextChoice("No", false, null)

        });
    }

    public BooleanAnswerFormat(Parcel in)
    {
        super(in);
    }

    @Override
    public QuestionType getQuestionType()
    {
        return QuestionType.Boolean;
    }


    public static final Creator<BooleanAnswerFormat> CREATOR = new Creator<BooleanAnswerFormat>()
    {
        @Override
        public BooleanAnswerFormat createFromParcel(Parcel in)
        {
            return new BooleanAnswerFormat(in);
        }

        @Override
        public BooleanAnswerFormat[] newArray(int size)
        {
            return new BooleanAnswerFormat[size];
        }
    };

}
