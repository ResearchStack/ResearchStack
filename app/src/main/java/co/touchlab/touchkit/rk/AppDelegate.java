package co.touchlab.touchkit.rk;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import co.touchlab.touchkit.rk.common.Constants;
import co.touchlab.touchkit.rk.common.model.ConsentSectionModel;
import co.touchlab.touchkit.rk.common.model.User;

public class AppDelegate
{

    public static AppDelegate instance;

    private User currentUser;

    //TODO Thread safe
    public static AppDelegate getInstance()
    {
        if(instance == null)
        {
            instance = new AppDelegate();
        }

        return instance;
    }

    private AppDelegate()
    {
        // TODO save and load user object
        currentUser = new User();
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // File Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public int getStudyOverviewResourceId()
    {
        return R.raw.study_overview;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public int getLargeLogoDiseaseIcon()
    {
        return R.drawable.logo_disease_large;
    }

    public int getConsentForm()
    {
        return R.raw.study_overview_consent_form;
    }

    public int getConsentSections()
    {
        return R.raw.consent_section;
    }

    public String getExternalSDAppFolder()
    {
        return "demo_touchkit";
    }

    public int getAppName()
    {
        return R.string.app_name;
    }

    //TODO Read on main thread for intense UI blockage.
    public ConsentSectionModel getConsentSectionsAndHtmlContent(Resources r)
    {

        Gson gson = new GsonBuilder().create();
        InputStream stream = r.openRawResource(R.raw.consent_section);
        Reader reader = null;
        try
        {
            reader = new InputStreamReader(stream, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }

        return gson.fromJson(reader, ConsentSectionModel.class);

    }

    public String getHTMLFilePath(String docName)
    {
        return "file:///android_res/raw/" + docName + ".html";
    }

    public boolean isSignatureEnabledInConsent()
    {
        return true;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Other (unorganized)
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    // TODO use this for deciding what info to collect during signup, hardcoded in layouts for now
    public Constants.UserInfoType[] getUserInfoTypes()
    {
        return new Constants.UserInfoType[] {Constants.UserInfoType.Name,
                Constants.UserInfoType.Email, Constants.UserInfoType.DateOfBirth,
                Constants.UserInfoType.Height, Constants.UserInfoType.Weight};
    }

    public User getCurrentUser()
    {
        return currentUser;
    }
}
