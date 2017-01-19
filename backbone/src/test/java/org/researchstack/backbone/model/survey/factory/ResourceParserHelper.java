package org.researchstack.backbone.model.survey.factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertTrue;

/**
 * Created by TheMDP on 1/6/17.
 */

public class ResourceParserHelper {

    public String getJsonStringForResourceName(String resourceName) {
        // Resources are in src/test/resources
        InputStream jsonStream = getClass().getClassLoader().getResourceAsStream(resourceName+".json");
        String json = convertStreamToString(jsonStream);
        return json;
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            assertTrue("Failed to read stream", false);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                assertTrue("Failed to read stream", false);
            }
        }
        return sb.toString();
    }
}
