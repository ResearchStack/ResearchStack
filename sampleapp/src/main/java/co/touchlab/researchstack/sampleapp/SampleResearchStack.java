package co.touchlab.researchstack.sampleapp;

import android.content.Context;
import android.os.Environment;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.aes.AesFileAccess;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.ui.step.body.SingleChoiceQuestionBody;
import co.touchlab.researchstack.glue.AppPrefs;
import co.touchlab.researchstack.glue.NavigationItem;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.User;

/**
 * Created by bradleymcdermott on 11/12/15.
 */
public class SampleResearchStack extends ResearchStack
{
    public static final String TEST_SOME_DATA = "Test some data";
    public static final String SOMEDATA_TXT   = "somedata.txt";

    public SampleResearchStack(Context context)
    {
        super(context);
        //        try
        //        {
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
        //
        //            AesFileAccess aesFileAccess = new AesFileAccess();
        //            aesFileAccess.init(this, "1234");
        //            aesFileAccess.writeData(this, SOMEDATA_TXT,
        //                                    TEST_SOME_DATA.getBytes("UTF8"));
        //            String decryptedString = new String(
        //                    aesFileAccess.readData(this, SOMEDATA_TXT), "UTF8");
        //
        //
        //        }
        //        catch(Exception e)
        //        {
        //            throw new RuntimeException(e);
        //        }

    }

    private void copyDbFile()
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
            LogExt.e(getClass(), e);
        }
    }

    //TODO Replace method with something that exists
    public void copy(InputStream inputStream, File output) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(output);
            int read = 0;
            byte[] bytes = new byte[1024];
            while((read = inputStream.read(bytes)) != - 1)
            {
                outputStream.write(bytes, 0, read);
            }
        }
        finally
        {
            try
            {
                if(inputStream != null)
                {
                    inputStream.close();
                }
            }
            finally
            {
                if(outputStream != null)
                {
                    outputStream.close();
                }
            }
        }
    }

    @Override
    public List<NavigationItem> getNavigationItems()
    {
        List<NavigationItem> navItems = super.getNavigationItems();

        // Add our custom fragment
        navItems.add(new NavigationItem().setId(R.id.nav_custom)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.custom)
                .setIcon(R.drawable.ic_nav_custom)
                .setClass(SampleCustomFragment.class));

        return navItems;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Concrete implementations
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    @Override
    protected AppDatabase createAppDatabaseImplementation()
    {
        return DatabaseHelper.getInstance(context);
    }

    @Override
    protected FileAccess createFileAccessImplementation()
    {
        long autoLockTime = AppPrefs.getInstance(context).getAutoLockTime();
        return new AesFileAccess(new PinCodeConfig(autoLockTime));
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
    public int getConsentPDF()
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
    public User.UserInfoType[] getUserInfoTypes()
    {
        return new User.UserInfoType[] {
                User.UserInfoType.Name,
                User.UserInfoType.Email,
                User.UserInfoType.BiologicalSex,
                User.UserInfoType.DateOfBirth,
                User.UserInfoType.Height,
                User.UserInfoType.Weight
        };
    }

    @Override
    public Class getInclusionCriteriaSceneClass()
    {
        return SingleChoiceQuestionBody.class;
    }
}
