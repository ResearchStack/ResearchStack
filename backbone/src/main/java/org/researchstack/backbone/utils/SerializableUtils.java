package org.researchstack.backbone.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * SerializableUtils can be used to save serializable objects to SharedPrefs
 * It is better than using gson because it can properly handle sub-classes
 */

public class SerializableUtils {

    /**
     * Converts a serializable object to a Base64 string that can be stored in SharedPrefs
     * @param object to convert to a base 64 string
     * @return a base 64 string representing the object, null if something went wrong
     */
    public static String toBase64String(Serializable object) {
        String encoded = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        } catch (IOException e) {
            LogExt.e(SerializableUtils.class, e.getLocalizedMessage());
        }
        return encoded;
    }

    /**
     * @param base64String to convert into a serializable object
     * @return the serializable object, or null if something went wrong
     */
    public static Serializable fromBase64String(String base64String) {
        byte[] bytes = Base64.decode(base64String,0);
        Serializable object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream( new ByteArrayInputStream(bytes) );
            object = (Serializable)objectInputStream.readObject();
        } catch (IOException e) {
            LogExt.e(SerializableUtils.class, e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            LogExt.e(SerializableUtils.class, e.getLocalizedMessage());
        } catch (ClassCastException e) {
            LogExt.e(SerializableUtils.class, e.getLocalizedMessage());
        }
        return object;
    }
}
