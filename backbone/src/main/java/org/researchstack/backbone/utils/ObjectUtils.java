package org.researchstack.backbone.utils;

import com.google.gson.Gson;

/**
 * Created by TheMDP on 12/29/16.
 */

public class ObjectUtils {

    /*
     * Performs a deep copy on the object of type using Gson
     * Object type must have a default constructor to work properly
     */
    public static <T> T deepCopy(T object, Class<T> type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJson(object, type), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
