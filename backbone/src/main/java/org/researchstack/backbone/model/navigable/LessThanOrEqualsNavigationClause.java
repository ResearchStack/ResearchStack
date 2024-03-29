package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;

public class LessThanOrEqualsNavigationClause extends StepNavigationClauseRule {

    public LessThanOrEqualsNavigationClause(String sourceStepIdentifier, Object value, AnswerFormat.Type ruleType, RuleClauseOperand operand) {
        super(sourceStepIdentifier, value, ruleType, operand);
    }

    @Override
    protected boolean evalClause(StepResult stepResult) {
        if (stepResult.getResult() != null) {
            Integer compareResult = compareResult(stepResult);
            if (compareResult != null) {
                return compareResult <= 0;
            }
        }
        return false;
    }
}
