package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.dev.DevUtils;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.ConsentSignatureResult;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TextQuestionResult;
import co.touchlab.researchstack.core.step.ConsentReviewStep;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.Step;

public class ConsentReviewScene extends MultiSubSectionScene<ConsentSignatureResult>
{
    public static final String TAG = ConsentReviewScene.class.getSimpleName();

    public static final int SECTION_REVIEW_DOCUMENT = 0;
    public static final int SECTION_REVIEW_NAME = 1;
    public static final int SECTION_REVIEW_SIGNATURE = 2;

    private static final String STEP_ID_NAME_FORM = "nameForm";
    private static final String RESULT_ID_NAME = "name";

    public List<Integer> sections;

    public ConsentReviewScene(Context context, Step step, StepResult result)
    {
        super(context, step, result);
    }

    @Override
    public void onPreInitialized()
    {
        super.onPreInitialized();

        sections = new ArrayList<>();

        ConsentReviewStep step = (ConsentReviewStep) getStep();

        if (step.getDocument() != null) {
            sections.add(SECTION_REVIEW_DOCUMENT);
        }

        if (step.getSignature().isRequiresName()) {
            sections.add(SECTION_REVIEW_NAME);
        }

        if (step.getSignature().isRequiresSignatureImage()) {
            sections.add(SECTION_REVIEW_SIGNATURE);
        }
    }

    @Override
    public StepResult initStepResult()
    {
        StepResult<ConsentSignatureResult> result = new StepResult<>(getStep().getIdentifier());

        ConsentSignatureResult sigResult = new ConsentSignatureResult(getStep().getIdentifier());
        sigResult.setStartDate(new Date());

        ConsentSignature clone = ((ConsentReviewStep) getStep()).getSignature();
        sigResult.setSignature(clone);

        result.setResultForIdentifier(StepResult.DEFAULT_KEY, sigResult);

        return result;
    }

    @Override
    public Scene onCreateScene(LayoutInflater inflater, int scenePos)
    {
        ConsentReviewStep step = (ConsentReviewStep) getStep();

        int section = sections.get(scenePos);

        if (section == SECTION_REVIEW_DOCUMENT)
        {
            ConsentReviewDocumentScene layout = new ConsentReviewDocumentScene(getContext());
            layout.setCallbacks(this);
            return layout;
        }
        else if (section == SECTION_REVIEW_NAME)
        {
            // TODO now that it's the full name, it probably doesn't need to be a form step
            FormStep formStep = new FormStep(STEP_ID_NAME_FORM,
                                             getString(R.string.consent_name_title),
                                             step.getText());
            formStep.setUseSurveyMode(false);
            formStep.setOptional(false);

            TextAnswerFormat format = new TextAnswerFormat();
            format.setIsMultipleLines(false);
//            TODO TODO set the following
//            nameAnswerFormat.autocapitalizationType = UITextAutocapitalizationTypeWords;
//            nameAnswerFormat.autocorrectionType = UITextAutocorrectionTypeNo;
//            nameAnswerFormat.spellCheckingType = UITextSpellCheckingTypeNo;

            List<FormScene.FormItem> items = new ArrayList<>();
            String placeholder = getResources().getString(R.string.consent_name_placeholder);

            String nameText = getResources().getString(R.string.consent_name_full);
            FormScene.FormItem givenName = new FormScene.FormItem(RESULT_ID_NAME, nameText, format, placeholder);
            items.add(givenName);

            formStep.setFormItems(items);

            return new FormScene(getContext(), formStep, null);
        }
        else if (section == SECTION_REVIEW_SIGNATURE)
        {
            ConsentReviewSignatureScene layout = new ConsentReviewSignatureScene(getContext());
            layout.setTitle(R.string.consent_signature_title);
            layout.setSummary(R.string.consent_signature_instruction);
            layout.setSkip(false, 0, null);
            return layout;
        }
        else
        {
            DevUtils.throwUnsupportedOpException();
            return null;
        }
    }

    @Override
    public int getSceneCount()
    {
        return sections.size();
    }

    @Override
    public void onSceneChanged(Scene oldScene, Scene newScene)
    {
        //Handle new scene, pass the title back up to the host
        if (newScene instanceof ConsentReviewSignatureScene)
        {
            ConsentReviewStep step = (ConsentReviewStep) getStep();
            int titleResId = step.getDocument().getSignaturePageTitle();
            getCallbacks().onStepTitleChanged(getString(titleResId));
        }
        else
        {
            getCallbacks().onStepTitleChanged(getString(R.string.consent));
        }
    }

