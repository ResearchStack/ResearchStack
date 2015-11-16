package co.touchlab.researchstack.common.helpers;

import java.io.Serializable;
import java.util.List;

import co.touchlab.researchstack.common.model.TaskModel;

public class TextChoice<T> implements Serializable
{
    private String text;

    private T value;

    private String detailText;

    public TextChoice(String text, T value, String detailText)
    {
        this.text = text;
        this.value = value;
        this.detailText = detailText;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
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

    public static <T>TextChoice[] from(List<TaskModel.EnumerationModel> enumeration)
    {
        TextChoice[] textChoices = new TextChoice[enumeration.size()];

        for (int i = 0; i < enumeration.size(); i++)
        {
            TaskModel.EnumerationModel choice = enumeration.get(i);
            // TODO none of the examples seem to have detail text, add that if we find it
            textChoices[i] = new TextChoice(choice.label, choice.value, null);
        }
        return textChoices;
    }
}
