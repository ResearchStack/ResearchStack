package org.researchstack.backbone.ui;


import android.content.Intent;
import android.support.annotation.Nullable;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BirthDateAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout.KEY_SIGNATURE;
import static org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout.KEY_SIGNATURE_DATE;

public class ConsentViewTaskActiviy extends ViewTaskActivity
{

    private static final String ID_FORM_NAME = "formName";
    private static final String ID_FORM_DOB = "formDob";
    private static final String ID_FORM = "form";

    String consentHtml;
    String firstName;
    String lastName;
    String signatureBase64;
    String signDate;


    protected void onExecuteStepAction(int action) {
        if (action == StepCallbacks.ACTION_END)
        {
            if((getCurrentStep() instanceof ConsentSignatureStep))
            {

            }
        }
        super.onExecuteStepAction(action);
    }

    public void onSaveStep(int action, Step step, StepResult result) {

        if(step instanceof FormStep)
        {

        }
        if(step instanceof ConsentSignatureStep)
        {
            //result.setResultForIdentifier(, getBase64EncodedImage());
            //result.setResultForIdentifier(KEY_SIGNATURE_DATE, formattedSignDate);

            signatureBase64 = (String) result.getResultForIdentifier(KEY_SIGNATURE);
            signDate = (String) result.getResultForIdentifier(KEY_SIGNATURE_DATE);

        }
        else if(step instanceof ConsentDocumentStep)
        {
            consentHtml = ((ConsentDocumentStep) step).getConsentHTML();
        }

        super.onSaveStep(action, step, result);
    }

    public static @Nullable FormStep getConsentPersonalInfoFormStep(boolean requiresName, boolean requiresBirthDate, @Nullable AnswerFormat.QuestionType questionType)
    {
        if (requiresName || requiresBirthDate)
        {
            List<QuestionStep> formSteps = new ArrayList<>();
            if (requiresName)
            {
                TextAnswerFormat format;
                if(questionType != null)
                {
                    format = new CustomQuestionTypeAnswerFormat(questionType);
                }
                else
                {
                    format = new TextAnswerFormat();
                }

                format.setIsMultipleLines(false);
                formSteps.add(new QuestionStep(ID_FORM_NAME, "Full Name", format));
            }

            if (requiresBirthDate)
            {
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.YEAR, -18);
                DateAnswerFormat dobFormat = new BirthDateAnswerFormat(null, 18, 0);
                String dobText = "Date of birth";
                formSteps.add(new QuestionStep(ID_FORM_DOB, dobText, dobFormat));
            }

            String formTitle = "Consent";
            FormStep formStep = new FormStep(ID_FORM, formTitle, "");
            formStep.setOptional(false);
            formStep.setFormSteps(formSteps);

            return formStep;
        }

        return null;
    }

    private static class CustomQuestionTypeAnswerFormat extends TextAnswerFormat
    {
        QuestionType questionType;

        CustomQuestionTypeAnswerFormat(QuestionType questionType)
        {
            this.questionType = questionType;
        }

        @Override
        public QuestionType getQuestionType() {
            return questionType;
        }
    }

}
