package co.touchlab.researchstack.common.task;
import android.content.Context;
import android.content.res.Resources;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.helpers.LogExt;
import co.touchlab.researchstack.common.model.ConsentDocument;
import co.touchlab.researchstack.common.model.ConsentQuizModel;
import co.touchlab.researchstack.common.model.ConsentSectionModel;
import co.touchlab.researchstack.common.model.ConsentSignature;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.result.TaskResult;
import co.touchlab.researchstack.common.step.ConsentQuizStep;
import co.touchlab.researchstack.common.step.ConsentReviewStep;
import co.touchlab.researchstack.common.step.ConsentSharingStep;
import co.touchlab.researchstack.common.step.ConsentVisualStep;
import co.touchlab.researchstack.common.step.Step;
import co.touchlab.researchstack.utils.JsonUtils;

public class ConsentTask extends OrderedTask
{

    public ConsentTask(Context context)
    {
        super("consent");

        ResearchStackApplication application = ResearchStackApplication.getInstance();
        Resources r = context.getResources();

        //TODO Read on main thread for intense UI blockage.
        ConsentSectionModel data = JsonUtils
                .loadClassFromRawJson(context, ConsentSectionModel.class,
                                      application.getConsentSections());

        ConsentSignature signature = new ConsentSignature("participant",
                                                          r.getString(R.string.participant), null);
        signature.setRequiresSignatureImage(
                ResearchStackApplication.getInstance().isSignatureEnabledInConsent());

        ConsentDocument consent = new ConsentDocument();
        consent.setTitle(r.getString(R.string.signature_page_title));
        consent.setSignaturePageTitle(r.getString(R.string.signature_page_title));
        consent.setSignaturePageContent(r.getString(R.string.signature_page_content));
        consent.setSections(data.getSections());
        consent.addSignature(signature);

        ConsentVisualStep visualStep = new ConsentVisualStep("visual", consent);
        addStep(visualStep);

        ConsentSharingStep sharingStep = new ConsentSharingStep("sharing", r,
                                                                data.getDocumentProperties());
        addStep(sharingStep);

        ConsentQuizModel quizModel = JsonUtils.loadClassFromRawJson(context, ConsentQuizModel.class,
                                                                    application.getQuizSections());
        ConsentQuizStep quizStep = new ConsentQuizStep("quiz", quizModel);
        addStep(quizStep);

        String reasonForConsent = r.getString(R.string.consent_review_reason);
        ConsentReviewStep reviewStep = new ConsentReviewStep("reviewStep", signature, consent,
                                                             reasonForConsent);
        addStep(reviewStep);
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        if (step != null)
        {
            LogExt.i(getClass(), "getStepAfterStep " + step.getIdentifier());

            if(step instanceof ConsentQuizStep)
            {
                LogExt.i(getClass(), "step is ConsentQuizStep");

                StepResult<QuestionResult<Boolean>> stepResult = (StepResult<QuestionResult<Boolean>>) result
                        .getStepResultForStepIdentifier(step.getIdentifier());
                QuestionResult<Boolean> passedResult = stepResult
                        .getResultForIdentifier(step.getIdentifier());
                boolean passed = passedResult.getAnswer();

                LogExt.i(getClass(), "ConsentQuizStep result is " + passed);

                if(! passed)
                {
                    LogExt.i(getClass(), "Quiz failed, going back to visual step");
                    return getStepWithIdentifier("visual");
                }
            }
        }

        return super.getStepAfterStep(step, result);
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
