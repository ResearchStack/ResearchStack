package co.touchlab.researchstack;

import android.os.Build;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.UnsupportedEncodingException;

import co.touchlab.researchstack.common.secure.aes.AesFileAccess;

/**
 * Created by kgalligan on 11/24/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class TestEnc
{

    public static final String TEST_SOME_DATA = "Test some data";
    public static final String SOMEDATA_TXT = "somedata.txt";

    @Before
    public void setup()
    {


    }

    @Test
    public void testAesEncryption() throws UnsupportedEncodingException
    {
        AesFileAccess aesFileAccess = new AesFileAccess();
        aesFileAccess.init(RuntimeEnvironment.application, "1234");
        aesFileAccess.writeData(RuntimeEnvironment.application, SOMEDATA_TXT,
                                TEST_SOME_DATA.getBytes("UTF8"));
        String decryptedString = new String(
                aesFileAccess.readData(RuntimeEnvironment.application, SOMEDATA_TXT), "UTF8");

        Assert.assertEquals(TEST_SOME_DATA, decryptedString);
    }
}
