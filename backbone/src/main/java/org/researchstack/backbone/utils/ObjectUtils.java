package org.researchstack.backbone.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by TheMDP on 12/29/16.
 */

public class ObjectUtils {

    /*
     * Performs a deep copy on the object of type using Gson
     * Object type must have a default constructor to work properly
     *
     * NOTE: this does not work with Polymorphism yet
     */
    public static <T> T deepCopy(Object object, Class<T> type) {
        return deepCopy(object, type, new Gson());
    }

    /*
    * Performs a deep copy on the object of type using Gson
    * Object type must have a default constructor to work properly
    *
    * NOTE: this does not work with Polymorphism yet
    */
    public static <T> T deepCopy(Object object, Class<T> type, Gson gson) {
        try {
            String copyJson = gson.toJson(object, type);
            return gson.fromJson(copyJson, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
