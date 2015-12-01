package co.touchlab.researchstack.core.task;
import android.content.Context;

import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;

public class ConsentTask extends OrderedTask
{

    public ConsentTask(Context context)
    {
        super("consent", "consent");

//        TODO MAKE THIS WORK
//        ResearchStackApplication application = ResearchStackApplication.getInstance();
//        Resources r = context.getResources();
//
//        TODO Read on main thread for intense UI blockage.
//        ConsentSectionModel data = JsonUtils
//                .loadClassFromRawJson(context, ConsentSectionModel.class,
//                                      application.getConsentSections());
//
//        ConsentSignature signature = new ConsentSignature("participant",
//                                                          r.getString(R.string.participant), null);
//        signature.setRequiresSignatureImage(
//                ResearchStackApplication.getInstance().isSignatureEnabledInConsent());
//
//        ConsentDocument consent = new ConsentDocument();
//        consent.setTitle(r.getString(R.string.signature_page_title));
//        consent.setSignaturePageTitle(R.string.signature_page_title);
//        consent.setSignaturePageContent(r.getString(R.string.signature_page_content));
//        consent.setSections(data.getSections());
//        consent.addSignature(signature);
//
//        ConsentVisualStep visualStep = new ConsentVisualStep("visual", consent);
//        addStep(visualStep);
//
//        ConsentSharingStep sharingStep = new ConsentSharingStep("sharing", r,
//                                                                data.getDocumentProperties());
//        addStep(sharingStep);
//
//        String reasonForConsent = r.getString(R.string.consent_review_reason);
//        ConsentReviewStep reviewStep = new ConsentReviewStep("reviewStep", signature, consent,
//                                                             reasonForConsent);
//        addStep(reviewStep);
    }


    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        return super.getStepAfterStep(step, result);
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        return super.getStepBeforeStep(step, result);
    }

}
