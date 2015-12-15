package co.touchlab.researchstack.glue.task;
import android.content.Context;
import android.content.res.Resources;

import java.util.Collections;

import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.model.ConsentSectionModel;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.ConsentReviewDocumentStep;
import co.touchlab.researchstack.core.step.ConsentSharingStep;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.core.ui.scene.ConsentReviewSignatureScene;
import co.touchlab.researchstack.core.ui.scene.FormScene;
import co.touchlab.researchstack.core.utils.ResUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.glue.step.ConsentQuizEvaluationStep;
import co.touchlab.researchstack.glue.step.ConsentQuizQuestionStep;
import co.touchlab.researchstack.glue.ui.scene.ConsentQuizEvaluationScene;
import co.touchlab.researchstack.glue.utils.JsonUtils;

public class ConsentTask extends OrderedTask
{
    public static final String SCHEDULE_ID_CONSENT = "consent";

    public static final String ID_CONSENT = "consent";
    public static final String ID_VISUAL = "ID_VISUAL";
    public static final String ID_FIRST_QUESTION = "FIRST_QUESTION";
    public static final String ID_QUIZ_RESULT = "ID_QUIZ_RESULT";
    public static final String ID_SHARING = "ID_SHARING";
    public static final String ID_CONSENT_DOC = "consent_review_doc";
    public static final String ID_FORM_NAME = "ID_FORM_NAME";
    public static final String ID_SIGNATURE = "ID_SIGNATURE";

    public ConsentTask(Context context)
    {
        super(ID_CONSENT, SCHEDULE_ID_CONSENT);

        ResearchStack researchStack = ResearchStack.getInstance();
        Resources r = context.getResources();

        //TODO Read on main thread for intense UI blockage.
        ConsentSectionModel data = JsonUtils
                .loadClass(context, ConsentSectionModel.class, researchStack.getConsentSections());

        ConsentSignature signature = new ConsentSignature("participant",
                                                          r.getString(R.string.participant), null);
        signature.setRequiresSignatureImage(
                ResearchStack.getInstance().isSignatureEnabledInConsent());

        ConsentDocument doc = new ConsentDocument();
        doc.setTitle(r.getString(R.string.consent_name_title));
        doc.setSignaturePageTitle(R.string.consent_name_title);
        doc.setSignaturePageContent(r.getString(R.string.consent_signature_content));
        doc.setSections(data.getSections());
        doc.addSignature(signature);

        String htmlDocName = data.getDocumentProperties().getHtmlDocument();
        int id = ResUtils.getRawResourceId(context, htmlDocName);
        doc.setHtmlReviewContent(ResUtils.getStringResource(context, id));

        initVisualSteps(context, doc);

        addStep(new ConsentSharingStep(ID_SHARING, r, data.getDocumentProperties()));

        initQuizSteps(context, researchStack);

        initConsentReviewSteps(context, doc);
    }

    private void initVisualSteps(Context ctx, ConsentDocument doc)
    {
        for(int i = 0, size = doc.getSections().size(); i < size; i++)
        {
            ConsentSection section = doc.getSections().get(i);
            ConsentVisualStep step = new ConsentVisualStep("consent_" + i, section);

            String nextString = ctx.getString(R.string.next);
            if(section.getType() == ConsentSection.Type.Overview)
            {
                nextString = ctx.getString(R.string.button_get_started);
            }
            else if(i == size - 1)
            {
                nextString = ctx.getString(R.string.button_done);
            }
            step.setNextButtonString(nextString);

            addStep(step);
        }
    }

    private void initQuizSteps(Context ctx, ResearchStack rs)
    {
        ConsentQuizModel model = JsonUtils.loadClass(ctx, ConsentQuizModel.class, rs.getQuizSections());

        for(int i = 0; i < model.getQuestions().size(); i++)
        {
            ConsentQuizModel.QuizQuestion question = model.getQuestions().get(i);
            if (i == 0)
            {
                question.id = ID_FIRST_QUESTION;
            }
            ConsentQuizQuestionStep quizStep = new ConsentQuizQuestionStep(
                    question.id, model.getQuestionProperties(), question);
            addStep(quizStep);
        }

        ConsentQuizEvaluationStep evaluationStep = new ConsentQuizEvaluationStep(
                ID_QUIZ_RESULT, model.getEvaluationProperties());
        addStep(evaluationStep);
    }

