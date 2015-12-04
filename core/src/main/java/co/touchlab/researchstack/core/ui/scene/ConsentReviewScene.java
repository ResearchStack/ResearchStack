package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

import java.io.ByteArrayOutputStream;
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
import co.touchlab.researchstack.core.ui.callbacks.ConsentReviewCallback;

public class ConsentReviewScene extends MultiSubSectionScene implements ConsentReviewCallback
{
    public static final String TAG = ConsentReviewScene.class.getSimpleName();

    public static final int SECTION_REVIEW_DOCUMENT = 0;
    public static final int SECTION_REVIEW_NAME = 1;
    public static final int SECTION_REVIEW_SIGNATURE = 2;

    private static final String NameFormIdentifier = "nameForm";
    private static final String NameIdentifier = "name";

    public List<Integer> sections;

    public ConsentReviewScene(Context context, Step step)
    {
        super(context, step);
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
    public void initialize()
    {
        super.initialize();
        initStepResult();
    }

    private void initStepResult()
    {
        StepResult<ConsentSignatureResult> parentResult = new StepResult<>(getStep().getIdentifier());

        ConsentSignatureResult result = new ConsentSignatureResult(getStep().getIdentifier());
        result.setStartDate(new Date());

        ConsentSignature clone = ((ConsentReviewStep) getStep()).getSignature();
        result.setSignature(clone);

        parentResult.getResults()
                .put(result.getIdentifier(), result);

        setStepResult(parentResult);
    }

    @Override
    public SceneImpl onCreateScene(LayoutInflater inflater, int scenePos)
    {
        ConsentReviewStep step = (ConsentReviewStep) getStep();

        int section = sections.get(scenePos);

        if (section == SECTION_REVIEW_DOCUMENT)
        {
            ConsentReviewDocumentScene layout = new ConsentReviewDocumentScene(getContext());
            layout.setCallback(this);
            return layout;
        }
        else if (section == SECTION_REVIEW_NAME)
        {
            // TODO now that it's the full name, it probably doesn't need to be a form step
            FormStep formStep = new FormStep(NameFormIdentifier,
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
            FormScene.FormItem givenName = new FormScene.FormItem(
                    NameIdentifier, nameText, format, placeholder);
            items.add(givenName);

            formStep.setFormItems(items);

            return new FormScene(getContext(), formStep);
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
    public StepResult createNewStepResult(String stepIdentifier)
    {
       return null;
    }

    @Override
    public void onSceneChanged(Scene oldScene, Scene newScene)
    {

        //Handle new scene, pass the title back up to the host
        if (newScene instanceof ConsentReviewSignatureScene)
        {
            ConsentReviewStep step = (ConsentReviewStep) getStep();
            int titleResId = step.getDocument().getSignaturePageTitle();
            getCallbacks().onChangeStepTitle(getString(titleResId));
        }
        else
        {
            getCallbacks().onChangeStepTitle(getString(R.string.consent));
        }

        // Check if old scene is null
        if (oldScene == null)
        {
            return;
        }

        Log.i(TAG, "scenePoppedOff " + oldScene.getClass().getSimpleName());

        //Handle the result of the popped off scene
        StepResult parentResult = getStepResult();
        ConsentSignatureResult result = (ConsentSignatureResult) parentResult
                .getResultForIdentifier(getStep().getIdentifier());

        ConsentSignature signature = result.getSignature();

//        TODO handle signature date-format string
//        if (signature.getSignatureDateFormatString().length() > 0) {
//            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
//            [dateFormatter setDateFormat:_currentSignature.signatureDateFormatString];
//            _currentSignature.signatureDate = [dateFormatter stringFromDate:[NSDate date]];
//        } else {
//            _currentSignature.signatureDate = ORKSignatureStringFromDate([NSDate date]);
//        }

        if (oldScene instanceof ConsentReviewDocumentScene)
        {
            result.setConsented(true);
        }
        else if (oldScene instanceof FormScene)
        {
            StepResult formResult = oldScene.getStepResult();

            TextQuestionResult nameResult = (TextQuestionResult) formResult
                    .getResultForIdentifier(NameIdentifier);
            signature.setFullName(nameResult.getTextAnswer());
        }
        else if (oldScene instanceof ConsentReviewSignatureScene)
        {
            ConsentReviewSignatureScene sigScene = (ConsentReviewSignatureScene) oldScene;

            //TODO The follow is less than ideal
            Bitmap bitmap = sigScene.getSignatureImage();
            if (bitmap != null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                signature.setSignatureImage(byteArray);
            }

            // If we get here, this means our last oldScene oldScene will trigger the step to finish. Its
            // a good idea to set the end date now.
            result.setEndDate(new Date());
        }
        else
        {
            String message = oldScene.getClass().getSimpleName() + " not supported";
            DevUtils.throwUnsupportedOpException(message);
        }

        getCallbacks().onStepResultChanged(getStep(), result);
    }

    @Override
    public void showConfirmationDialog()
    {
        ConsentReviewStep step = (ConsentReviewStep) getStep();

        new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setTitle(R.string.consent_review_alert_title)
                .setMessage(step.getReasonForConsent()).setCancelable(false)
                .setPositiveButton(R.string.agree, (dialog, which) -> {
                    showScene(SECTION_REVIEW_NAME, true);
                }).setNegativeButton(R.string.cancel, null)
                .show();
    }


    @Override
    public void closeToWelcomeFlow()
    {
        getCallbacks().onCancelStep();
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
