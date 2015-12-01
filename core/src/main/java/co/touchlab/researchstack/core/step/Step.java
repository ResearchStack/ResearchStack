package co.touchlab.researchstack.core.step;

import java.io.Serializable;

import co.touchlab.researchstack.core.task.Task;


public class Step implements Serializable
{

    private String identifier;

    private Class sceneClass;

    private int sceneTitle;

    private boolean restorable;

    private boolean optional;

    private String title;

    private String text;

    private Task task;

    private boolean shouldTintImages;

    private boolean showsProgress;

    private boolean allowsBackNavigation;

    private boolean useSurveyMode;

    public Step(String identifier)
    {
        this.identifier = identifier;
    }

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

    public void setTitle(String title)
    {
        this.title = title;
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

    public int getSceneTitle()
    {
        return sceneTitle;
    }

    public void setSceneTitle(int sceneTitle)
    {
        this.sceneTitle = sceneTitle;
    }

    public Class getSceneClass()
    {
        return sceneClass;
    }

    public void setSceneClass(Class sceneClass)
    {
        this.sceneClass = sceneClass;
    }

    @Deprecated
    public Class getStepFragment()
    {
        throw new UnsupportedOperationException("Didn't specify a fragment for this step");
    }
}
