package org.researchstack.backbone.model.survey.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.ConsentReviewSurveyItem;
import org.researchstack.backbone.model.survey.ConsentSharingOptionsSurveyItem;
import org.researchstack.backbone.model.survey.CustomInstructionSurveyItem;
import org.researchstack.backbone.model.survey.CustomSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.onboarding.ResourceNameToStringConverter;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentReviewSubstepListStep;
import org.researchstack.backbone.step.ConsentSharingStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.ConsentSubtaskStep;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.CustomInstructionStep;
import org.researchstack.backbone.step.CustomStep;
import org.researchstack.backbone.step.RegistrationStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.step.NavigationSubtaskStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 12/29/16.
 */

public class ConsentDocumentFactory extends SurveyFactory {

    public static final String RECONSENT_IDENTIFIER_PREFIX = "reconsent";
    public static final String CONSENT_SIGNATURE_IDENTIFIER = "consentSignature";
    public static final String CONSENT_REVIEW_PROFILE_IDENTIFIER = "consentReviewProfile";
    public static final String CONSENT_SHARING_IDENTIFIER = "consentSharingOptions";
    public static final String CONSENT_REVIEW_IDENTIFIER = "consentReview";

    ConsentDocument consentDocument;
    ResourceNameToStringConverter resourceConverter;

    /**
     * @param context used for building steps with resources
     * @param surveyItems surveyItems to convert to steps
     * @param document consent document, already deserialized
     * @param convertor used to convert html filenames to html content for VisualConsentStep
     */
    public ConsentDocumentFactory(
            Context context,
            List<SurveyItem> surveyItems,
            ConsentDocument document,
            ResourceNameToStringConverter convertor)
    {
        super();
        consentDocument = document;
        resourceConverter = convertor;
        steps = createSteps(context, surveyItems, false);
    }

    /**
     * @param context used for building steps with resources
     * @param surveyItems surveyItems to convert to steps
     * @param document consent document, already deserialized
     * @param convertor used to convert html filenames to html content for VisualConsentStep
     * @param customStepCreator override to control step creation from custom survey items
     */
    public ConsentDocumentFactory(
            Context context,
            List<SurveyItem> surveyItems,
            ConsentDocument document,
            ResourceNameToStringConverter convertor,
            CustomStepCreator customStepCreator)
    {
        super();
        consentDocument = document;
        resourceConverter = convertor;
        super.customStepCreator = customStepCreator;
        steps = createSteps(context, surveyItems, false);
    }

