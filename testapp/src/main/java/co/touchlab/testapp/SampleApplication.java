package co.touchlab.testapp;

import android.content.Context;

import co.touchlab.touchkit.rk.TouchKitApplication;
import co.touchlab.touchkit.rk.common.Constants;
import co.touchlab.touchkit.rk.common.model.ConsentSectionModel;
import co.touchlab.touchkit.rk.utils.JsonUtils;

/**
 * Created by bradleymcdermott on 11/12/15.
 */
public class SampleApplication extends TouchKitApplication
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
    public String getExternalSDAppFolder()
    {
        return "demo_touchkit";
    }

    @Override
    public int getAppName()
    {
        return co.touchlab.touchkit.rk.R.string.app_name;
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
}
