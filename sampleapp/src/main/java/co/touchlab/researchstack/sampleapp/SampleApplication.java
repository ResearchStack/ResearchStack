package co.touchlab.researchstack.sampleapp;

import junit.framework.Assert;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.Constants;
import co.touchlab.researchstack.common.secure.SecurityProfile;
import co.touchlab.researchstack.common.secure.aes.AesFileAccess;
import co.touchlab.researchstack.common.secure.aes.DataDecoder;
import co.touchlab.researchstack.common.secure.aes.DataEncoder;

/**
 * Created by bradleymcdermott on 11/12/15.
 */
public class SampleApplication extends ResearchStackApplication
{
    public static final String TEST_SOME_DATA = "Test some data";
    public static final String SOMEDATA_TXT = "somedata.txt";

    @Override
    public void onCreate()
    {
        super.onCreate();
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
        return co.touchlab.researchstack.R.string.app_name;
    }

    @Override
    public SecurityProfile getSecurityProfile()
    {
        return null;
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