    private void initConsentReviewSteps(Context ctx, ConsentDocument doc)
    {
        // Add ConsentReviewDocumentStep (view html version of the PDF doc)
        StringBuilder docBuilder = new StringBuilder("</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
        String title = ctx.getString(R.string.consent_review_title);
        docBuilder.append(String.format(
                "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>", title));
        String detail =  ctx.getString(R.string.consent_review_instruction);
        docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
        docBuilder.append("</div></br>");
        docBuilder.append(doc.getHtmlReviewContent());

        ConsentReviewDocumentStep step = new ConsentReviewDocumentStep(ID_CONSENT_DOC);
        step.setConsentHTML(docBuilder.toString());
        step.setConfirmMessage(ctx.getString(R.string.consent_review_reason));
        addStep(step);

        // Add full-name input
        if (doc.getSignature(0).isRequiresName())
        {
            String formTitle = ctx.getString(R.string.consent_name_title);
            FormStep formStep = new FormStep(ID_FORM_NAME, formTitle, step.getText());
            formStep.setSceneTitle(R.string.consent);
            formStep.setUseSurveyMode(false);
            formStep.setOptional(false);

            TextAnswerFormat format = new TextAnswerFormat();
            format.setIsMultipleLines(false);
            // TODO Implement the following -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
            // TODO format.autocapitalizationType = UITextAutocapitalizationTypeWords;
            // TODO format.autocorrectionType = UITextAutocorrectionTypeNo;
            // TODO format.spellCheckingType = UITextSpellCheckingTypeNo;

            String placeholder = ctx.getResources().getString(R.string.consent_name_placeholder);
            String nameText = ctx.getResources().getString(R.string.consent_name_full);
            FormScene.FormItem fullName = new FormScene.FormItem(formStep.getIdentifier(), nameText,
                                                                 format, placeholder);
            formStep.setFormItems(Collections.singletonList(fullName));
            addStep(formStep);
        }

        // Add signature input
        if (doc.getSignature(0).isRequiresSignatureImage())
        {
            Step signatureStep = new Step(ID_SIGNATURE);
            signatureStep.setTitle(ctx.getString(R.string.consent_signature_title));
            signatureStep.setText(ctx.getString(R.string.consent_signature_instruction));
            signatureStep.setOptional(false);
            signatureStep.setSceneClass(ConsentReviewSignatureScene.class);
            addStep(signatureStep);
        }
    }

    /**
     * TODO Fix multiple points of truth when figuring out the attempt & incorrect count.
     *
     * @param step
     * @param result
     * @return
     */
    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        if(step != null)
        {
            // First, we get the evaluation step and increase the amount of incorrect answers if
            // needed
            if (step instanceof ConsentQuizQuestionStep)
            {
                ConsentQuizQuestionStep firstQuestion = (ConsentQuizQuestionStep)
                        getStepWithIdentifier(ID_FIRST_QUESTION);
                int incorrectAnswers = getQuestionIncorrectCount(result, firstQuestion, 0);

                LogExt.i(getClass(), "Quiz question answered. Number of incorrect: " + incorrectAnswers);

                ConsentQuizEvaluationStep evaluationStep = (ConsentQuizEvaluationStep)
                        getStepWithIdentifier(ID_QUIZ_RESULT);
                evaluationStep.setIncorrectCount(incorrectAnswers);
            }

            // If this is the ConsentQuizEvaluationStep, we need to check if the user has passed
            // or failed the quiz. If they have have failed, AND it was their first attempt, let the
            // user retake the quiz. If attempts > 1, they must go through the visual consent steps
            // another time
            else if(step instanceof ConsentQuizEvaluationStep)
            {
                StepResult<Boolean> stepResult = result.getStepResult(step.getIdentifier());

                boolean quizPassed = stepResult.getResultForIdentifier(
                        ConsentQuizEvaluationScene.KEY_RESULT_PASS);

                boolean exceedsAttempts = stepResult.getResultForIdentifier(
                        ConsentQuizEvaluationScene.KEY_RESULT_EXCEED_ATTEMPS);

                LogExt.i(getClass(), "Quiz has passed: " + quizPassed);

                if(! quizPassed)
                {
                    // TODO Clearing incorrect count causes multiple sources of truth
                    ConsentQuizEvaluationStep evaluationStep = (ConsentQuizEvaluationStep) step;
                    evaluationStep.setIncorrectCount(0);

                    Step firstQuestion = getStepWithIdentifier(ID_FIRST_QUESTION);
                    clearQuestionIncorrectCount(result, firstQuestion);

                    if (exceedsAttempts)
                    {
                        LogExt.i(getClass(), "Quiz attempts exceeded, starting visual");
                        evaluationStep.setAttempt(0);
                        return getStepWithIdentifier(ID_VISUAL);
                    }
                    else
                    {
                        LogExt.i(getClass(), "Restarting quiz");
                        evaluationStep.setAttempt(1);
                        return firstQuestion;
                    }
                }
            }
        }

        return super.getStepAfterStep(step, result);
    }

