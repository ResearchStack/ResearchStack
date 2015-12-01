package co.touchlab.researchstack.sampleapp;

import android.os.Environment;
import android.util.Log;

import com.joanzapata.pdfview.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import co.touchlab.researchstack.glue.ResearchStackApplication;
import co.touchlab.researchstack.glue.common.Constants;
import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.aes.AesFileAccess;

/**
 * Created by bradleymcdermott on 11/12/15.
 */
public class SampleApplication extends ResearchStackApplication
{
    public static final String TEST_SOME_DATA = "Test some data";
    public static final String SOMEDATA_TXT = "somedata.txt";
    private AesFileAccess aesFileAccess = new AesFileAccess(256, false, 6);

    @Override
    public void onCreate()
    {
        super.onCreate();

        if(BuildConfig.DEBUG)
            copyDbFile();

        //        SQLiteDatabase.loadLibs(this);
        try
        {
            /*DataEncoder dataEncoder = new DataEncoder("1234".toCharArray());
            DataDecoder dataDecoder = new DataDecoder("1234".toCharArray());
            byte[] encrypted = dataEncoder.encrypt("Hello!".getBytes());
            byte[] clear = dataDecoder.decrypt(encrypted);
            String theThing = new String(clear);
            theThing.equals("Hello!");*/

//            setEnteredPin("1234");
//            ((AesFileAccess)getFileAccess()).updatePassphrase(this, "1234", "4567");
//
//            String decryptedString = new String(getFileAccess().readData(this, SOMEDATA_TXT), "UTF8");
//
//            if(!TEST_SOME_DATA.equals(decryptedString))
//                throw new RuntimeException("whoops");

//            AesFileAccess aesFileAccess = new AesFileAccess();
//            aesFileAccess.init(this, "1234");
//            aesFileAccess.writeData(this, SOMEDATA_TXT,
//                                    TEST_SOME_DATA.getBytes("UTF8"));
//            String decryptedString = new String(
//                    aesFileAccess.readData(this, SOMEDATA_TXT), "UTF8");


        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    private void copyDbFile()
    {
        try
        {
            File folder = new File(Environment.getExternalStorageDirectory(), "researchstack");
            folder.mkdirs();
            File databasePath = getDatabasePath(DatabaseHelper.DB_NAME);
            File outFile = new File(folder, databasePath.getName());
            FileInputStream input = new FileInputStream(databasePath);
            FileUtils.copy(input, outFile);
            input.close();
        }
        catch(IOException e)
        {
            Log.e("asdf", "", e);
        }
    }

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
        return R.string.app_name;
    }

    public AppDatabase getAppDatabase()
    {
        return DatabaseHelper.getInstance(this);
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

    @Override
    public FileAccess getFileAccess()
    {
        return aesFileAccess;
    }
}
