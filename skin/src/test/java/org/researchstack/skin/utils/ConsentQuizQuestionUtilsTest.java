package org.researchstack.skin.utils;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentQuestionType;
import org.researchstack.backbone.model.ConsentQuizModel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by TheMDP on 12/15/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class ConsentQuizQuestionUtilsTest {

    Context mockContext;
    ConsentQuizModel.QuizQuestion mockQuestion;

    @Before
    public void setUp() throws Exception {
        mockContext = Mockito.mock(Context.class);
        Mockito.when(mockContext.getString(org.researchstack.skin.R.string.rss_btn_true)).thenReturn("True");
        Mockito.when(mockContext.getString(org.researchstack.skin.R.string.rss_btn_false)).thenReturn("False");
    }

    void resetQuestion(ConsentQuestionType type) {
        mockQuestion = Mockito.mock(ConsentQuizModel.QuizQuestion.class);
        Mockito.when(mockQuestion.getType()).thenReturn(type);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBooleanChoiceCreation() {
        resetQuestion(ConsentQuestionType.BOOLEAN);

        List<Choice> expectedChoices = new ArrayList<>();
        expectedChoices.add(new Choice("True", "true"));
        expectedChoices.add(new Choice("False", "false"));

        List<Choice> actualChoices = ConsentQuizQuestionUtils.createChoices(mockContext, mockQuestion);
        assertChoiceListEquals(expectedChoices, actualChoices);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleChoiceTextTextChoicesCreation() {
        resetQuestion(ConsentQuestionType.SINGLE_CHOICE_TEXT);
        List<String> textChoices = new ArrayList<>();
        textChoices.add("A");
        textChoices.add("B");
        Mockito.when(mockQuestion.getTextChoices()).thenReturn(textChoices);

        List<Choice> expectedChoices = new ArrayList<>();
        expectedChoices.add(new Choice("A", "0"));
        expectedChoices.add(new Choice("B", "1"));

        List<Choice> actualChoices = ConsentQuizQuestionUtils.createChoices(mockContext, mockQuestion);
        assertChoiceListEquals(expectedChoices, actualChoices);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleChoiceTextItemsChoiceCreation() {
        resetQuestion(ConsentQuestionType.SINGLE_CHOICE_TEXT);
        List<Choice> expectedChoices = new ArrayList<>();
        expectedChoices.add(new Choice("A", true));
        expectedChoices.add(new Choice("B", false));
        Mockito.when(mockQuestion.getItems()).thenReturn(expectedChoices);

        List<Choice> actualChoices = ConsentQuizQuestionUtils.createChoices(mockContext, mockQuestion);
        assertChoiceListEquals(expectedChoices, actualChoices);
    }

    void assertChoiceListEquals(List<Choice> expected, List<Choice> actual) {
        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            Choice expectedChoice = expected.get(i);
            Choice actualChoice = actual.get(i);
            assertEquals(expectedChoice.getText(), actualChoice.getText());
            assertEquals(expectedChoice.getValue(), actualChoice.getValue());
            assertEquals(expectedChoice.getDetailText(), actualChoice.getDetailText());
        }
    }
}
