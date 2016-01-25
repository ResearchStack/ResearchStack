package co.touchlab.researchstack.sampleapp;
import co.touchlab.researchstack.glue.ResourceManager;

public class SampleResourceManager extends ResourceManager
{
    @Override
    public int getStudyOverviewSections()
    {
        return R.raw.study_overview;
    }

    @Override
    public int getConsentSections()
    {
        return R.raw.consent_section;
    }

    @Override
    public int getQuizSections()
    {
        return R.raw.quiz_section;
    }

    @Override
    public int getLearnSections()
    {
        return R.raw.learn_items;
    }

    @Override
    public int getLicenseSections()
    {
        return R.raw.license_items;
    }

    @Override
    public int getConsentPDF()
    {
        return R.raw.study_overview_consent_form;
    }

    @Override
    public int getPrivacyPolicy()
    {
        return R.raw.app_privacy_policy;
    }

    @Override
    public int getLargeLogoDiseaseIcon()
    {
        return R.drawable.logo_disease_large;
    }

}
