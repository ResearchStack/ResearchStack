package co.touchlab.researchstack.core.model;

import java.io.Serializable;
import java.util.List;

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

}
