package co.touchlab.researchstack.sampleapp;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.Constants;

/**
 * Created by bradleymcdermott on 11/12/15.
 */
public class SampleApplication extends ResearchStackApplication
{
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // File Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    @Override
    public int getStudyOverviewResourceId()
    {
        return R.raw.study_overview;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    @Override
    public int getLargeLogoDiseaseIcon()
    {
        return R.drawable.logo_disease_large;
    }

    @Override
    public int getConsentForm()
    {
        return R.raw.study_overview_consent_form;
    }

    @Override
    public int getConsentSections()
    {
        return R.raw.consent_section;
    }

    @Override
    public int getLearnSections()
    {
        return R.raw.learn_items;
    }

    @Override
    public int getPrivacyPolicy()
    {
        return R.raw.app_privacy_policy;
    }

    @Override
    public int getLicenseSections()
    {
        return R.raw.license_items;
    }

    @Override
    public String getExternalSDAppFolder()
    {
        return "demo_researchstack";
    }

    @Override
    public int getAppName()
    {
        return co.touchlab.researchstack.R.string.app_name;
    }

    @Override
    public boolean isSignatureEnabledInConsent()
    {
        return true;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Other (unorganized)
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    // TODO use this for deciding what info to collect during signup, hardcoded in layouts for now
    @Override
    public Constants.UserInfoType[] getUserInfoTypes()
    {
        return new Constants.UserInfoType[]{
                Constants.UserInfoType.Name,
                Constants.UserInfoType.Email,
                Constants.UserInfoType.BiologicalSex,
                Constants.UserInfoType.DateOfBirth,
                Constants.UserInfoType.Height,
                Constants.UserInfoType.Weight
        };
    }

    @Override
    public Class getInclusionCriteriaSceneClass()
    {
        return SignUpInclusionCriteriaScene.class;
    }
}
