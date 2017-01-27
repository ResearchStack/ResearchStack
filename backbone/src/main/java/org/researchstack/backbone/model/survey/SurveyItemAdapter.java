package org.researchstack.backbone.model.survey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.researchstack.backbone.step.OnboardingCompletionStep;

import java.lang.reflect.Type;

/**
 * Created by TheMDP on 1/2/17.
 *
 * This class is the deserializer for SurveyItem classes
 * It looks at the "type" field, attempts to map it to this library's pre-defined types
 * and if it does not find it, creates a custom survey item
 * the class of the custom survey item can easily be controlled by overriding this
 * adapter, and overriding the method getCustomClass
 *
 * To go even further and change the mapping of the custom survey item to a custom step,
 * you should override SurveyFactory's method public Step createCustomStep(SurveyItem item)
 * which is the go to for converting a survey item to a step
 */

public class SurveyItemAdapter implements JsonDeserializer<SurveyItem> {

    @Override
    public SurveyItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject =  json.getAsJsonObject();

        JsonElement typeJson = jsonObject.get(SurveyItem.TYPE_GSON);
        SurveyItemType surveyItemType = context.deserialize(typeJson, SurveyItemType.class);

        // This was a custom survey item type
        // For instance, "reconsent.instruction" is a subtask consent type
        // That will be dealt with by a custom ConsentDocumentSurveyFactory
        String customTypeString = null;
        if (surveyItemType == null) {
            surveyItemType = SurveyItemType.CUSTOM;
            customTypeString = typeJson.getAsString();
        }

        switch (surveyItemType) {
            case INSTRUCTION:
            case INSTRUCTION_COMPLETION:
                return context.deserialize(json, InstructionSurveyItem.class);
            case SUBTASK:
                return context.deserialize(json, SubtaskQuestionSurveyItem.class);
            case QUESTION_COMPOUND:
                return context.deserialize(json, CompoundQuestionSurveyItem.class);
            case QUESTION_TOGGLE:
                return context.deserialize(json, ToggleQuestionSurveyItem.class);
            case QUESTION_BOOLEAN:
                return context.deserialize(json, BooleanQuestionSurveyItem.class);
            case QUESTION_DECIMAL:
                return context.deserialize(json, FloatRangeSurveyItem.class);
            case QUESTION_INTEGER:
                return context.deserialize(json, IntegerRangeSurveyItem.class);
            case QUESTION_DURATION:
                break;
            case QUESTION_SCALE:
                return context.deserialize(json, ScaleQuestionSurveyItem.class);
            case QUESTION_TEXT:
                return context.deserialize(json, CompoundQuestionSurveyItem.class);
            case QUESTION_DATE:
            case QUESTION_DATE_TIME:
            case QUESTION_TIME:
                return context.deserialize(json, DateRangeSurveyItem.class);
            case QUESTION_MULTIPLE_CHOICE:
            case QUESTION_SINGLE_CHOICE:
            case QUESTION_TIMING_RANGE:
                return context.deserialize(json, ChoiceQuestionSurveyItem.class);
            case CONSENT_SHARING_OPTIONS:
                return context.deserialize(json, ConsentSharingOptionsSurveyItem.class);
            case CONSENT_REVIEW:
                return context.deserialize(json, ConsentReviewSurveyItem.class);
            case CONSENT_VISUAL:
                break;
            case ACCOUNT_REGISTRATION:
            case ACCOUNT_LOGIN:
            case ACCOUNT_PROFILE:
                return context.deserialize(json, ProfileSurveyItem.class);
            case ACCOUNT_COMPLETION:
            case ACCOUNT_EMAIL_VERIFICATION:
                return context.deserialize(json, InstructionSurveyItem.class);
            case ACCOUNT_DATA_GROUPS:
            case ACCOUNT_EXTERNAL_ID:
            case ACCOUNT_PERMISSIONS:
                break;
            case PASSCODE:
                break;
            case SHARE_THE_APP:
                return context.deserialize(json, InstructionSurveyItem.class);
            case CUSTOM:
                CustomSurveyItem item = context.deserialize(json, getCustomClass(customTypeString));
                item.type = surveyItemType; // need to set CUSTOM type for surveyItem, since it is a special case
                item.customSurveyItemIdentifer = customTypeString;
                return item;
        }

        SurveyItem surveyItem = context.deserialize(json, BaseSurveyItem.class);
        surveyItem.type = surveyItemType;
        return surveyItem;
    }

    /**
     * This can be overridden by subclasses to provide custom survey item deserialization
     * the default deserialization is always an instruction survey item
     * @param customType used to map to different types of survey items
     * @return type of survey item to create from the custom class
     */
    public Class<? extends CustomSurveyItem> getCustomClass(String customType) {
        if (customType.endsWith(".instruction")) {
            return CustomInstructionSurveyItem.class;
        }
        return CustomSurveyItem.class;
    }
}
