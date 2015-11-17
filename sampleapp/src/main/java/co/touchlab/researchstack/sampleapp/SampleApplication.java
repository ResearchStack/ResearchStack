package co.touchlab.researchstack.sampleapp;

import android.content.Context;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.Constants;
import co.touchlab.researchstack.common.model.ConsentSectionModel;
import co.touchlab.researchstack.utils.JsonUtils;

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
    public int getPrivacyPolicy()
    {
        return R.raw.app_privacy_policy;
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

    //TODO Read on main thread for intense UI blockage.
    @Override
    public ConsentSectionModel getConsentSectionsAndHtmlContent(Context context)
    {
        return JsonUtils.loadClassFromRawJson(context,
                ConsentSectionModel.class,
                R.raw.consent_section);

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
