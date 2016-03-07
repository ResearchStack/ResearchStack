package org.researchstack.skin;
import org.researchstack.backbone.step.Step;
import org.researchstack.skin.notification.TaskNotificationReceiver;

import java.util.List;

public abstract class UiManager
{
    private static UiManager instance;

    public static void init(UiManager manager)
    {
        UiManager.instance = manager;
    }

    public static UiManager getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "UiManager instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    public abstract List<ActionItem> getMainActionBarItems();

    public abstract List<ActionItem> getMainTabBarItems();

    public abstract Step getInclusionCriteriaStep();

    public abstract boolean isSignatureEnabledInConsent();

    // Override this if you want to allow the user to get to the main portion of the app without
    // signing up for the study and consenting
    public boolean isConsentSkippable()
    {
        return false;
    }

    public Class<?> getTaskNotificationReceiver()
    {
        return TaskNotificationReceiver.class;
    }
}
