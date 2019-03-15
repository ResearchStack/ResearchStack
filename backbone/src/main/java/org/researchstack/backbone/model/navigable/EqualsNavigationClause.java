package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;

public class EqualsNavigationClause extends StepNavigationClauseRule {

    public EqualsNavigationClause(String sourceStepIdentifier, Object value, AnswerFormat.Type ruleType, RuleClauseOperand operand) {
        super(sourceStepIdentifier, value, ruleType, operand);
    }

    @Override
    protected boolean evalClause(StepResult stepResult) {
        Integer compareResult = compareResult(stepResult);
        if (compareResult != null) {
            return compareResult.equals(0);
        }
        return false;
    }
}
