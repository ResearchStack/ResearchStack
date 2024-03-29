package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.result.TaskResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StepNavigationClause implements Serializable {

    protected String nextStep;
    protected List<StepNavigationClauseRule> rules;

    public StepNavigationClause(String nextStep, List<StepNavigationClauseRule> rules) {
        this.nextStep = nextStep;
        this.rules = new ArrayList<>(rules);
    }

    public StepNavigationClause(String nextStep) {
        this.nextStep = nextStep;
        this.rules = new ArrayList<>();
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public List<StepNavigationClauseRule> getRules() {
        return rules;
    }

    public void setRules(List<StepNavigationClauseRule> rules) {
        this.rules = rules;
    }

    public boolean fulfillsCondition(TaskResult result) {
        boolean evalResult = false;
        for (StepNavigationClauseRule rule : rules) {
            switch (rule.getOperand()) {
                case NONE:
                    evalResult = rule.eval(result);
                    break;
                case OR:
                    evalResult = evalResult || rule.eval(result);
                    break;
                case AND:
                    evalResult = evalResult && rule.eval(result);
                    break;
            }
        }
        return evalResult;
    }
}
