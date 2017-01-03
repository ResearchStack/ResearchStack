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
            case QUESTION_BOOLEAN:
            case QUESTION_COMPOUND:
            case QUESTION_DATE:
            case QUESTION_DATE_TIME:
            case QUESTION_DECIMAL:
            case QUESTION_DURATION:
            case QUESTION_INTEGER:
            case QUESTION_MULTIPLE_CHOICE:
            case QUESTION_SCALE:
            case QUESTION_TEXT:
            case QUESTION_TIME:
            case QUESTION_TIMING_RANGE:
                return context.deserialize(json, QuestionSurveyItem.class);
            case QUESTION_TOGGLE:
                return context.deserialize(json, ToggleQuestionSurveyItem.class);
            case QUESTION_SINGLE_CHOICE:
                return context.deserialize(json, SingleChoiceTextQuestionSurveyItem.class);
            case CONSENT_SHARING_OPTIONS:
                return context.deserialize(json, ConsentSharingOptionsSurveyItem.class);
            case CONSENT_REVIEW:
                return context.deserialize(json, ConsentReviewSurveyItem.class);
            case CONSENT_VISUAL:
                break;
            case ACCOUNT_REGISTRATION:
                return context.deserialize(json, RegistrationSurveyItem.class);
            case ACCOUNT_LOGIN:
            case ACCOUNT_EMAIL_VERIFICATION:
            case ACCOUNT_EXTERNAL_ID:
            case ACCOUNT_PERMISSIONS:
            case ACCOUNT_COMPLETION:
            case ACCOUNT_DATA_GROUPS:
            case ACCOUNT_PROFILE:
            case PASSCODE:
            case CUSTOM:
                break;
        }

        SurveyItem surveyItem = context.deserialize(json, BaseSurveyItem.class);
        surveyItem.type = surveyItemType;
        return surveyItem;
    }
}
