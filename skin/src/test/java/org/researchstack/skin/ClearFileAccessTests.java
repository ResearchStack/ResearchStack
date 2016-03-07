package org.researchstack.skin;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ClearFileAccessTests
{
    //    private TestClearFileAccess clearFileAccess;
    //
    //    @Before
    //    public void before()
    //    {
    //        clearFileAccess = new TestClearFileAccess();
    //        clearFileAccess.initFileAccess(RuntimeEnvironment.application);
    //    }
    //
    //    @Test
    //    public void readWrite()
    //    {
    //        clearFileAccess.writeString(RuntimeEnvironment.application, "/asdf", "Hello!");
    //        String s = clearFileAccess.readString(RuntimeEnvironment.application, "/asdf");
    //
    //
    //        Assert.assertEquals(s, "Hello!");
    //    }
    //
    //    @Test
    //    public void initClearFileAccess()
    //    {
    //        TestClearFileAccess clearFileAccess = new TestClearFileAccess();
    //        FileAccessListener listener = new FileAccessListener()
    //        {
    //
    //            @Override
    //            public void dataReady()
    //            {
    //
    //            }
    //
    //            @Override
    //            public void dataAccessError()
    //            {
    //
    //            }
    //        };
    //
    //        clearFileAccess.register(listener);
    //        Assert.assertEquals(clearFileAccess.countListeners(), 1);
    //        clearFileAccess.unregister(listener);
    //        Assert.assertEquals(clearFileAccess.countListeners(), 0);
    //    }
    //
    //    private static class TestClearFileAccess extends ClearFileAccess
    //    {
    //        public TestClearFileAccess()
    //        {
    //            checkThreads = false;
    //        }
    //
    //        public int countListeners()
    //        {
    //            return listeners.size();
    //        }
    //    }

}
