package org.researchstack.backbone.model.survey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.researchstack.backbone.model.survey.factory.SurveyFactory;
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
            // Some JSON can be malformed and there is no type, only identifier
            // In that case let's try and still parse it by setting the type as the identifier;
            if (typeJson != null) {
                customTypeString = typeJson.getAsString();
            } else { // use "identifier" string
                customTypeString = jsonObject.get(SurveyItem.IDENTIFIER_GSON).getAsString();
            }
        }

        SurveyItem item = null;

        switch (surveyItemType) {
            case INSTRUCTION:
            case INSTRUCTION_COMPLETION:
                item = context.deserialize(json, InstructionSurveyItem.class);
                break;
            case SUBTASK:
                item = context.deserialize(json, SubtaskQuestionSurveyItem.class);
                break;
            case QUESTION_COMPOUND:
                item = context.deserialize(json, CompoundQuestionSurveyItem.class);
                break;
            case QUESTION_TOGGLE:
                item = context.deserialize(json, ToggleQuestionSurveyItem.class);
                break;
            case QUESTION_BOOLEAN:
                item = context.deserialize(json, BooleanQuestionSurveyItem.class);
                break;
            case QUESTION_DECIMAL:
                item = context.deserialize(json, FloatRangeSurveyItem.class);
                break;
            case QUESTION_INTEGER:
                item = context.deserialize(json, IntegerRangeSurveyItem.class);
                break;
            case QUESTION_DURATION:
                break;
            case QUESTION_SCALE:
                item = context.deserialize(json, ScaleQuestionSurveyItem.class);
                break;
            case QUESTION_TEXT:
                item = context.deserialize(json, CompoundQuestionSurveyItem.class);
                break;
            case QUESTION_DATE:
            case QUESTION_DATE_TIME:
            case QUESTION_TIME:
                item = context.deserialize(json, DateRangeSurveyItem.class);
                break;
            case QUESTION_MULTIPLE_CHOICE:
            case QUESTION_SINGLE_CHOICE:
                item = context.deserialize(json, ChoiceQuestionSurveyItem.class);
                break;
            case QUESTION_TIMING_RANGE:
                item = context.deserialize(json, TimingRangeQuestionSurveyItem.class);
                break;
            case CONSENT_SHARING_OPTIONS:
                item = context.deserialize(json, ConsentSharingOptionsSurveyItem.class);
                break;
            case CONSENT_REVIEW:
                item = context.deserialize(json, ConsentReviewSurveyItem.class);
                break;
            case CONSENT_VISUAL:
                break;
            case RE_CONSENT:
                item = context.deserialize(json, InstructionSurveyItem.class);
                break;
            case ACCOUNT_REGISTRATION:
            case ACCOUNT_LOGIN:
            case ACCOUNT_PROFILE:
                item = context.deserialize(json, ProfileSurveyItem.class);
                break;
            case ACCOUNT_COMPLETION:
            case ACCOUNT_EMAIL_VERIFICATION:
                item = context.deserialize(json, InstructionSurveyItem.class);
                break;
            case ACCOUNT_DATA_GROUPS:
            case ACCOUNT_EXTERNAL_ID:
            case ACCOUNT_PERMISSIONS:
            case PASSCODE:
                break;
            case SHARE_THE_APP:
                item = context.deserialize(json, InstructionSurveyItem.class);
                break;
            case ACTIVE_STEP:
                item = context.deserialize(json, ActiveStepSurveyItem.class);
                break;
            case CUSTOM:
                item = context.deserialize(json, getCustomClass(customTypeString, json));
                item.type = surveyItemType; // need to set CUSTOM type for surveyItem, since it is a special case
                item.setCustomTypeValue(customTypeString);
                break;
        }

        if (item == null) {
            item = context.deserialize(json, BaseSurveyItem.class);
            item.type = surveyItemType;
        }

        item.setRawJson(json.toString());

        return item;
    }

    /**
     * This can be overridden by subclasses to provide custom survey item deserialization
     * the default deserialization is always an instruction survey item
     * @param customType used to map to different types of survey items
     * @param json if customType is not enough, you can use the JsonElement to determine how to parse
     *             it's contents by peeking at it's variables
     * @return type of survey item to create from the custom class
     */
    public Class<? extends SurveyItem> getCustomClass(String customType, JsonElement json) {
        return BaseSurveyItem.class;
    }
}
