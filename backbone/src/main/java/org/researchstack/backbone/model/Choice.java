package org.researchstack.backbone.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Choice <T> implements Serializable
{
    private String text;

    private T value;

    private String detailText;

    public Choice(String text, T value)
    {
        this(text, value, null);
    }

    public Choice(String text, T value, String detailText)
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

    public static List<Choice<Integer>> from(List<String> textChoices)
    {
        List<Choice<Integer>> choices = new ArrayList<>();
        for(String textChoice : textChoices)
        {
            choices.add(new Choice<>(textChoice, choices.size()));
        }
        return choices;
    }
}
