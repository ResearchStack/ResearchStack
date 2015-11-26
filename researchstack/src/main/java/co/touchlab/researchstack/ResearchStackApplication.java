package co.touchlab.researchstack;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import co.touchlab.researchstack.common.Constants;
import co.touchlab.researchstack.common.helpers.LogExt;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.storage.FileAccess;
import co.touchlab.researchstack.common.storage.SecurityProfile;
import co.touchlab.researchstack.common.storage.aes.AesFileAccess;

public abstract class ResearchStackApplication extends Application
{
    public static final String TEMP_USER_JSON_FILE_NAME = "temp_user";
    protected static ResearchStackApplication instance;

    private User currentUser;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    //TODO Thread safe
    public static ResearchStackApplication getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException("Accessing instance of application before onCreate");
        }

        return instance;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // File Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public abstract int getStudyOverviewResourceId();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public abstract int getLargeLogoDiseaseIcon();

    public abstract int getConsentForm();

    public abstract int getConsentSections();

    public abstract int getQuizSections();

    public abstract int getLearnSections();

    public abstract int getPrivacyPolicy();

    public abstract int getLicenseSections();

    public String getExternalSDAppFolder()
    {
        return "demo_researchstack";
    }

    public abstract int getAppName();

    public abstract SecurityProfile getSecurityProfile();

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

    public abstract boolean isSignatureEnabledInConsent();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Other (unorganized)
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    // TODO use this for deciding what info to collect during signup, hardcoded in layouts for now
    public abstract Constants.UserInfoType[] getUserInfoTypes();

    public abstract Class getInclusionCriteriaSceneClass();

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

    /**
     * File access interface.  Should either be clear, or used with standard encryption.  If you
     * need something funky, override.
     * @return
     */
    public abstract FileAccess getFileAccess();
}
