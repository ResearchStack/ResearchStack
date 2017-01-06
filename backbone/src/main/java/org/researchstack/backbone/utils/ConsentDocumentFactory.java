package org.researchstack.backbone.model.survey.factory;

import android.content.Context;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.ConsentReviewSurveyItem;
import org.researchstack.backbone.model.survey.ConsentSharingOptionsSurveyItem;
import org.researchstack.backbone.model.survey.ProfileSurveyItem;
import org.researchstack.backbone.model.survey.QuestionSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.onboarding.OnboardingSectionType;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSharingStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.CustomStep;
import org.researchstack.backbone.step.RegistrationStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.step.navigation.NavigationSubtaskStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 12/29/16.
 */

public class ConsentDocumentFactory extends SurveyFactory {
    
    public static final String RECONSENT_IDENTIFIER_PREFIX = "reconsent";
    
    ConsentDocument consentDocument;
    
    public ConsentDocumentFactory(Context context, List<SurveyItem> surveyItems, ConsentDocument document) {
        super();
        consentDocument = document;
        steps = createSteps(context, surveyItems, false);
    }
    
    ConsentDocumentFactory(Context context, List<SurveyItem> surveyItems) {
        super(context, surveyItems);
    }
    
    @Override
    public List<Step> createSteps(Context context, List<SurveyItem> surveyItems, boolean isSubtaskStep) {
        List<Step> steps = new ArrayList<>();
        for (SurveyItem item : surveyItems) {
            switch (item.type) {
                    // Consent review actually consists of two steps,
                    // the name, and birthdate profile steps
                    // and the consent signature step,
                    // so, instead of making a subtask step, just add multiple steps
                case CONSENT_REVIEW:
                    if (!(item instanceof ConsentReviewSurveyItem)) {
                        throw new IllegalStateException("Error in json parsing, CONSENT_REVIEW types must be ConsentReviewSurveyItem");
                    }
                    if (consentDocument == null) {
                        throw new IllegalStateException("Consent document cannot be null!");
                    }
                    steps.addAll(createConsentReviewSteps(context, (ConsentReviewSurveyItem)item));
                    break;
                case CONSENT_SHARING_OPTIONS:
                    if (!(item instanceof ConsentSharingOptionsSurveyItem)) {
                        throw new IllegalStateException("Error in json parsing, CONSENT_SHARING_OPTIONS types must be ConsentSharingOptionsSurveyItem");
                    }
                    steps.add(createConsentSharingStep((ConsentSharingOptionsSurveyItem)item));
                    break;
                case CONSENT_VISUAL:
                    if (consentDocument == null) {
                        throw new IllegalStateException("Consent document cannot be null!");
                    }
                    steps.addAll(createConsentVisualSteps(consentDocument.getSections()));
                    break;
                default:
                    steps.add(super.createSurveyStep(context, item, isSubtaskStep));
                    break;
            }
        }
        
        return steps;
    }
    
    /**
     * Creates consent review steps, which can be a total of name, birthdate step,
     * SignatureStep, and a consent doc review step
     * @param context
     * @param item ConsentReviewSurveyItem used to create steps
     * @return
     */
    public List<Step> createConsentReviewSteps(Context context, ConsentReviewSurveyItem item) {
        List<Step> stepList = new ArrayList<>();
        
        // Check if birthdate and name are required, and remove them if they are not
        if (!consentDocument.getRequiresBirthdate()) {
            item.items.remove(ProfileInfoOption.BIRTHDATE.getIdentifier());
        }
        if (!consentDocument.getRequiresName()) {
            item.items.remove(ProfileInfoOption.NAME.getIdentifier());
        }
        // Only create the profile step if there is at least one consent required item
        if (!item.items.isEmpty()) {
            // This will create a profile step with name, and birthday, or w/e is in JSON
            stepList.add(super.createProfileStep(context, item));
        }
        
        if (consentDocument.getRequiresSignature()) {
            // Add Consent Signature
            stepList.add(createConsentSignatureStep(item));
        }
        
        // Find the html consent review doc, since it can come from a deprecated or modern place
        String htmlConsentDoc = consentDocument.getHtmlReviewContent();  // deprecated way
        if (htmlConsentDoc == null) {  // more modern way
            for (ConsentSection section : consentDocument.getSections()) {
                if (section.getType() == ConsentSection.Type.OnlyInDocument) {
                    htmlConsentDoc = section.getHtmlContent();
                }
            }
        }
        // Add Consent document review step here
        ConsentDocumentStep docReviewStep = new ConsentDocumentStep(item.identifier);
        docReviewStep.setConsentHTML(htmlConsentDoc);
        stepList.add(docReviewStep);
        
        return stepList;
    }
    