    private void clearQuestionIncorrectCount(TaskResult result, Step step)
    {
        if (step != null)
        {
            boolean isQuestion = step instanceof ConsentQuizQuestionStep;
            boolean isEvaluation = step instanceof ConsentQuizEvaluationStep;

            if (isQuestion || isEvaluation)
            {
                // Remove the result
                result.setStepResultForStepIdentifier(step.getIdentifier(), null);

                if (isQuestion)
                {
                    // Clear the next step
                    Step next = super.getStepAfterStep(step, result);
                    clearQuestionIncorrectCount(result, next);
                }
            }
        }
    }

    private int getQuestionIncorrectCount(TaskResult result, Step step, int count)
    {
        if (step != null && step instanceof ConsentQuizQuestionStep)
        {
            StepResult stepResult = result.getStepResult(step.getIdentifier());
            if (stepResult != null)
            {
                boolean correct = (boolean) stepResult.getResult();
                Step next = super.getStepAfterStep(step, result);
                return getQuestionIncorrectCount(result, next, count + (correct ? 0 : 1));
            }
        }

        return count;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        return super.getStepBeforeStep(step, result);
    }

    //  -(ORKStep *)stepBeforeStep:(ORKStep *)__unused step withResult:(ORKTaskResult *)__unused result{
    //      return nil;
    //  }
    //
    //  -(ORKStep *)stepAfterStep:(ORKStep *)step withResult:(ORKTaskResult *)__unused result{
    //
    //      APCUser *user = ((APHAppDelegate *)[UIApplication sharedApplication].delegate ).dataSubstrate.currentUser;
    //      ORKStep *nextStep;
    //      if (user.isSignedIn) {
    //          if (!step) {
    //              nextStep = [self.steps objectAtIndex:0];
    //          }else{
    //              nextStep = nil;
    //              [[NSNotificationCenter defaultCenter] postNotificationName:kReturnControlOfTaskDelegate object:nil];
    //          }
    //      }else if(user.isSignedUp){
    //          if (!step) {
    //              nextStep = [self.steps objectAtIndex:0];
    //          }else{
    //              nextStep = nil;
    //              [[NSNotificationCenter defaultCenter] postNotificationName:kReturnControlOfTaskDelegate object:nil];
    //          }
    //      }else{
    //
    //          if (!step) {
    //              nextStep = [self.steps objectAtIndex:0];
    //          }else if ([step.identifier isEqualToString:@"consentStep"]) {
    //              nextStep = [self.steps objectAtIndex:1];
    //          }else if ([step.identifier isEqualToString:@"question1"]) {
    //              nextStep = [self.steps objectAtIndex:2];
    //          }else if ([step.identifier isEqualToString:@"question2"]) {
    //              nextStep = [self.steps objectAtIndex:3];
    //          }else if ([step.identifier isEqualToString:@"question3"]) {
    //              nextStep = [self.steps objectAtIndex:4];
    //          }else if ([step.identifier isEqualToString:@"quizEvaluation"]) {
    //
    //              if (self.passedQuiz) {
    //                  nextStep = [self.steps objectAtIndex:5];//return to consent review step
    //              }else if (self.failedAttempts == 1) {
    //                  nextStep = [self.steps objectAtIndex:1];//return to quiz
    //              }else {
    //                  nextStep = [self.steps objectAtIndex:0];//reassurance steps
    //              }
    //
    //          }else if ([step isKindOfClass:[ORKConsentReviewStep class]]) {
    //              nextStep = step;
    //          }else{
    //              nextStep = nil;
    //          }
    //
    //      }
    //
    //      return nextStep;
    //  }

}