    @Override
    public void notifyStepResultChanged(Step sceneStep, StepResult sceneResult)
    {
        //Handle the result of the popped off scene
        StepResult<ConsentSignatureResult> result = getStepResult();
        ConsentSignatureResult sigResult = result.getResultForIdentifier(StepResult.DEFAULT_KEY);

        //        TODO handle signature date-format string
        //        if (signature.getSignatureDateFormatString().length() > 0) {
        //            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        //            [dateFormatter setDateFormat:_currentSignature.signatureDateFormatString];
        //            _currentSignature.signatureDate = [dateFormatter stringFromDate:[NSDate date]];
        //        } else {
        //            _currentSignature.signatureDate = ORKSignatureStringFromDate([NSDate date]);
        //        }

        if (ConsentReviewDocumentScene.STEP_ID.equals(sceneStep.getIdentifier()))
        {
            sigResult.setConsented(true);
        }
        else if (STEP_ID_NAME_FORM.equals(sceneStep.getIdentifier()))
        {
            TextQuestionResult nameResult = (TextQuestionResult) sceneResult
                    .getResultForIdentifier(RESULT_ID_NAME);
            sigResult.getSignature().setFullName(nameResult.getTextAnswer());
        }
        else if (ConsentReviewSignatureScene.STEP_ID.equals(sceneStep.getIdentifier()))
        {
            StepResult<String> signatureResult = (StepResult<String>) sceneResult;
            String base64StringImage = signatureResult.getResultForIdentifier(StepResult.DEFAULT_KEY);
            sigResult.getSignature().setSignatureImage(base64StringImage);
            sigResult.setEndDate(new Date());
        }
        else
        {
            String message = "Result with ID:" + sceneStep.getIdentifier() + " not supported";
            DevUtils.throwUnsupportedOpException(message);
        }
    }

    @Override
    public void onNextStep(Step step)
    {
        if (step != null && step.getIdentifier().equals("consent_review_doc"))
        {
            ConsentReviewStep parentStep = (ConsentReviewStep) getStep();

            new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                    .setTitle(R.string.consent_review_alert_title)
                    .setMessage(parentStep.getReasonForConsent()).setCancelable(false)
                    .setPositiveButton(R.string.agree, (dialog, which) -> {
                        super.onNextStep(step);
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
        }
        else
        {
            super.onNextStep(step);
        }
    }

        /*

     - (ORKFormStepViewController *)makeNameFormViewController {
        ORKFormStep *formStep = [[ORKFormStep alloc] initWithIdentifier:_NameFormIdentifier
                                                                title:self.step.title ? : ORKLocalizedString(@"CONSENT_NAME_TITLE", nil)
                                                                 text:self.step.text];
        formStep.useSurveyMode = NO;

        ORKTextAnswerFormat *nameAnswerFormat = [ORKTextAnswerFormat textAnswerFormat];
        nameAnswerFormat.multipleLines = NO;
        nameAnswerFormat.autocapitalizationType = UITextAutocapitalizationTypeWords;
        nameAnswerFormat.autocorrectionType = UITextAutocorrectionTypeNo;
        nameAnswerFormat.spellCheckingType = UITextSpellCheckingTypeNo;
        ORKFormItem *givenName = [[ORKFormItem alloc] initWithIdentifier:_GivenNameIdentifier
                                                                  text:ORKLocalizedString(@"CONSENT_NAME_FIRST", nil)
                                                          answerFormat:nameAnswerFormat];
        givenName.placeholder = ORKLocalizedString(@"CONSENT_NAME_PLACEHOLDER", nil);

        ORKFormItem *familyName = [[ORKFormItem alloc] initWithIdentifier:_FamilyNameIdentifier
                                                                 text:ORKLocalizedString(@"CONSENT_NAME_LAST", nil)
                                                         answerFormat:nameAnswerFormat];
        familyName.placeholder = ORKLocalizedString(@"CONSENT_NAME_PLACEHOLDER", nil);

        NSArray *formItems = @[givenName, familyName];
        if ([self currentLocalePresentsFamilyNameFirst]) {
            formItems = @[familyName, givenName];
        }

        [formStep setFormItems:formItems];

        formStep.optional = NO;

        ORKTextQuestionResult *givenNameDefault = [[ORKTextQuestionResult alloc] initWithIdentifier:_GivenNameIdentifier];
        givenNameDefault.textAnswer = _signatureFirst;
        ORKTextQuestionResult *familyNameDefault = [[ORKTextQuestionResult alloc] initWithIdentifier:_FamilyNameIdentifier];
        familyNameDefault.textAnswer = _signatureLast;
        ORKStepResult *defaults = [[ORKStepResult alloc] initWithStepIdentifier:_NameFormIdentifier results:@[givenNameDefault, familyNameDefault]];

        ORKFormStepViewController *viewController = [[ORKFormStepViewController alloc] initWithStep:formStep result:defaults];
        viewController.delegate = self;

        return viewController;
    }

    - (ORKConsentReviewController *)makeDocumentReviewViewController {
        ORKConsentSignature *originalSignature = [self.consentReviewStep signature];
        ORKConsentDocument *origninalDocument = self.consentReviewStep.consentDocument;

        NSUInteger index = [origninalDocument.signatures indexOfObject:originalSignature];

        // Deep copy
        ORKConsentDocument *document = [origninalDocument copy];

        if (index != NSNotFound) {
            ORKConsentSignature *signature = document.signatures[index];

            if (signature.requiresName) {
                signature.givenName = _signatureFirst;
                signature.familyName = _signatureLast;
            }
        }

        NSString *html = [document mobileHTMLWithTitle:ORKLocalizedString(@"CONSENT_REVIEW_TITLE", nil)
                                                 detail:ORKLocalizedString(@"CONSENT_REVIEW_INSTRUCTION", nil)];

        ORKConsentReviewController *reviewViewController = [[ORKConsentReviewController alloc] initWithHTML:html delegate:self];
        reviewViewController.localizedReasonForConsent = [[self consentReviewStep] reasonForConsent];
        return reviewViewController;
    }

    - (ORKConsentSignatureController *)makeSignatureViewController {
        ORKConsentSignatureController *signatureController = [[ORKConsentSignatureController alloc] init];
        signatureController.delegate = self;
        return signatureController;
    }

     */
}
