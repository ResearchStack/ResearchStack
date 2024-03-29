package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StepNavigationRule implements NavigableOrderedTask.NavigationRule, Serializable {

    private String otherwise;
    private List<StepNavigationClause> clauses = new ArrayList<>();

    public StepNavigationRule(String otherwise) {
        this.otherwise = otherwise;
    }

    public StepNavigationRule(String otherwise, List<StepNavigationClause> clauses) {
        this.otherwise = otherwise;
        this.clauses = clauses;
    }

    public void setClauses(List<StepNavigationClause> clauses) {
        this.clauses = clauses;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        StepNavigationClause clause = null;
        for (StepNavigationClause navigationClause : clauses) {
            if (navigationClause.fulfillsCondition(result)) {
                clause = navigationClause;
                break;
            }
        }
        if (clause != null) {
            return clause.getNextStep();
        } else if (otherwise != null) {
            return otherwise;
        }
        return null;
    }
}