    @Override
    public List<Step> createSteps(Context context, List<SurveyItem> surveyItems, boolean isSubtaskStep) {
        List<Step> steps = new ArrayList<>();
        for (SurveyItem item : surveyItems) {
            switch (item.type) {
                case CONSENT_REVIEW:
                    if (!(item instanceof ConsentReviewSurveyItem)) {
                        throw new IllegalStateException("Error in json parsing, CONSENT_REVIEW types must be ConsentReviewSurveyItem");
                    }
                    if (consentDocument == null) {
                        throw new IllegalStateException("Consent document cannot be null!");
                    }
                    steps.add(createConsentReviewSteps(context, (ConsentReviewSurveyItem)item));
                    break;
                case CONSENT_SHARING_OPTIONS:
                    if (!(item instanceof ConsentSharingOptionsSurveyItem)) {
                        throw new IllegalStateException("Error in json parsing, CONSENT_SHARING_OPTIONS types must be ConsentSharingOptionsSurveyItem");
                    }
                    steps.add(createConsentSharingStep(context, (ConsentSharingOptionsSurveyItem)item));
                    break;
                case CONSENT_VISUAL:
                    if (consentDocument == null) {
                        throw new IllegalStateException("Consent document cannot be null!");
                    }
                    steps.add(createConsentVisualSteps(item, consentDocument.getSections()));
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
     * SignatureStep, but always a consent doc review step
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item ConsentReviewSurveyItem used to create steps
     * @return ConsentReviewSubstepListStep used for consent review
     */
    public ConsentReviewSubstepListStep createConsentReviewSteps(Context context, ConsentReviewSurveyItem item) {
        List<Step> stepList = new ArrayList<>();

        if (item.items == null) {
            // Default to name and birthdate
            item.items = new ArrayList<>();
            item.items.add(ProfileInfoOption.NAME.getIdentifier());
            item.items.add(ProfileInfoOption.BIRTHDATE.getIdentifier());
        }

        // This is a deprecated way of supplying requiresName and requiresBirthdate
        // But we are going to support it for now, but use "name", and "birthdate" moving forward
        if (consentDocument.getDocumentProperties() != null) {
            // Check if birthdate and name are required, and remove them if they are not
            if (!consentDocument.getDocumentProperties().requiresName() && item.items != null) {
                item.items.remove(ProfileInfoOption.NAME.getIdentifier());
            }
            if (!consentDocument.getDocumentProperties().requiresBirthdate()) {
                item.items.remove(ProfileInfoOption.BIRTHDATE.getIdentifier());
            }
        }

        // Only create the profile step if there is at least one consent required item
        if (item.items != null && !item.items.isEmpty()) {
            String oldIdentifier = new String(item.identifier);
            item.identifier = CONSENT_REVIEW_PROFILE_IDENTIFIER;
            // This will create a profile step with name, and birthday, or w/e is in JSON
            stepList.add(super.createProfileStep(context, item));
            item.identifier = oldIdentifier;
        }

        if (consentDocument.getDocumentProperties() != null &&
            consentDocument.getDocumentProperties().requiresSignature())
        {
            // Add Consent Signature
            stepList.add(createConsentSignatureStep(context, item));
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

        return new ConsentReviewSubstepListStep(item.identifier, stepList);
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item ConsentSharingOptionsSurveyItem that may have Sharing option choices
     * @return ConsentSharingStep for designating how to share the user's data
     */
    public ConsentSharingStep createConsentSharingStep(Context context, ConsentSharingOptionsSurveyItem item) {
        AnswerFormat format = null;
        if (item.items != null && !item.items.isEmpty()) {
            Choice[] choices = item.items.toArray(new Choice[item.items.size()]);
            format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, choices);
        } else if (item.investigatorLongDescription != null) {
            String shareWidely = context.getString(R.string.rsb_consent_share_widely, item.investigatorLongDescription);
            Choice<Boolean> shareWidelyChoice = new Choice<>(shareWidely, true);
            String shareRestricted = context.getString(R.string.rsb_consent_share_only, item.investigatorLongDescription);
            Choice<Boolean> shareRestrictedChoice = new Choice<>(shareRestricted, false);
            Choice[] choices = new Choice[] { shareWidelyChoice, shareRestrictedChoice };
            format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, choices);
        }
        ConsentSharingStep step;
        if (format == null) {
            step = new ConsentSharingStep(CONSENT_SHARING_IDENTIFIER);
        } else {
            step = new ConsentSharingStep(CONSENT_SHARING_IDENTIFIER, item.title, format);
        }

        if (item.title == null) {
            step.setTitle(context.getString(R.string.rsb_consent_share_title));
        }

        if (item.text != null) {
            step.setText(item.text);
        } else if (item.investigatorLongDescription != null) {
            if (item.learnMoreHTMLContentURL != null) {
                step.setText(context.getString(R.string.rsb_consent_share_description,
                        item.investigatorLongDescription,
                        item.learnMoreHTMLContentURL));
            }
        }

        return step;
    }

    /**
     * @param item the survey item to be converted into a step
     * @param sections used to create the ConsentVisualSteps
     * @return Ordered list of ConsentVisualSteps
     */
    public SubtaskStep createConsentVisualSteps(SurveyItem item, List<ConsentSection> sections) {
        List<Step> stepList = new ArrayList<>();
        int customIdx = 1;
        for (ConsentSection section : consentDocument.getSections()) {
            // OnlyInDocument is used to create the ConsentDocumentStep later on
            if (section.getType() != ConsentSection.Type.OnlyInDocument) {
                ConsentVisualStep step;
                if (section.getType() == ConsentSection.Type.Custom) {
                    step = new ConsentVisualStep(section.getTypeIdentifier() + customIdx);
                    customIdx++;
                } else {
                    step = new ConsentVisualStep(section.getTypeIdentifier());
                }
                step.setSection(section);
                stepList.add(step);
            }
        }
        return new SubtaskStep(item.getTypeIdentifier(), stepList);
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item used to create signature step
     * @return ConsentSignatureStep
     */
    public ConsentSignatureStep createConsentSignatureStep(Context context, ConsentReviewSurveyItem item) {
        ConsentSignatureStep step = new ConsentSignatureStep(CONSENT_SIGNATURE_IDENTIFIER, item.title, item.text);
        if (item.title == null) {
            step.setTitle(context.getString(R.string.rsb_consent_signature_title));
        }
        if (item.text == null) {
            step.setTitle(context.getString(R.string.rsb_consent_signature_instruction));
        }
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
        return new NavigationSubtaskStep(OnboardingSection.CONSENT_IDENTIFIER, steps);
    }

    boolean isRegistrationStep(Step step) {
        // TODO: add ExternalIdStep here when it is created
        return step instanceof RegistrationStep; // || step instanceof ExternalIdStep;
    }

    /**
     * @return subtask step with only the steps required for consent or reconsent on login
     */
    public Step loginConsentStep() {
        // Strip out the registration steps, and only leave the consent steps
        List<Step> steps = new ArrayList<>();
        for (Step step : getSteps()) {
            if (!isRegistrationStep(step)) {
                steps.add(step);
            }
        }
        // Consent subtask step will be skipped if user has already consented
        return new ConsentSubtaskStep(OnboardingSection.CONSENT_IDENTIFIER, steps);
    }

    /**
     * Return subtask step with only the steps required for initial registration
     * @return the consent step that is applicable during registration
     */
    public Step registrationConsentStep() {
        // If this is a step that conforms to the custom step protocol and the custom step type is
        // a reconsent subtype, then this is not to be included in the registration steps
        // Strip out the registration steps, and only leave the consent steps
        List<Step> steps = new ArrayList<>();
        for (Step step : getSteps()) {
            if (!(step instanceof CustomStep)) {
                steps.add(step);
            } else {
                CustomStep customStep = (CustomStep)step;
                // Do not include re-consent step in regular registration consent step
                if (!customStep.getCustomTypeIdentifier().startsWith(RECONSENT_IDENTIFIER_PREFIX)) {
                    steps.add(step);
                }
            }
        }
        return new NavigationSubtaskStep(OnboardingSection.CONSENT_IDENTIFIER, steps);
    }

    /**
     * @param item InstructionSurveyItem from JSON
     * @return valid CustomStep matching the InstructionSurveyItem
     */
    @Override
    public CustomStep createCustomStep(CustomSurveyItem item) {
        if (item instanceof CustomInstructionSurveyItem) {
            return createCustomInstructionStep((CustomInstructionSurveyItem)item);
        } else {
            return super.createCustomStep(item);
        }
    }

    /**
     * @param item CustomInstructionSurveyItem from JSON
     * @return valid CustomInstructionStep matching the CustomInstructionSurveyItem
     */
    CustomInstructionStep createCustomInstructionStep(CustomInstructionSurveyItem item) {
        CustomInstructionStep step = new CustomInstructionStep(item.identifier, item.title, item.text, item.getTypeIdentifier());
        step.setFootnote(item.footnote);
        step.setNextStepIdentifier(item.nextIdentifier);
        step.setMoreDetailText(item.detailText);
        step.setImage(item.image);
        step.setIconImage(item.iconImage);
        return step;
    }
}
