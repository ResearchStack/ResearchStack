package org.researchstack.backbone.onboarding;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.survey.SurveyItem;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by TheMDP on 1/2/17.
 */

public class OnboardingSectionAdapter implements JsonDeserializer<OnboardingSection> {

    private OnboardingManager.AdapterContextProvider adapterProvider;

    /**
     * @param adapterProvider should provide Gson and Context, to avoid storing them as member variables in this class
     */
    public OnboardingSectionAdapter(OnboardingManager.AdapterContextProvider adapterProvider) {
        this.adapterProvider = adapterProvider;
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
            ResourcePathManager.Resource resource = ResourceManager.getInstance().getResource(convertedResourceName);
            if (resource == null) {
                throw new IllegalStateException("Could not find resource with name " + convertedResourceName +
                "to load in onboarding JSON.  Make sure you add it with ResourceManager.addResource(), so this" +
                "class knows where to load it from");
            }
            String resourceJson = ResourceManager.getResourceAsString(adapterProvider.getContext(),
                    ResourceManager.getInstance().generatePath(resource.getType(), resource.getName()));
            JsonParser parser = new JsonParser();
            json = parser.parse(resourceJson);
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
}
