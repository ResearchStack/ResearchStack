package co.touchlab.researchstack.glue;
public abstract class ResourceManager
{
    private static ResourceManager instance;

    public static void init(ResourceManager manager)
    {
        ResourceManager.instance = manager;
    }

    public static ResourceManager getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    public abstract int getStudyOverviewSections();

    public abstract int getLargeLogoDiseaseIcon();

    public abstract int getConsentPDF();

    public abstract int getConsentSections();

    public abstract int getQuizSections();

    public abstract int getLearnSections();

    public abstract int getPrivacyPolicy();

    public abstract int getLicenseSections();

}
