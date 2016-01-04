package co.touchlab.researchstack.glue.common.storage;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import co.touchlab.researchstack.BuildConfig;
import co.touchlab.researchstack.glue.common.storage.file.ClearFileAccess;
import co.touchlab.researchstack.glue.common.storage.file.FileAccessListener;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ClearFileAccessTests
{
    private TestClearFileAccess clearFileAccess;

    @Before
    public void before()
    {
        clearFileAccess = new TestClearFileAccess();
        clearFileAccess.initFileAccess(RuntimeEnvironment.application);
    }

    @Test
    public void readWrite()
    {
        clearFileAccess.writeString(RuntimeEnvironment.application, "/asdf", "Hello!");
        String s = clearFileAccess.readString(RuntimeEnvironment.application, "/asdf");


        Assert.assertEquals(s, "Hello!");
    }

    @Test
    public void initClearFileAccess()
    {
        TestClearFileAccess clearFileAccess = new TestClearFileAccess();
        FileAccessListener listener = new FileAccessListener()
        {

            @Override
            public void dataReady()
            {

            }

            @Override
            public void dataAccessError()
            {

            }
        };

        clearFileAccess.register(listener);
        Assert.assertEquals(clearFileAccess.countListeners(), 1);
        clearFileAccess.unregister(listener);
        Assert.assertEquals(clearFileAccess.countListeners(), 0);
    }

    private static class TestClearFileAccess extends ClearFileAccess
    {
        public TestClearFileAccess()
        {
            checkThreads = false;
        }

        public int countListeners()
        {
            return listeners.size();
        }
    }

}
