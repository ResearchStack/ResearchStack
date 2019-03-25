package org.researchstack.backbone.model.navigable;

import android.text.TextUtils;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;


public class NotEmptyNavigationClause extends StepNavigationClauseRule {

    public NotEmptyNavigationClause(String sourceStepIdentifier, AnswerFormat.Type ruleType, RuleClauseOperand operand) {
        super(sourceStepIdentifier, "", ruleType, operand);
    }

    @Override
    protected boolean evalClause(StepResult stepResult) {
        Object stepResultValue = stepResult.getResult();
        return stepResultValue != null && !TextUtils.isEmpty(stepResultValue.toString());
    }
}
