package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mauriciosouto on 11/10/17.
 */

public class StepNavigationRule implements NavigableOrderedTask.NavigationRule, Serializable {

    private String nextStep;
    private String otherwise;
    private List<StepNavigationClause> clauses = new ArrayList<>();

    public StepNavigationRule(String nextStep, String otherwise) {
        this.nextStep = nextStep;
        this.otherwise = otherwise;
    }

    public StepNavigationRule(String nextStep, String otherwise, List<StepNavigationClause> clauses) {
        this.nextStep = nextStep;
        this.otherwise = otherwise;
        this.clauses = clauses;
    }

    public void setClauses(List<StepNavigationClause> clauses) {
        this.clauses = clauses;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        boolean isRuleClausesFulfilled = false;
        for (StepNavigationClause clause : clauses) {
            if (clause.fulfillsCondition(result)) {
                isRuleClausesFulfilled = true;
                break;
            }
        }
        if (isRuleClausesFulfilled) {
            return this.nextStep;
        } else if (otherwise != null) {
            return otherwise;
        }
        return null;
    }
}
