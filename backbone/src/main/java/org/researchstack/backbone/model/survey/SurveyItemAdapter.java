package org.researchstack.backbone.model.survey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by TheMDP on 1/2/17.
 */

public class SurveyItemAdapter implements JsonDeserializer<SurveyItem> {

    @Override
    public SurveyItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject =  json.getAsJsonObject();

        SurveyItemType surveyItemType = context.deserialize(
                jsonObject.get(SurveyItem.TYPE_GSON), SurveyItemType.class);

        // This was a custom survey item type
        // For instance, "reconsent.instruction" is a subtask consent type
        // That will be dealt with by a custom ConsentDocumentSurveyFactory
        if (surveyItemType == null) {
            surveyItemType = SurveyItemType.CUSTOM;
            surveyItemType.setCustomValue(jsonObject.get(SurveyItem.TYPE_GSON).getAsString());
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
            case CUSTOM:
                InstructionSurveyItem item = context.deserialize(json, InstructionSurveyItem.class);
                item.type = surveyItemType; // need to set CUSTOM type for surveyItem, since it is a special case
                return item;
        }

        SurveyItem surveyItem = context.deserialize(json, BaseSurveyItem.class);
        surveyItem.type = surveyItemType;
        return surveyItem;
    }
}
