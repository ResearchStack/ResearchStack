package co.touchlab.researchstack.glue.task;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.dev.DevUtils;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.model.ConsentSectionModel;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.ConsentDocumentStep;
import co.touchlab.researchstack.core.step.ConsentSharingStep;
import co.touchlab.researchstack.core.step.ConsentSignatureStep;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.core.ui.step.layout.ConsentSignatureStepLayout;
import co.touchlab.researchstack.core.utils.ResUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.glue.step.ConsentQuizEvaluationStep;
import co.touchlab.researchstack.glue.step.ConsentQuizQuestionStep;
import co.touchlab.researchstack.glue.utils.JsonUtils;

public class ConsentTask extends OrderedTask
{
    public static final String SCHEDULE_ID_CONSENT = "consent";

    public static final String ID_CONSENT        = "consent";
    public static final String ID_VISUAL         = "ID_VISUAL";
    public static final String ID_FIRST_QUESTION = "question_1";
    public static final String ID_QUIZ_RESULT    = "ID_QUIZ_RESULT";
    public static final String ID_SHARING        = "ID_SHARING";
    public static final String ID_CONSENT_DOC    = "consent_review_doc";
    public static final String ID_FORM      = "ID_FORM";
    public static final String ID_FORM_NAME      = "ID_FORM_NAME";
    public static final String ID_FORM_DOB      = "ID_FORM_DOB";
    public static final String ID_FORM_BIRTHDATE = "ID_FORM_BIRTHDATE";
    public static final String ID_SIGNATURE      = "ID_SIGNATURE";

    public ConsentTask(Context context)
    {
        super(ID_CONSENT, SCHEDULE_ID_CONSENT);

        ResearchStack researchStack = ResearchStack.getInstance();
        Resources r = context.getResources();

        //TODO Read on main thread for intense UI blockage.
        ConsentSectionModel data = JsonUtils.loadClass(context,
                ConsentSectionModel.class,
                researchStack.getConsentSections());

        String participant = r.getString(R.string.participant);
        ConsentSignature signature = new ConsentSignature("participant", participant, null);

        signature.setRequiresSignatureImage(ResearchStack.getInstance()
                .isSignatureEnabledInConsent());

        ConsentDocument doc = new ConsentDocument();
        doc.setTitle(r.getString(R.string.rsc_consent_form_title));
        doc.setSignaturePageTitle(R.string.rsc_consent_form_title);
        doc.setSignaturePageContent(r.getString(R.string.rsc_consent_signature_content));
        doc.setSections(data.getSections());
        doc.addSignature(signature);

        String htmlDocName = data.getDocumentProperties().getHtmlDocument();
        int id = ResUtils.getRawResourceId(context, htmlDocName);
        doc.setHtmlReviewContent(ResUtils.getStringResource(context, id));

        initVisualSteps(context, doc);

        initConsentSharingStep(r, data);

        initQuizSteps(context, researchStack);

        initConsentReviewSteps(context, doc);
    }

