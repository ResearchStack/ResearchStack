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

    @Test
    public void testTextMinMaxRegexAnswerFormat()
    {
        TextAnswerFormat format = new TextAnswerFormat();
        format.setValidationRegex("^\\w{4,16}$");
        assertFalse(format.isAnswerValid("Abc"));               // less than 3 characters invalid
        assertFalse(format.isAnswerValid("Abcd1234Abcd1234A")); // more than 17 characters
        assertTrue(format.isAnswerValid("Abcd1234Abcd1234"));   // 16 characters
        assertTrue(format.isAnswerValid("Abcd"));               // 4 characters is valid up to
    }

    @Test
    public void testPasswordAnswerFormat()
    {
        PasswordAnswerFormat format = new PasswordAnswerFormat();
        assertTrue(format.isAnswerValid("Abcd1234"));           // normal password valid
        assertTrue(format.isAnswerValid("Abcd"));               // 4 characters is valid up to
        assertTrue(format.isAnswerValid("Abcd1234Abcd1234"));   // 16 characters
    }

    @Test
    public void testInvalidTextRegexAnswerFormat()
    {
        PasswordAnswerFormat format = new PasswordAnswerFormat();
        assertFalse(format.isAnswerValid("Ãbcd1234"));          // non-asciii character, Ã, not allowed
        assertFalse(format.isAnswerValid("Abc"));               // less than 3 characters invalid
        assertFalse(format.isAnswerValid("Abcd1234Abcd1234A")); // more than 17 characters
    }
}