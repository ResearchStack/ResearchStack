package org.researchstack.backbone.model.navigable;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;

public class EmptyNavigationClause extends StepNavigationClauseRule {

    public EmptyNavigationClause(String sourceStepIdentifier, Object value, AnswerFormat.Type ruleType, RuleClauseOperand operand) {
        super(sourceStepIdentifier, value, ruleType, operand);
    }

    @Override
    protected boolean evalClause(StepResult stepResult) {
        return stepResult.getResult().toString().equals("");
    }
}
