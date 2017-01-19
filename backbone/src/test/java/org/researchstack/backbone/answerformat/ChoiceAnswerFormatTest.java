package org.researchstack.backbone.answerformat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.researchstack.backbone.model.Choice;

import static junit.framework.TestCase.assertEquals;


public class ChoiceAnswerFormatTest {

    Choice<String> c1 = new Choice<>("choice 01", "c1");
    Choice<String> c2 = new Choice<>("choice 02", "c2");
    Choice<String> c3 = new Choice<>("choice 03", "c3");
    Choice<String> c4 = new Choice<>("choice 04", "c4");
    private ChoiceAnswerFormat format;

    @Before
    public void setUp() throws Exception {
        format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                c1,
                c2,
                c3,
                c4);
    }

    @Test
    public void testCount() throws Exception {
        assertEquals("Correctly returns the count of the choices", 4, format.getChoices().length);

        format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, c1, c2, c3);

        assertEquals("Correctly returns the count of the choices", 3, format.getChoices().length);

    }

    @Test
    public void testTextChoice() throws Exception {
        assertEquals("Maintains the correct order of the choices", c1, format.getChoices()[0]);
        assertEquals("Maintains the correct order of the choices", c3, format.getChoices()[2]);
    }

    @Ignore
    @Test
    public void testImageChoice() throws Exception {
        // Image choices not implemented
    }

    @Ignore
    @Test
    public void testAnswerForSelectedIndexes() throws Exception {
        // we don't have the ChoiceFormatHelper class in RS, probably don't need this test
    }

    @Ignore
    @Test
    public void testSelectedIndexesForAnswer() throws Exception {
        // we don't have the ChoiceFormatHelper class in RS, probably don't need this test
    }
}