package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;

/**
 * Created by mauriciosouto on 10/10/17.
 */

public class NotEqualsNavigationClause extends StepNavigationClause {

    public NotEqualsNavigationClause(String sourceStepIdentifier, Object value, AnswerFormat.Type ruleType, RuleClauseOperand operand) {
        super(sourceStepIdentifier, value, ruleType, operand);
    }

    @Override
    protected boolean evalClause(StepResult stepResult) {
        return !stepResult.getResult().equals(value);
    }
}
