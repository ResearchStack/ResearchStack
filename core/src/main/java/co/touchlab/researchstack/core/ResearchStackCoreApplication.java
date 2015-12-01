package co.touchlab.researchstack.core;

import android.app.Application;
import android.content.Context;

import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.file.FileAccess;

public abstract class ResearchStackCoreApplication extends Application
{
    protected static ResearchStackCoreApplication instance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    //TODO Thread safe
    public static ResearchStackCoreApplication getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException("Accessing instance of application before onCreate");
        }

        return instance;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public String getExternalSDAppFolder()
    {
        return "demo_researchstack";
    }

    public String getHTMLFilePath(String docName)
    {
        return getRawFilePath(docName, "html");
    }

    public String getPDFFilePath(String docName)
    {
        return getRawFilePath(docName, "pdf");
    }

    public String getRawFilePath(String docName, String postfix)
    {
        return "file:///android_res/raw/" + docName + "." + postfix;
    }

    public int getDrawableResourceId(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Back End
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public abstract AppDatabase getAppDatabase();

    /**
     * File access interface.  Should either be clear, or used with standard encryption.  If you
     * need something funky, override.
     * @return
     */
    public abstract FileAccess getFileAccess();
}