    private void initConsentSharingStep(Resources r, ConsentSectionModel data)
    {
        String investigatorShortDesc = data.getDocumentProperties()
                .getInvestigatorShortDescription();
        if(TextUtils.isEmpty(investigatorShortDesc))
        {
            DevUtils.throwIllegalArgumentException();
        }

        String investigatorLongDesc = data.getDocumentProperties().getInvestigatorLongDescription();
        if(TextUtils.isEmpty(investigatorLongDesc))
        {
            DevUtils.throwIllegalArgumentException();
        }

        String localizedLearnMoreHTMLContent = data.getDocumentProperties().getHtmlContent();
        if(TextUtils.isEmpty(localizedLearnMoreHTMLContent))
        {
            DevUtils.throwIllegalArgumentException();
        }

        ConsentSharingStep sharingStep = new ConsentSharingStep(ID_SHARING);
        sharingStep.setOptional(false);
        sharingStep.setShowsProgress(false);
        sharingStep.setUseSurveyMode(false);
//        sharingStep.setLocalizedLearnMoreHTMLContent(localizedLearnMoreHTMLContent);

        String shareWidely = r.getString(R.string.rsc_consent_share_widely, investigatorLongDesc);
        Choice<String> shareWidelyChoice = new Choice<>(shareWidely, "sponsors_and_partners", null);

        String shareRestricted = r.getString(R.string.rsc_consent_share_only,
                investigatorShortDesc);
        Choice<String> shareRestrictedChoice = new Choice<>(shareRestricted,
                "all_qualified_researchers",
                null);

        sharingStep.setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                shareWidelyChoice,
                shareRestrictedChoice));

        sharingStep.setTitle(r.getString(R.string.rsc_consent_share_title));
        sharingStep.setText(r.getString(R.string.rsc_consent_share_description,
                investigatorLongDesc, localizedLearnMoreHTMLContent));

        addStep(sharingStep);
    }

    private void initVisualSteps(Context ctx, ConsentDocument doc)
    {
        for(int i = 0, size = doc.getSections().size(); i < size; i++)
        {
            ConsentSection section = doc.getSections().get(i);
            ConsentVisualStep step = new ConsentVisualStep("consent_" + i);
            step.setSection(section);

            String nextString = ctx.getString(R.string.rsc_next);
            if(section.getType() == ConsentSection.Type.Overview)
            {
                nextString = ctx.getString(R.string.rsc_get_started);
            }
            else if(i == size - 1)
            {
                nextString = ctx.getString(R.string.rsc_done);
            }
            step.setNextButtonString(nextString);

            addStep(step);
        }
    }

    private void initQuizSteps(Context ctx, ResearchStack rs)
    {
        ConsentQuizModel model = JsonUtils.loadClass(ctx,
                ConsentQuizModel.class,
                rs.getQuizSections());

        String trueString = ctx.getString(R.string.btn_true);
        Choice<Boolean> trueChoice = new Choice<>(trueString, true, null);

        String falseString = ctx.getString(R.string.btn_false);
        Choice<Boolean> falseChoice = new Choice<>(falseString, false, null);

        for(int i = 0; i < model.getQuestions().size(); i++)
        {
            ConsentQuizModel.QuizQuestion question = model.getQuestions().get(i);

            // We need to overwrite the id of the first question to later find it in our internal
            // map later on. This is done to clear and attain the incorrect question count.
            if(i == 0)
            {
                question.id = ID_FIRST_QUESTION;
            }

            ConsentQuizQuestionStep quizStep = new ConsentQuizQuestionStep(question.id,
                    model.getQuestionProperties(),
                    question);
            quizStep.setTitle(question.question);
            quizStep.setText(model.getQuestionProperties().introText);
            quizStep.setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                            new Choice[] {trueChoice, falseChoice}));
            addStep(quizStep);
        }

        ConsentQuizEvaluationStep evaluationStep = new ConsentQuizEvaluationStep(ID_QUIZ_RESULT,
                model.getEvaluationProperties());
        addStep(evaluationStep);
    }

    private void initConsentReviewSteps(Context ctx, ConsentDocument doc)
    {
        // Add ConsentReviewDocumentStep (view html version of the PDF doc)
        StringBuilder docBuilder = new StringBuilder(
                "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
        String title = ctx.getString(R.string.rsc_consent_review_title);
        docBuilder.append(String.format(
                "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>",
                title));
        String detail = ctx.getString(R.string.rsc_consent_review_instruction);
        docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
        docBuilder.append("</div></br>");
        docBuilder.append(doc.getHtmlReviewContent());

        ConsentDocumentStep step = new ConsentDocumentStep(ID_CONSENT_DOC);
        step.setConsentHTML(docBuilder.toString());
        step.setConfirmMessage(ctx.getString(R.string.rsc_consent_review_reason));
        addStep(step);

        // Add full-name input
        if(doc.getSignature(0).isRequiresName())
        {
            String formTitle = ctx.getString(R.string.rsc_consent_form_title);
            FormStep formStep = new FormStep(ID_FORM, formTitle, step.getText());
            formStep.setSceneTitle(R.string.rsc_consent);
            formStep.setUseSurveyMode(false);
            formStep.setOptional(false);

            TextAnswerFormat format = new TextAnswerFormat();
            format.setIsMultipleLines(false);
            // TODO Implement the following -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
            // TODO format.autocapitalizationType = UITextAutocapitalizationTypeWords;
            // TODO format.autocorrectionType = UITextAutocorrectionTypeNo;
            // TODO format.spellCheckingType = UITextSpellCheckingTypeNo;

            String placeholder = ctx.getResources()
                    .getString(R.string.rsc_consent_name_placeholder);
            String nameText = ctx.getResources().getString(R.string.rsc_consent_name_full);
            QuestionStep fullName = new QuestionStep(ID_FORM_NAME, nameText, format);

            DateAnswerFormat dobFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
            String dobText = ctx.getResources().getString(R.string.rsc_consent_dob_full);
            QuestionStep dobStep = new QuestionStep(ID_FORM_DOB, dobText, dobFormat);

            formStep.setFormSteps(fullName, dobStep);
            addStep(formStep);
        }

        // Add signature input
        if(doc.getSignature(0).isRequiresSignatureImage())
        {
            ConsentSignatureStep signatureStep = new ConsentSignatureStep(ID_SIGNATURE);
            signatureStep.setSceneTitle(R.string.rsc_consent);
            signatureStep.setTitle(ctx.getString(R.string.rsc_consent_signature_title));
            signatureStep.setText(ctx.getString(R.string.rsc_consent_signature_instruction));
            signatureStep.setOptional(false);
            signatureStep.setSignatureDateFormat(doc.getSignature(0)
                    .getSignatureDateFormatString());
            signatureStep.setSceneClass(ConsentSignatureStepLayout.class);
            addStep(signatureStep);
        }
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        if(step != null)
        {
            // If we are on a question step, and the next step is an ConsentQuizEvaluationStep,
            // calculate and set the number of incorrect answers on ConsentQuizEvaluationStep.
            if(step instanceof ConsentQuizQuestionStep)
            {
                Step nextStep = super.getStepAfterStep(step, result);

                if(nextStep instanceof ConsentQuizEvaluationStep)
                {
                    ConsentQuizQuestionStep firstQuestion = (ConsentQuizQuestionStep) getStepWithIdentifier(
                            ID_FIRST_QUESTION);
                    int incorrectAnswers = getQuestionIncorrectCount(result, firstQuestion, 0);

                    ConsentQuizEvaluationStep evaluationStep = (ConsentQuizEvaluationStep) nextStep;
                    evaluationStep.setIncorrectCount(incorrectAnswers);

                    return evaluationStep;
                }
            }

            // If this is the ConsentQuizEvaluationStep, we need to check if the user has passed
            // or failed the quiz. If they have have failed, AND it was their first attempt, let the
            // user retake the quiz. If attempts > 1, they must go through the visual consent steps
            // another time.
            else if(step instanceof ConsentQuizEvaluationStep)
            {
                ConsentQuizEvaluationStep evaluationStep = (ConsentQuizEvaluationStep) step;

                if(! evaluationStep.isQuizPassed())
                {
                    // Reset incorrect count on the QuizQuestionSteps.
                    Step firstQuestion = getStepWithIdentifier(ID_FIRST_QUESTION);
                    clearQuestionIncorrectCount(result, firstQuestion);

                    if(evaluationStep.isOverMaxAttempts())
                    {
                        evaluationStep.setAttempt(0);
                        //Return to first visual step
                        return getSteps().get(0);
                    }
                    else
                    {
                        evaluationStep.setAttempt(1);
                        return firstQuestion;
                    }
                }
            }
        }

        return super.getStepAfterStep(step, result);
    }

    /**
     * Recursive method to clear StepResults of type {@link ConsentQuizQuestionStep}
     *
     * @param result the result object where {@link ConsentQuizQuestionStep} are stored
     * @param step   the first ConsentQuizQuestionStep within the task
     */
    private void clearQuestionIncorrectCount(TaskResult result, Step step)
    {
        if(step != null)
        {
            boolean isQuestion = step instanceof ConsentQuizQuestionStep;
            boolean isEvaluation = step instanceof ConsentQuizEvaluationStep;

            if(isQuestion || isEvaluation)
            {
                // Remove the result
                result.setStepResultForStepIdentifier(step.getIdentifier(), null);

                if(isQuestion)
                {
                    // Clear the next step
                    Step next = super.getStepAfterStep(step, result);
                    clearQuestionIncorrectCount(result, next);
                }
            }
        }
    }

    /**
     * Recursive method to get a count of how many incorrect answers there are in total
     *
     * @param result the result object where {@link ConsentQuizQuestionStep} are stored
     * @param step   the first ConsentQuizQuestionStep within the task
     * @param count  the initial count of the how many incorrect answers exist, default to 0
     * @return integer representing how many incorrect answers currently exist
     */
    private int getQuestionIncorrectCount(TaskResult result, Step step, int count)
    {
        if(step != null && step instanceof ConsentQuizQuestionStep)
        {
            StepResult stepResult = result.getStepResult(step.getIdentifier());
            if(stepResult != null)
            {
                boolean correct = (boolean) stepResult.getResult();
                Step next = super.getStepAfterStep(step, result);
                return getQuestionIncorrectCount(result, next, count + (correct ? 0 : 1));
            }
        }

        return count;
    }

}
