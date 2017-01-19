package org.researchstack.skin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DataResponseTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testDataResponseConstructor() throws Exception {
        DataResponse dr = new DataResponse(true, "A test");
        String message = dr.getMessage();
        assertEquals("DataResponse message not set.", "A test", message);

        boolean success = dr.isSuccess();
        assertTrue("DataResponse success value incorrect.", success);
    }

    @Test
    public void testDataResponseSetters() throws Exception {
        DataResponse dr = new DataResponse();

        dr.setMessage("Another test");
        dr.setSuccess(true);

        String message = dr.getMessage();
        boolean success = dr.isSuccess();

        assertEquals("DataResponse message not set.", "Another test", message);
        assertTrue("DataResponse success value not set.", success);
    }
}
