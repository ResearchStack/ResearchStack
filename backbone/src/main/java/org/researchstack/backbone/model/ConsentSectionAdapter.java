package org.researchstack.backbone.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by TheMDP on 1/5/17.
 */

public class ConsentSectionAdapter implements JsonDeserializer<ConsentSection> {
    @Override
    public ConsentSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject =  json.getAsJsonObject();

        ConsentSection.Type type = context.deserialize(
                jsonObject.get(ConsentSection.SECTION_TYPE_GSON), ConsentSection.Type.class);

        // This was a custom ConsentSection Type
        if (type == null) {
            type = ConsentSection.Type.Custom;
            type.setIdentifier(jsonObject.get(ConsentSection.SECTION_TYPE_GSON).getAsString());
        }

        // This will avoid the infinite loop of using the param json context,
        // Since this wont have the ConsentSectionAdapter registered
        Gson gson = new Gson();
        ConsentSection consentSection = gson.fromJson(json, ConsentSection.class);
        consentSection.setType(type);

        return consentSection;
    }
}
