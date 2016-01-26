package co.touchlab.researchstack.sampleapp;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.glue.ResearchStack;

public class SampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        ResearchStack.init(this, new SampleResearchStack());

        copyDbFile(this);
    }

    //TODO For debug purposes only
    @Deprecated
    public static void copyDbFile(Context context)
    {
        try
        {
            File folder = new File(Environment.getExternalStorageDirectory(), "researchstack");
            folder.mkdirs();
            File databasePath = context.getDatabasePath(DatabaseHelper.DB_NAME);
            File outFile = new File(folder, databasePath.getName());
            FileOutputStream output = new FileOutputStream(outFile);
            Files.copy(databasePath, output);
            output.close();
        }
        catch(IOException e)
        {
            LogExt.e("SampleApplication", "copyDbFile", e);
        }
    }

}
