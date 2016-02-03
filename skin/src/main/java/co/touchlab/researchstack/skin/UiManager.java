package co.touchlab.researchstack.skin;
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
                    "Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    public abstract List<NavigationItem> getNavigationItems();

    public abstract Class getInclusionCriteriaStepLayoutClass();

    public abstract boolean isSignatureEnabledInConsent();

}
