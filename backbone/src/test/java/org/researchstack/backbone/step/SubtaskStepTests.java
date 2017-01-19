package org.researchstack.backbone.step;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.ResourceParserHelper;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.model.survey.factory.SurveyFactoryHelper;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.ObjectUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by TheMDP on 1/6/17.
 */

public class SubtaskStepTests {

    SurveyFactoryHelper  helper;
    ResourceParserHelper resourceHelper;

    @Before
    public void setUp() throws Exception
    {
        helper = new SurveyFactoryHelper();
        resourceHelper = new ResourceParserHelper();
    }

    @Test
    public void testMutatedResultSet() throws Exception
    {
        SubtaskStepAndSteps subtaskStepAndSteps = createSubtaskStep();
        Step firstStep = (Step)ObjectUtils.clone(subtaskStepAndSteps.steps.get(0));
        Step lastStep = (Step)ObjectUtils.clone(subtaskStepAndSteps.steps.get(subtaskStepAndSteps.steps.size()-1));
        List<Step> navSteps = new ArrayList<>();
        navSteps.add(firstStep);
        navSteps.add(subtaskStepAndSteps.subtaskStep);
        navSteps.add(lastStep);
        NavigableOrderedTask navTask = new NavigableOrderedTask("Parent Task", navSteps);

        TaskResult taskResult = new TaskResult("Parent Task");

        Step step1 = navTask.getStepAfterStep(null, taskResult);
        assertNotNull(step1);
        assertEquals(step1.getIdentifier(), "intruction");
        Map<String, StepResult> resultMap = new LinkedHashMap<>();
        resultMap.put(step1.getIdentifier(), new StepResult(step1));
        taskResult.setResults(resultMap);

        Step step2 = navTask.getStepAfterStep(step1, taskResult);
        assertNotNull(step2);
        assertEquals(step2.getIdentifier(), "Mutating Task.intruction");
        taskResult.getResults().put(step2.getIdentifier(), new StepResult(step2));

        Step step3 = navTask.getStepAfterStep(step2, taskResult);
        assertNotNull(step3);
        assertEquals(step3.getIdentifier(), "Mutating Task.question1");
        assertTrue(step3 instanceof QuestionStep);
        StepResult<String> step3Result = new StepResult(step3);
        step3Result.getResults().put("answer", "a");
        taskResult.getResults().put(step3.getIdentifier(), step3Result);

        Step step4 = navTask.getStepAfterStep(step3, taskResult);
        assertNotNull(step4);
        assertEquals(step4.getIdentifier(), "Mutating Task.question2");

        // Check that mutated task result is returned
        StepResult stepResult = taskResult.getStepResult("Mutating Task.question1");
        assertNotNull(stepResult);
        assertEquals(stepResult.getResults().size(), 2);
    }

    // Helper methods for test class

    class SubtaskStepAndSteps {
        SubtaskStep subtaskStep;
        List<Step> steps;
        SubtaskStepAndSteps(SubtaskStep subtaskStep, List<Step> steps) {
            this.subtaskStep = subtaskStep;
            this.steps = steps;
        }
    }

    SubtaskStepAndSteps createSubtaskStep() {
        Type listType = new TypeToken<List<SurveyItem>>() {
        }.getType();
        String subtaskJson = resourceHelper.getJsonStringForResourceName("subtask");
        List<SurveyItem> surveyItemList = helper.gson.fromJson(subtaskJson, listType);

        SurveyFactory factory = new SurveyFactory(helper.mockContext, surveyItemList);
        MutatedResultTask mutatedResultTask = new MutatedResultTask("Mutating Task", factory.getSteps());
        SubtaskStep subtaskStep = new SubtaskStep(mutatedResultTask);
        return new SubtaskStepAndSteps(subtaskStep, factory.getSteps());
    }

    class MutatedResultTask extends OrderedTask {

        public MutatedResultTask(String identifier, List<Step> steps) {
            super(identifier, steps);
        }

        public MutatedResultTask(String identifier, Step... steps) {
            super(identifier, steps);
        }

        @Override
        public Step getStepAfterStep(Step step, TaskResult result) {
            if (step instanceof QuestionStep) {
                QuestionStep previousStep = (QuestionStep)step;
                StepResult stepResult = result.getStepResult(previousStep.getIdentifier());
                if (stepResult != null && stepResult.getResults() != null) {
                    String addedId = previousStep.getIdentifier() + "addedResult";
                    Step addedStepForResult = step.deepCopy(addedId);
                    StepResult addResult = new StepResult(addedStepForResult);
                    stepResult.getResults().put(addResult.getIdentifier(), addResult);
                }
            }

            Step nextStep = super.getStepAfterStep(step, result);
            return nextStep;
        }
    }
}
