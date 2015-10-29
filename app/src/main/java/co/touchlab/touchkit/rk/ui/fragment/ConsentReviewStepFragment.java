package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentReviewStep;
import co.touchlab.touchkit.rk.ui.fragment.callbacks.ConsentReviewCallback;
import co.touchlab.touchkit.rk.ui.views.ConsentReviewDocumentLayout;

public class ConsentReviewStepFragment extends MultiSectionStepFragment implements ConsentReviewCallback
{

    public static final int SECTION_DOCUMENT = 0;
    public static final int SECTION_FORM = 1;
    public static final int SECTION_SIGNATURE = 2;
    public static final int SECTION_COUNT = 3;

//    static NSString *const _NameFormIdentifier = @"nameForm";
//    static NSString *const _GivenNameIdentifier = @"given";
//    static NSString *const _FamilyNameIdentifier = @"family";

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
                    showConsentSection(SECTION_FORM, true);
            }).setNegativeButton(R.string.cancel, null)
            .show();
    }

    @Override
    public View createSectionLayout(LayoutInflater inflater, int section)
    {
        if (section == SECTION_DOCUMENT)
        {
            ConsentReviewDocumentLayout layout = new ConsentReviewDocumentLayout(getContext());
            layout.setCallback(this);
            return layout;
        }
        else if (section == SECTION_FORM)
        {
            throw new RuntimeException("SECTION_FORM");
        }
        else if (section == SECTION_SIGNATURE)
        {
            throw new RuntimeException("SECTION_SIGNATURE");
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getSectionCount()
    {
        return SECTION_COUNT;
    }

    @Override
    public int getNextViewId()
    {
        return R.id.next;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return null;
    }


    //    - (BOOL)currentLocalePresentsFamilyNameFirst {
    //        NSString * language = [[[NSLocale preferredLanguages] firstObject] substringToIndex:2];
    //        static dispatch_once_t onceToken;
    //        static NSArray *familyNameFirstLangs = nil;
    //        dispatch_once(&onceToken, ^{
    //            familyNameFirstLangs = @[@"zh",@"ko",@"ja",@"vi"];
    //        });
    //        return (language != nil) && [familyNameFirstLangs containsObject:language];
    //    }


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
