package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentReviewStep;
import co.touchlab.touchkit.rk.ui.callbacks.ConsentReviewCallback;
import co.touchlab.touchkit.rk.ui.scene.ConsentReviewDocumentScene;
import co.touchlab.touchkit.rk.ui.scene.ConsentReviewSignatureScene;
import co.touchlab.touchkit.rk.ui.scene.GenericFormScene;

public class ConsentReviewStepFragment extends MultiSectionStepFragment implements ConsentReviewCallback
{

    public static final int SECTION_REVIEW_DOCUMENT = 0;
    public static final int SECTION_REVIEW_NAME = 1;
    public static final int SECTION_REVIEW_SIGNATURE = 2;

    private static final String NameFormIdentifier = "nameForm";
    private static final String GivenNameIdentifier = "given";
    private static final String FamilyNameIdentifier = "family";

    public List<Integer> sections;

    public ConsentReviewStepFragment()
    {
        super();
    }

    //TODO Add ConsentFormStep [First name, last name]

    //TODO Add ConsentSignatureStep [Draw signature]

    public static Fragment newInstance(ConsentReviewStep step)
    {
        ConsentReviewStepFragment fragment = new ConsentReviewStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ConsentReviewStep step = (ConsentReviewStep) getStep();

        sections = new ArrayList<>();

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

    //TODO Clear activity stack up until OnboardingActivity
    @Override
    public void closeToWelcomeFlow()
    {
        Toast.makeText(getContext(), "Close and show welcome screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showConfirmationDialog()
    {
        ConsentReviewStep step = (ConsentReviewStep) getStep();

        new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
            .setTitle(R.string.consent_review_alert_title)
            .setMessage(step.getReasonForConsent()).setCancelable(false)
                .setPositiveButton(R.string.agree, (dialog, which) -> {
                    showConsentSection(SECTION_REVIEW_NAME, true);
            }).setNegativeButton(R.string.cancel, null)
            .show();
    }

    @Override
    public View createSectionLayout(LayoutInflater inflater, int position)
    {
        int section = sections.get(position);

        if (section == SECTION_REVIEW_DOCUMENT)
        {
            ConsentReviewDocumentScene layout = new ConsentReviewDocumentScene(getContext());
            layout.setCallback(this);
            return layout;
        }
        else if (section == SECTION_REVIEW_NAME)
        {
            //TODO Create TextAnswerFormat
//            ORKTextAnswerFormat *nameAnswerFormat = [ORKTextAnswerFormat textAnswerFormat];
//            nameAnswerFormat.multipleLines = NO;
//            nameAnswerFormat.autocapitalizationType = UITextAutocapitalizationTypeWords;
//            nameAnswerFormat.autocorrectionType = UITextAutocorrectionTypeNo;
//            nameAnswerFormat.spellCheckingType = UITextSpellCheckingTypeNo;

            String placeholder = getResources().getString(R.string.consent_name_placeholder);
            List<GenericFormScene.FormItem> items = new ArrayList<>();

            //TODO Pass in Answer format
            String givenText = getResources().getString(R.string.consent_name_first);
            GenericFormScene.FormItem givenName = new GenericFormScene.FormItem(
                    GivenNameIdentifier, givenText, null, placeholder);
            items.add(givenName);

            //TODO Pass in Answer format
            String familyText = getResources().getString(R.string.consent_name_last);
            GenericFormScene.FormItem familyName = new GenericFormScene.FormItem(
                    FamilyNameIdentifier, familyText, null, placeholder);
            items.add(familyName);

            if (getResources().getBoolean(R.bool.lang_display_last_name_first)) {
                Collections.reverse(items);
            }

            GenericFormScene layout = new GenericFormScene(getContext());
            layout.setTitle(R.string.consent_name_title);
            layout.setSkip(false, 0, null);
            layout.setFormItems(items);

            return layout;
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
            return null;
        }
    }

    @Override
    public int getSectionCount()
    {
        return sections.size();
    }

    @Override
    public int getNextViewId()
    {
        return R.id.next;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        //TODO Implement consent result
        return null;
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
