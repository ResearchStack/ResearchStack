package co.touchlab.touchkit.rk.common.step;

import java.io.Serializable;

import co.touchlab.touchkit.rk.common.task.Task;

public abstract class Step implements Serializable
{

    private String identifier;

    private boolean restorable;

    private boolean optional;

    private String title;

    private String text;

    private Task task;

    private boolean shouldTintImages;

    private boolean showsProgress;

    private boolean allowsBackNavigation;

    private boolean useSurveyMode;

    public Step(String identifier, String title)
    {
        this.identifier = identifier;
        this.title = title;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public boolean isRestorable()
    {
        return restorable;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public void setOptional(boolean optional)
    {
        this.optional = optional;
    }

    public String getTitle()
    {
        return title;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Task getTask()
    {
        return task;
    }

    public void setTask(Task task)
    {
        this.task = task;
    }

    public boolean isUseSurveyMode()
    {
        return useSurveyMode;
    }

    public void setUseSurveyMode(boolean useSurveyMode)
    {
        this.useSurveyMode = useSurveyMode;
    }

    public boolean isAllowsBackNavigation()
    {
        return allowsBackNavigation;
    }

    public void setAllowsBackNavigation(boolean allowsBackNavigation)
    {
        this.allowsBackNavigation = allowsBackNavigation;
    }

    public boolean isShowsProgress()
    {
        return showsProgress;
    }

    public void setShowsProgress(boolean showsProgress)
    {
        this.showsProgress = showsProgress;
    }

    public boolean isShouldTintImages()
    {
        return shouldTintImages;
    }

    public void setShouldTintImages(boolean shouldTintImages)
    {
        this.shouldTintImages = shouldTintImages;
    }

    public abstract Class getStepFragment();
}
