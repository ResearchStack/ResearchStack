package co.touchlab.researchstack.skin;
import java.util.List;

import co.touchlab.researchstack.backbone.step.Step;

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

}
