package co.touchlab.touchkit.rk;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import co.touchlab.touchkit.rk.common.Constants;
import co.touchlab.touchkit.rk.common.helpers.LogExt;
import co.touchlab.touchkit.rk.common.model.ConsentSectionModel;
import co.touchlab.touchkit.rk.common.model.User;
import co.touchlab.touchkit.rk.utils.JsonUtils;

public class AppDelegate
{

    private static final String TEMP_USER_JSON_FILE_NAME = "temp_user";
    public static AppDelegate instance;

    private User currentUser;

    //TODO Thread safe
    public static AppDelegate getInstance()
    {
        if (instance == null)
        {
            instance = new AppDelegate();
        }

        return instance;
    }

    private AppDelegate()
    {
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
    public ConsentSectionModel getConsentSectionsAndHtmlContent(Context context)
    {
        return JsonUtils.loadClassFromRawJson(context,
                ConsentSectionModel.class,
                R.raw.consent_section);

    }

    public String getHTMLFilePath(String docName)
    {
        return getRawFilePath(docName,
                "html");
    }

    public String getPDFFilePath(String docName)
    {
        return getRawFilePath(docName,
                "pdf");
    }


    public String getRawFilePath(String docName, String postfix)
    {
        return "file:///android_res/raw/" + docName + "." + postfix;
    }

    public int getDrawableResourceId(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
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
        return new Constants.UserInfoType[]{Constants.UserInfoType.Name,
                Constants.UserInfoType.Email, Constants.UserInfoType.DateOfBirth,
                Constants.UserInfoType.Height, Constants.UserInfoType.Weight};
    }

    public User getCurrentUser()
    {
        return currentUser;
    }

    public void saveUser(Context context)
    {
        File userJsonFile = new File(context.getFilesDir(),
                TEMP_USER_JSON_FILE_NAME);

        Gson gson = new Gson();
        String userJsonString = gson.toJson(getCurrentUser());

        LogExt.d(getClass(),
                "Writing user json:\n" + userJsonString);

        try
        {
            FileOutputStream outputStream = new FileOutputStream(userJsonFile);
            outputStream.write(userJsonString.getBytes());
            outputStream.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void loadUser(Context context)
    {
        Gson gson = new Gson();
        File userJsonFile = new File(context.getFilesDir(),
                TEMP_USER_JSON_FILE_NAME);
        if (userJsonFile.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(userJsonFile);
                Reader reader = new InputStreamReader(inputStream);
                currentUser = gson.fromJson(reader,
                        User.class);
                LogExt.d(getClass(),
                        "Loaded user json");
            }
            catch (FileNotFoundException e)
            {
                LogExt.e(getClass(),
                        "Error loading user file");
                e.printStackTrace();
            }
        }

        if (currentUser == null)
        {
            LogExt.d(getClass(),
                    "No user file found, creating new user");
            currentUser = new User();
        }
    }

    public void clearUserData(Context context)
    {
        File userJsonFile = new File(context.getFilesDir(),
                TEMP_USER_JSON_FILE_NAME);
        if (userJsonFile.exists())
        {
            boolean deleted = userJsonFile.delete();
            LogExt.d(getClass(),
                    "Deleted user data: " + deleted);

        }
    }
}
