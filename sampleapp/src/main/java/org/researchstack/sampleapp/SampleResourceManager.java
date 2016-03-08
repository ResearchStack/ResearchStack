package org.researchstack.sampleapp;
import org.researchstack.skin.ResourceManager;

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
        return R.raw.learn;
    }

    @Override
    public int getSoftwareNotices()
    {
        return R.raw.software_notices;
    }

    @Override
    public int getConsentPDF()
    {
        return R.raw.study_overview_consent_form;
    }

    @Override
    public int getConsentHtml()
    {
        return R.raw.asthma_fullconsent;
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

    @Override
    public int getLogoInstitution()
    {
        return R.drawable.logo_institution;
    }

}
