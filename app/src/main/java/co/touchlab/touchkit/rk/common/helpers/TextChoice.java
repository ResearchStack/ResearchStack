package co.touchlab.touchkit.rk.common.helpers;

import java.io.Serializable;

public class TextChoice implements Serializable
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
}
