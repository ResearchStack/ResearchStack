package org.researchstack.backbone.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by TheMDP on 12/29/16.
 */

public class ObjectUtils {

    /**
     * @param copyObject the Object to copy, which must implement interface Serializable
     *                   and all classes, subclasses, and member field classes must
     *                   implement a default package-level constructor, or exception will be thrown
     * @return deep object copy
     */
    public static Object clone(Object copyObject) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(copyObject);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object deepCopy = ois.readObject();
            return deepCopy;
        } catch (IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
