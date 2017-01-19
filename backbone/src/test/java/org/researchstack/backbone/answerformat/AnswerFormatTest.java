package org.researchstack.backbone.answerformat;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class AnswerFormatTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testTestValidEmailAnswerFormat() throws Exception {
        EmailAnswerFormat format = new EmailAnswerFormat();
        // Test email regex validation with correct input.
        assertTrue(format.isAnswerValid("someone@researchkit.org"));
        assertTrue(format.isAnswerValid("some.one@researchkit.org"));
        assertTrue(format.isAnswerValid("someone@researchkit.org.uk"));
        assertTrue(format.isAnswerValid("some_one@researchkit.org"));
        assertTrue(format.isAnswerValid("some-one@researchkit.org"));
        assertTrue(format.isAnswerValid("someone1@researchkit.org"));
    }

    @Test
    public void testTestInvalidEmailAnswerFormat() throws Exception {
        EmailAnswerFormat format = new EmailAnswerFormat();
        // Test email regex validation with incorrect input.
        assertFalse(format.isAnswerValid("emailtest"));
        assertFalse(format.isAnswerValid("emailtest@"));
        assertFalse(format.isAnswerValid("emailtest@researchkit"));
        assertFalse(format.isAnswerValid("emailtest@.org"));
        assertFalse(format.isAnswerValid("12345"));
    }
}