package co.touchlab.researchstack.glue.task;
import android.content.Context;
import android.content.res.Resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSectionModel;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.ConsentReviewStep;
import co.touchlab.researchstack.core.step.ConsentSharingStep;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.glue.step.ConsentQuizStep;
import co.touchlab.researchstack.glue.utils.JsonUtils;

public class ConsentTask extends OrderedTask
{

    public ConsentTask(Context context)
    {
        super("consent", "consent");

        ResearchStack researchStack = ResearchStack.getInstance();
        Resources r = context.getResources();

        //TODO Read on main thread for intense UI blockage.
        ConsentSectionModel data = JsonUtils
                .loadClassFromRawJson(context,
                        ConsentSectionModel.class,
                        researchStack.getConsentSections());

        ConsentSignature signature = new ConsentSignature("participant",
                                                          r.getString(R.string.participant), null);
        signature.setRequiresSignatureImage(
                ResearchStack.getInstance().isSignatureEnabledInConsent());

        ConsentDocument consent = new ConsentDocument();
        consent.setTitle(r.getString(R.string.consent_name_title));
        consent.setSignaturePageTitle(R.string.consent_name_title);
        consent.setSignaturePageContent(r.getString(R.string.consent_signature_content));
        consent.setSections(data.getSections());
        consent.addSignature(signature);

        String htmlDocName = data.getDocumentProperties().getHtmlDocument();
        int id = context.getResources().getIdentifier(htmlDocName, "raw", context.getPackageName());
        consent.setHtmlReviewContent(getStringResource(context, id));

        ConsentVisualStep visualStep = new ConsentVisualStep("visual", consent);
        addStep(visualStep);

        ConsentSharingStep sharingStep = new ConsentSharingStep("sharing", r,
                                                                data.getDocumentProperties());
        addStep(sharingStep);

        ConsentQuizModel quizModel = JsonUtils.loadClassFromRawJson(context,
                ConsentQuizModel.class,
                researchStack.getQuizSections());
        ConsentQuizStep quizStep = new ConsentQuizStep("quiz", quizModel);
        addStep(quizStep);

        String reasonForConsent = r.getString(R.string.consent_review_reason);
        ConsentReviewStep reviewStep = new ConsentReviewStep("reviewStep", signature, consent, reasonForConsent);
        addStep(reviewStep);
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        if(step != null)
        {
            LogExt.i(getClass(), "getStepAfterStep " + step.getIdentifier());

            if(step instanceof ConsentQuizStep)
            {
                LogExt.i(getClass(), "step is ConsentQuizStep");

                boolean passed = (boolean) result
                        .getStepResult(step.getIdentifier()).getResult();

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

    public String getStringResource(Context ctx, int id)
    {
        return new String(getResource(id, ctx), Charset.forName("UTF-8"));
    }

    public byte[] getResource(int id, Context context)
    {
        InputStream is =  context.getResources().openRawResource(id);
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        byte[] readBuffer = new byte[4 * 1024];

        try
        {
            int read;
            do
            {
                read = is.read(readBuffer, 0, readBuffer.length);
                if(read == - 1)
                {
                    break;
                }
                byteOutput.write(readBuffer, 0, read);
            }
            while(true);

            return byteOutput.toByteArray();
        }
        catch(IOException e)
        {
            LogExt.e(getClass(), e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                LogExt.e(getClass(), e);
            }
        }
        return null;
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
