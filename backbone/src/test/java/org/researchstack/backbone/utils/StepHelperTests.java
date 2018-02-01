/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.researchstack.backbone.utils;

import org.junit.Test;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by TheMDP on 11/21/17.
 */

public class StepHelperTests {
    @Test
    public void formStepWasSkippedTest_false1() {
        FormStep formStep = createFormStep();
        TaskResult result = createTaskResult(formStep, true, true);
        assertFalse(StepHelper.wasFormStepSkipped(formStep, result));
    }

    @Test
    public void formStepWasSkippedTest_true1() {
        FormStep formStep = createFormStep();
        TaskResult result = createTaskResult(formStep, true, false);
        assertTrue(StepHelper.wasFormStepSkipped(formStep, result));
    }

    @Test
    public void formStepWasSkippedTest_true2() {
        FormStep formStep = createFormStep();
        TaskResult result = createTaskResult(formStep, false, false);
        assertTrue(StepHelper.wasFormStepSkipped(formStep, result));
    }

    @Test
    public void getStepIdentifierTest_SimpleList() {
        List<Step> stepList = new ArrayList<>();
        stepList.add(new Step("a"));
        stepList.add(new Step("b"));
        stepList.add(new Step("c"));

        Step a = StepHelper.getStepWithIdentifier(stepList, "a");
        assertNotNull(a);
        assertEquals("a", a.getIdentifier());

        Step b = StepHelper.getStepWithIdentifier(stepList, "b");
        assertNotNull(b);
        assertEquals("b", b.getIdentifier());

        Step c = StepHelper.getStepWithIdentifier(stepList, "c");
        assertNotNull(c);
        assertEquals("c", c.getIdentifier());
    }

    @Test
    public void getStepIdentifierTest_Subtasks() {
        List<Step> stepList = new ArrayList<>();
        stepList.add(new Step("a"));
        stepList.add(new Step("b"));
        stepList.add(new Step("c"));

        List<Step> subtaskStepList = new ArrayList<>();
        stepList.add(new Step("d"));
        stepList.add(new Step("e"));
        stepList.add(new Step("f"));

        stepList.add(new SubtaskStep("subtask", subtaskStepList));

        Step a = StepHelper.getStepWithIdentifier(stepList, "a");
        assertNotNull(a);
        assertEquals("a", a.getIdentifier());

        Step b = StepHelper.getStepWithIdentifier(stepList, "b");
        assertNotNull(b);
        assertEquals("b", b.getIdentifier());

        Step c = StepHelper.getStepWithIdentifier(stepList, "c");
        assertNotNull(c);
        assertEquals("c", c.getIdentifier());

        Step d = StepHelper.getStepWithIdentifier(stepList, "d");
        assertNotNull(d);
        assertEquals("d", d.getIdentifier());

        Step e = StepHelper.getStepWithIdentifier(stepList, "e");
        assertNotNull(e);
        assertEquals("e", e.getIdentifier());

        Step f = StepHelper.getStepWithIdentifier(stepList, "f");
        assertNotNull(f);
        assertEquals("f", f.getIdentifier());

        Step subtaskStep = StepHelper.getStepWithIdentifier(stepList, "subtask");
        assertNotNull(subtaskStep);
        assertEquals("subtask", subtaskStep.getIdentifier());
    }

    private FormStep createFormStep() {
        List<QuestionStep> questionStepList = new ArrayList<>();
        questionStepList.add(new QuestionStep("q1"));
        questionStepList.add(new QuestionStep("q2"));
        return new FormStep("form_step", null, null, questionStepList);
    }

    private TaskResult createTaskResult(FormStep formStep, boolean hasStepResult, boolean hasQuestionResults) {
        TaskResult result = new TaskResult("task_id");
        StepResult<StepResult> formStepResult = new StepResult<>(formStep);
        if (hasStepResult) {
            if (hasQuestionResults) {
                StepResult<String> q1Result = new StepResult<>(formStep.getFormSteps().get(0));
                q1Result.setResult("q1Answer");
                formStepResult.setResultForIdentifier(q1Result.getIdentifier(), q1Result);

                StepResult<String> q2Result = new StepResult<>(formStep.getFormSteps().get(1));
                q2Result.setResult("q2Answer");
                formStepResult.setResultForIdentifier(q2Result.getIdentifier(), q2Result);
            }
            result.setStepResultForStepIdentifier(formStep.getIdentifier(), formStepResult);
        }
        return result;
    }
}
