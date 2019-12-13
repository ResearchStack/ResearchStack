package org.researchstack.backbone.result;

import org.junit.Test;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.Map;

import androidx.core.util.Pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TaskResultTest {

    private final String stepIdentifier = "step-1234";

    @Test
    public void allConstructors_initializeCorrectValues() {

        final String taskIdentifier = "task-1234";
        final TaskResult subject1 = new TaskResult(taskIdentifier);
        final TaskResult subject2 = new TaskResult(subject1);

        assertEquals(taskIdentifier, subject1.getIdentifier());
        assertEquals(taskIdentifier, subject2.getIdentifier());
        assertNotNull(subject1.getStepsAndResults());
        assertNotNull(subject2.getStepsAndResults());
        assertNotSame(subject1, subject2);
    }

    @Test
    public void setStepResultForStep_setsCorrectValues() {
        // Assemble
        final TaskResult subject = makeTaskResult();
        final Step step = makeQuestionStep(stepIdentifier, makeIntegerAnswerFormat());
        final StepResult expectedStepResult = new StepResult(step);

        // Act
        subject.setStepResultForStep(step, expectedStepResult);

        // Assert
        final StepResult actualResult = subject.getStepResult(stepIdentifier);
        assertEquals(expectedStepResult, actualResult);
    }

    @Test
    public void getStepAndResult_returnsCorrectValues() {
        // Assemble
        final TaskResult subject = makeTaskResult();
        final Step step = makeQuestionStep(stepIdentifier, makeIntegerAnswerFormat());
        final StepResult expectedStepResult = new StepResult(step);
        subject.setStepResultForStep(step, expectedStepResult);

        // Act
        final Pair<Step, StepResult> stepAndResult = subject.getStepAndResult(stepIdentifier);

        // assert
        assertEquals(step, stepAndResult.first); //step
        assertEquals(expectedStepResult, stepAndResult.second); //stepResult
    }

    @Test
    public void getStepAndResult_whenStepIsUnknown_returnsNullStepAndResponse() {
        // Assemble
        final TaskResult subject = makeTaskResult();

        // Act
        final Pair<Step, StepResult> stepAndResult = subject.getStepAndResult(stepIdentifier);

        // assert
        assertNull(stepAndResult.first); //step
        assertNull(stepAndResult.second); //stepResult
    }

    @Test
    public void removeStepResultForStep_correctlyRemoves() {
        // Assemble
        final TaskResult subject = makeTaskResult();
        final Step step = makeQuestionStep(stepIdentifier, makeIntegerAnswerFormat());
        final StepResult expectedStepResult = new StepResult(step);
        subject.setStepResultForStep(step, expectedStepResult);

        //Act
        subject.removeStepResultForStep(step);

        // Assert
        final StepResult actualResult = subject.getStepResult(stepIdentifier);
        assertNull(actualResult);
    }

    @Test
    public void getStepAndResults_containsExpectedValues() {
        // Assemble
        final TaskResult subject = makeTaskResult();
        final Step step1 = makeQuestionStep(stepIdentifier + "1", makeIntegerAnswerFormat());
        final Step step2 = makeQuestionStep(stepIdentifier + "2", makeBooleanAnswerFormat());

        final StepResult step1Result = new StepResult(step1);
        final StepResult step2Result = new StepResult(step2);

        // Act
        subject.setStepResultForStep(step1, step1Result);
        subject.setStepResultForStep(step2, step2Result);
        final Map<Step, StepResult> stepsAndResults = subject.getStepsAndResults();

        // Assert
        assertTrue(stepsAndResults.containsKey(step1));
        assertTrue(stepsAndResults.containsKey(step2));
        assertTrue(stepsAndResults.containsValue(step1Result));
        assertTrue(stepsAndResults.containsValue(step2Result));
    }

    @Test
    public void cloneTaskResult_correctlyClonesObject() throws CloneNotSupportedException {
        // Assemble
        final TaskResult subject = makeTaskResult();
        final Step originalStep = makeQuestionStep(stepIdentifier, makeIntegerAnswerFormat());
        subject.setStepResultForStep(originalStep, new StepResult(originalStep));

        // Act
        final TaskResult clonedSubject = (TaskResult) subject.clone();

        // Assert
        final Map<Step, StepResult> originalResults = subject.getStepsAndResults();
        final Map<Step, StepResult> clonedResults = clonedSubject.getStepsAndResults();
        final StepResult originalStepResult = originalResults.get(originalStep);
        final Pair<Step, StepResult> clonedStepAndResult = clonedSubject.getStepAndResult(stepIdentifier);

        assertNotSame(subject, clonedSubject);
        assertEquals(subject, clonedSubject); //only truly compares identifiers
        assertEquals(originalResults, clonedResults);
        assertEquals(originalStep, clonedStepAndResult.first);
        assertEquals(originalStepResult, clonedStepAndResult.second);
    }

    private TaskResult makeTaskResult() {
        return new TaskResult(stepIdentifier);
    }

    private AnswerFormat makeIntegerAnswerFormat() {
        return new IntegerAnswerFormat(0, 10);
    }

    private AnswerFormat makeBooleanAnswerFormat() {
        return new BooleanAnswerFormat("true", "false");
    }

    private Step makeQuestionStep(String identifier, AnswerFormat answerFormat) {
        return new QuestionStep(identifier, "irrelevantTitle", answerFormat);
    }
}