    /**
     * @param item ConsentSharingOptionsSurveyItem that may have Sharing option choices
     * @return ConsentSharingStep for designating how to share the user's data
     */
    public ConsentSharingStep createConsentSharingStep(ConsentSharingOptionsSurveyItem item) {
        AnswerFormat format = null;
        if (item.items != null && !item.items.isEmpty()) {
            Choice[] choices = item.items.toArray(new Choice[item.items.size()]);
            format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, choices);
        }
        if (format == null) {
            return new ConsentSharingStep(item.identifier);
        } else {
            return new ConsentSharingStep(item.identifier, item.title, format);
        }
    }
    
    /**
     * @param sections used to create the ConsentVisualSteps
     * @return Ordered list of ConsentVisualSteps
     */
    public List<ConsentVisualStep> createConsentVisualSteps(List<ConsentSection> sections) {
        List<ConsentVisualStep> stepList = new ArrayList<>();
        for (ConsentSection section : consentDocument.getSections()) {
            // OnlyInDocument is used to create the ConsentDocumentStep later on
            if (section.getType() != ConsentSection.Type.OnlyInDocument) {
                ConsentVisualStep step = new ConsentVisualStep(section.getType().getIdentifier());
                step.setSection(section);
                stepList.add(step);
            }
        }
        return stepList;
    }
    
    /**
     * @param item used to create signature step
     * @return ConsentSignatureStep
     */
    public ConsentSignatureStep createConsentSignatureStep(ConsentReviewSurveyItem item) {
        ConsentSignatureStep step = new ConsentSignatureStep(item.identifier);
        return step;
    }
    
    /**
     * After all survey items have been processed into steps, use this method to get all
     * ConsentVisualSteps
     * @return list of all the ConsentVisualStep
     */
    public List<Step> visualConsentSteps() {
        List<Step> steps = new ArrayList<>();
        for (Step step : getSteps()) {
            if (step instanceof ConsentVisualStep) {
                steps.add(step);
            }
        }
        return steps;
    }
    
    public SubtaskStep reconsentStep() {
        // Strip out the registration steps, and only leave the consent steps
        List<Step> steps = new ArrayList<>();
        for (Step step : getSteps()) {
            if (!isRegistrationStep(step)) {
                steps.add(step);
            }
        }
        return new NavigationSubtaskStep(OnboardingSectionType.CONSENT.getIdentifier(), steps);
    }
    
    boolean isRegistrationStep(Step step) {
        // TODO: add ExternalIdStep here when it is created
        return step instanceof RegistrationStep; // || step instanceof ExternalIdStep;
    }
    
    /**
     * Return subtask step with only the steps required for consent or reconsent on login
     */
    public Step loginConsentStep() {
        // Strip out the registration steps, and only leave the consent steps
        List<Step> steps = new ArrayList<>();
        for (Step step : getSteps()) {
            if (!isRegistrationStep(step)) {
                steps.add(step);
            }
        }
        return new NavigationSubtaskStep(OnboardingSectionType.LOGIN.getIdentifier(), steps);
    }
    
    /**
     * Return subtask step with only the steps required for initial registration
     */
    public Step registrationConsentStep() {
        // If this is a step that conforms to the custom step protocol and the custom step type is
        // a reconsent subtype, then this is not to be included in the registration steps
        // Strip out the registration steps, and only leave the consent steps
        List<Step> steps = new ArrayList<>();
        for (Step step : getSteps()) {
            if (!(step instanceof CustomStep) &&
                ((CustomStep)step).getCustomTypeIdentifier().startsWith(RECONSENT_IDENTIFIER_PREFIX))
            {
                steps.add(step);
            }
        }
        return new NavigationSubtaskStep(OnboardingSectionType.CONSENT.getIdentifier(), steps);
    }
}
