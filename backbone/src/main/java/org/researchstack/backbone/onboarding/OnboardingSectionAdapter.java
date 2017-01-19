package org.researchstack.backbone.onboarding;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.survey.SurveyItem;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by TheMDP on 1/2/17.
 */

public class OnboardingSectionAdapter implements JsonDeserializer<OnboardingSection> {

    ResourceNameToStringConverter mResourceNameConverter;

    public OnboardingSectionAdapter(ResourceNameToStringConverter converter) {
        mResourceNameConverter = converter;
    }

    @Override
    public OnboardingSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonElement typeJson = json.getAsJsonObject().get(OnboardingSection.ONBOARDING_TYPE_GSON);
        OnboardingSectionType type = context.deserialize(typeJson, OnboardingSectionType.class);

        // setup custom type
        if (type == null) {
            type = OnboardingSectionType.CUSTOM;
        }

        JsonElement resourceName = json.getAsJsonObject().get(OnboardingSection.ONBOARDING_RESOURCE_NAME_GSON);
        if (resourceName != null) {
            // Android does not support spaces or uppercase letters for resource names
            // So convert all of these before we request the resource name
            String convertedResourceName = resourceName.getAsString().replace(" ", "_").toLowerCase();
            String resourceJson = mResourceNameConverter.getJsonStringForResourceName(convertedResourceName);
            JsonParser parser = new JsonParser();
            JsonElement nestedSectionElement = parser.parse(resourceJson);
            json = nestedSectionElement;
        }

        OnboardingSection section;
        // Consent section also has a consent document with it, try and parse it if we have that type
        if (type == OnboardingSectionType.CONSENT) {
            ConsentOnboardingSection consentSection = new ConsentOnboardingSection();
            consentSection.consentDocument = context.deserialize(json, ConsentDocument.class);
            section = consentSection;
        } else if (type == OnboardingSectionType.CUSTOM) {
            section = new CustomOnboardingSection(typeJson.getAsString());
        } else {  // otherwise make the base onboarding section class
            section = new OnboardingSection();
        }
        section.onboardingType = type;

        List<SurveyItem> surveyItems = context.deserialize(
                json.getAsJsonObject().get(OnboardingSection.ONBOARDING_SURVEY_ITEMS_GSON),
                new TypeToken<List<SurveyItem>>() {}.getType());
        section.surveyItems = surveyItems;

        return section;
    }

    public interface GsonProvider {
        Gson getGson();
    }
}
