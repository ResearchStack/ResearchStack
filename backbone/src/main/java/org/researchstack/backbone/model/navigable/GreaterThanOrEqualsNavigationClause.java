package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;

/**
 * Created by mauriciosouto on 10/10/17.
 */

public class GreaterThanOrEqualsNavigationClause extends StepNavigationClause {

    public GreaterThanOrEqualsNavigationClause(String sourceStepIdentifier, Object value, AnswerFormat.Type ruleType, RuleClauseOperand operand) {
        super(sourceStepIdentifier, value, ruleType, operand);
    }

    @Override
    protected boolean evalClause(StepResult stepResult) {
        Integer compareResult = compareResult(stepResult);
        if (compareResult != null) {
            return compareResult >= 0;
        }
        return false;
    }
}
