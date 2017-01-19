package org.researchstack.backbone.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.researchstack.backbone.onboarding.ResourceNameToStringConverter;

import java.lang.reflect.Type;

/**
 * Created by TheMDP on 1/5/17.
 */

public class ConsentSectionAdapter implements JsonDeserializer<ConsentSection> {

    /**
     * Used to convert ConsentSections
     */
    Context androidContext;
    ResourceNameToStringConverter resourceConverter;

    public ConsentSectionAdapter(Context context, ResourceNameToStringConverter convertor) {
        androidContext = context;
        resourceConverter = convertor;
    }

    @Override
    public ConsentSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject =  json.getAsJsonObject();
        JsonElement typeJson = jsonObject.get(ConsentSection.SECTION_TYPE_GSON);

        ConsentSection.Type type = context.deserialize(typeJson, ConsentSection.Type.class);
        // This was a custom ConsentSection Type
        if (type == null) {
            type = ConsentSection.Type.Custom;
        }

        // This will avoid the infinite loop of using the param json context,
        // Since this wont have the ConsentSectionAdapter registered
        Gson gson = new Gson();
        ConsentSection consentSection = gson.fromJson(json, ConsentSection.class);
        consentSection.setType(type);

        // If we have a non-custom type, we can auto-populate title, learn more, and image name
        // if they weren't specifically provided by the JSON
        if (type != ConsentSection.Type.Custom) {
            if (androidContext != null) {
                if (consentSection.getTitle() == null && type.getTitleResId() != ConsentSection.UNDEFINED_RES) {
                    consentSection.setTitle(androidContext.getString(type.getTitleResId()));
                }
                if (consentSection.getCustomLearnMoreButtonTitle() == null && type.getMoreInfoResId() != ConsentSection.UNDEFINED_RES) {
                    consentSection.setCustomLearnMoreButtonTitle(androidContext.getString(type.getMoreInfoResId()));
                }
            }
            if (consentSection.getCustomImageName() == null) {
                consentSection.setCustomImageName(type.getImageName());
            }
        } else {
            consentSection.customTypeIdentifier = typeJson.getAsString();
        }

        // Convert HTML content from filename to actual HTML content
        if (resourceConverter != null) {
            String htmlContent = resourceConverter.getHtmlStringForResourceName(consentSection.getHtmlContent());
            consentSection.setHtmlContent(htmlContent);
        }

        return consentSection;
    }
}
