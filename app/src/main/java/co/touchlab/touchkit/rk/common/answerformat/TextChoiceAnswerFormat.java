package co.touchlab.touchkit.rk.common.answerformat;
import android.os.Parcel;

import co.touchlab.touchkit.rk.common.helpers.TextChoice;

public class TextChoiceAnswerFormat extends AnswerFormat
{

    private AnswerFormat.ChoiceAnswerStyle answerStyle;
    private TextChoice[]                   textChoices;

    public TextChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle answerStyle, TextChoice[] textChoices)
    {
        this.answerStyle = answerStyle;
        this.textChoices = textChoices;
    }

    public TextChoiceAnswerFormat(Parcel in)
    {
        answerStyle = ChoiceAnswerStyle.values()[in.readInt()];
        textChoices = in.createTypedArray(TextChoice.CREATOR);
    }

    @Override
    public QuestionType getQuestionType()
    {
        return QuestionType.SingleChoice;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(answerStyle.ordinal());
        dest.writeTypedArray(textChoices, 0);
    }

    public TextChoice[] getTextChoices() {
        return textChoices;
    }
}
