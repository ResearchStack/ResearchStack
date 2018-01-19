package org.researchstack.backbone.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.researchstack.backbone.answerformat.BirthDateAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.utils.RSHTMLPDFWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout.KEY_SIGNATURE;
import static org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout.KEY_SIGNATURE_DATE;

public class ConsentViewTaskActivity extends ViewTaskActivity implements StepCallbacks
{
    private static final String ID_FORM_FIRST_NAME = "user_info_form_first_name";
    private static final String ID_FORM_LAST_NAME = "user_info_form_last_name";
    private static final String ID_FORM_DOB = "user_info_form_dob";
    private static final String ID_FORM = "user_info_form";

    String consentHtml;
    String firstName;
    String lastName;
    String dateOfBirth;
    String signatureBase64;
    String signDate;

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ConsentViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result) {

        if(step instanceof FormStep && step.getIdentifier().equalsIgnoreCase(ID_FORM))
        {
            for (QuestionStep question : ((FormStep) step).getFormSteps())
            {
                if(question.getIdentifier().equalsIgnoreCase(ID_FORM_FIRST_NAME))
                {
                    StepResult nameResult = (StepResult) result.getResultForIdentifier(ID_FORM_FIRST_NAME);
                    firstName = (String)nameResult.getResult();
                }
                if(question.getIdentifier().equalsIgnoreCase(ID_FORM_LAST_NAME))
                {
                    StepResult nameResult = (StepResult) result.getResultForIdentifier(ID_FORM_LAST_NAME);
                    lastName = (String)nameResult.getResult();
                }
                if(question.getIdentifier().equalsIgnoreCase(ID_FORM_DOB))
                {
                    StepResult dobResult = (StepResult) result.getResultForIdentifier(ID_FORM_DOB);
                    dateOfBirth = (String)dobResult.getResult();
                }
            }
        }
        if(step instanceof ConsentSignatureStep)
        {
            signatureBase64 = (String) result.getResultForIdentifier(KEY_SIGNATURE);
            signDate = (String) result.getResultForIdentifier(KEY_SIGNATURE_DATE);
        }
        else if(step instanceof ConsentDocumentStep)
        {
            consentHtml = ((ConsentDocumentStep) step).getConsentHTML();
        }

        if(consentHtml != null && signatureBase64 != null)
        {
            PDFWriteExposer writter = new PDFWriteExposer();
            writter.printPdfFile(this, getCurrentTaskId(), consentHtml);
        }

        super.onSaveStep(action, step, result);
    }



    public static @Nullable FormStep getConsentPersonalInfoFormStep(boolean requiresName, boolean requiresBirthDate)
    {
        if (requiresName || requiresBirthDate)
        {
            List<QuestionStep> formSteps = new ArrayList<>();
            if (requiresName)
            {
                formSteps.add(new QuestionStep(ID_FORM_FIRST_NAME, "First Name", new TextAnswerFormat()));
                formSteps.add(new QuestionStep(ID_FORM_LAST_NAME, "Last Name", new TextAnswerFormat()));
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

    class PDFWriteExposer extends RSHTMLPDFWriter
    {
        protected void printPdfFile(Activity context, final String taskId, String htmlConsentDocument)
        {
            super.printPdfFile(context, taskId, htmlConsentDocument);
        }
    }
}
