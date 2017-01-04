package org.researchstack.backbone.utils;

import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.SurveyFactory;

import java.util.List;

/**
 * Created by TheMDP on 12/29/16.
 */

public class ConsentDocumentFactory extends SurveyFactory {

    public ConsentDocumentFactory(List<SurveyItem> surveyItems) {
        super(surveyItems);
    }

//    lazy open var consentDocument: ORKConsentDocument = {
//
//        // Setup the consent document
//        let consentDocument = ORKConsentDocument()
//        consentDocument.title = Localization.localizedString("SBA_CONSENT_TITLE")
//        consentDocument.signaturePageTitle = Localization.localizedString("SBA_CONSENT_TITLE")
//        consentDocument.signaturePageContent = Localization.localizedString("SBA_CONSENT_SIGNATURE_CONTENT")
//
//        // Add the signature
//        let signature = ORKConsentSignature(forPersonWithTitle: Localization.localizedString("SBA_CONSENT_PERSON_TITLE"), dateFormatString: nil, identifier: "participant")
//        consentDocument.addSignature(signature)
//
//        return consentDocument
//    }()
//
//    public convenience init?(jsonNamed: String) {
//        guard let json = SBAResourceFinder.shared.json(forResource: jsonNamed) else { return nil }
//        self.init(dictionary: json as NSDictionary)
//    }

//    public convenience init(dictionary: NSDictionary) {
//        self.init()
//
//        // Load the sections
//        var previousSectionType: SBAConsentSectionType?
//        if let sections = dictionary["sections"] as? [NSDictionary] {
//            self.consentDocument.sections = sections.map({ (dictionarySection) -> ORKConsentSection in
//                    let consentSection = dictionarySection.createConsentSection(previous: previousSectionType)
//            previousSectionType = dictionarySection.consentSectionType
//            return consentSection
//            })
//        }
//
//        // Load the document for the HTML content
//        if let properties = dictionary["documentProperties"] as? NSDictionary,
//                let documentHtmlContent = properties["htmlDocument"] as? String {
//            self.consentDocument.htmlReviewContent = SBAResourceFinder.shared.html(forResource: documentHtmlContent)
//        }
//
//        // After loading the consentDocument, map the steps
//        self.mapSteps(dictionary)
//    }

//    override open func createSurveyStepWithCustomType(_ inputItem: SBASurveyItem) -> ORKStep? {
//        guard let subtype = inputItem.surveyItemType.consentSubtype() else {
//            return super.createSurveyStepWithCustomType(inputItem)
//        }
//        switch (subtype) {
//
//            case .visual:
//            return ORKVisualConsentStep(identifier: inputItem.identifier,
//                    document: self.consentDocument)
//
//            case .sharingOptions:
//            return SBAConsentSharingStep(inputItem: inputItem)
//
//            case .review:
//            if let consentReview = inputItem as? SBAConsentReviewOptions
//                    , consentReview.usesDeprecatedOnboarding {
//                // If this uses the deprecated onboarding (consent review defined by ORKConsentReviewStep)
//                // then return that object type.
//                let signature = self.consentDocument.signatures?.first
//                signature?.requiresName = consentReview.requiresSignature
//                signature?.requiresSignatureImage = consentReview.requiresSignature
//                return ORKConsentReviewStep(identifier: inputItem.identifier,
//                        signature: signature,
//                        in: self.consentDocument)
//            }
//            else {
//                let review = inputItem as! SBAFormStepSurveyItem
//                let step = SBAConsentReviewStep(inputItem: review, inDocument: self.consentDocument, factory: self)
//                return step;
//            }
//        }
//    }

    /**
     * Return visual consent step
     */
    public Step visualConsentStep() {
//    open func visualConsentStep() -> ORKVisualConsentStep {
//        return self.steps?.find({ $0 is ORKVisualConsentStep }) as? ORKVisualConsentStep ??
//        ORKVisualConsentStep(identifier: SBAOnboardingSectionBaseType.consent.rawValue, document: self.consentDocument)
//    }
        return null;
    }

    /**
     * Return subtask step with only the steps required for reconsent
     */
    public Step reconsentStep() {
        //        open func reconsentStep() -> SBASubtaskStep {
//        // Strip out the registration steps
//        let steps = self.steps?.filter({ !isRegistrationStep($0) })
//        let task = SBANavigableOrderedTask(identifier: SBAOnboardingSectionBaseType.consent.rawValue, steps: steps)
//        return SBASubtaskStep(subtask: task)
//    }
        return null;
    }

    /**
     * Return subtask step with only the steps required for consent or reconsent on login
     */
    public Step loginConsentStep() {
//    open func loginConsentStep() -> SBASubtaskStep {
//        // Strip out the registration steps
//        let steps = self.steps?.filter({ !isRegistrationStep($0) })
//        let task = SBANavigableOrderedTask(identifier: SBAOnboardingSectionBaseType.consent.rawValue, steps: steps)
//        return SBAConsentSubtaskStep(subtask: task)
//    }
        return null;
    }

    boolean isRegistrationStep(Step step) {
        // TODO: return (step is SBARegistrationStep) || (step is ORKRegistrationStep) || (step is SBAExternalIDStep)
        return false;
    }

    /**
     * Return subtask step with only the steps required for initial registration
     */
    public Step registrationConsentStep() {
        //    open func registrationConsentStep() -> SBASubtaskStep {
//        // Strip out the reconsent steps
//        let steps = self.steps?.filter({ (step) -> Bool in
//                // If this is a step that conforms to the custom step protocol and the custom step type is
//                // a reconsent subtype, then this is not to be included in the registration steps
//        if let customStep = step as? SBACustomTypeStep, let customType = customStep.customTypeIdentifier, customType.hasPrefix("reconsent") {
//            return false
//        }
//        return true
//        })
//        let task = SBANavigableOrderedTask(identifier: SBAOnboardingSectionBaseType.consent.rawValue, steps: steps)
//        return SBASubtaskStep(subtask: task)
//    }
        return null;
    }
}
