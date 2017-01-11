package org.researchstack.backbone.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.ObjectUtils;
import org.researchstack.backbone.utils.RuntimeTypeAdapterFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 1/7/17.
 *
 * The idea behind this is to be able to serialize/deserialize sub-classes automatically in gson
 * Out of the box, this is not supported, so we must handle the generic case ourselves
 *
 * In short, we use a key "class" to store the lowest subclass of the base class
 * That way, when we deserialize, it will be able to use the correct Type
 *
 * Note, if an object that extends GsonSerializablePolymorphism has member variables
 * that also extend from GsonSerializablePolymorphism, all the GsonBasSubClassPairs must include them
 */

public abstract class GsonSerializablePolymorphism<T> {

    public GsonSerializablePolymorphism() { super(); }

    /*
     * THE ORDER OF THE DATA PAIRS IS VERY IMPORTANT
     *
     * The deepest nested GsonSerializablePolymorphism class pair must go first,
     * With your base class last
     *
     * The base/sub class pairs to use in the automatic polymorphism serialization/deserialization
     * For example, if you have a base class Shape.class, and a Triangle.class that extends Shape.class,
     * You would do...
     * List A = new List { new GsonBaseSubClassPair(Shape.class, TriangleObj.getClass()) }
     * return new Data(Shape.class, A);
     *
     * If Shape.class has member which LineType.class base, and its of subclass type BigLineType.class
     * You would return something like this,
     * List A = new List { new GsonSerializablePolymorphism.DataPair(LineType.class, BigLineTypeObj.getClass()) }
     * A.add(new GsonSerializablePolymorphism.DataPair(Shape.class, TriangleObj.getClass()));
     * return new Data(Shape.class, A);
     */
    public abstract Data<T> getPolymorphismData();

    public T deepCopy() {
        Data<T> data = getPolymorphismData();
        Gson gson = new Gson();
        for (DataPair pair : data.baseSubClassPairs) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            TypeAdapter typeAdapter = createTypeAdapter(gson, pair);
            if (typeAdapter != null) {
                gsonBuilder.registerTypeAdapter(pair.baseClass, typeAdapter);
            }
            gson = gsonBuilder.create();
        }

        // Now make the type adpater for the base most class
        return ObjectUtils.deepCopy(this, data.baseClass, gson);
    }

    TypeAdapter createTypeAdapter(Gson gson, DataPair pair) {
        if (pair.baseClass == pair.subClass) {
            return null;   // no type adapter needed
        }
        // "class" just cant be the name of a SerializedName in this class, which it is unlikely it would
        RuntimeTypeAdapterFactory typeFactory = RuntimeTypeAdapterFactory.of(pair.baseClass, "class");
        // If these are the same, the serializer/deserializer will enter an infinite loop
        typeFactory = typeFactory.registerSubtype(pair.subClass);
        return typeFactory.create(gson, TypeToken.get(pair.baseClass));
    }

    /**
     * Used to help GsonSerializablePolymorphism
     */

     /*
     * THE ORDER OF THE DATA PAIRS IS VERY IMPORTANT
     *
     * The deepest nested GsonSerializablePolymorphism class pair must go first,
     * With your base class last
     */
    public static class Data<T> {
        public Class<? extends T>   baseClass;
        public List<DataPair>       baseSubClassPairs;
        public Data(Class<? extends T> baseClass, List<DataPair> baseSubClassPairs) {
            this.baseClass = baseClass;
            this.baseSubClassPairs  = baseSubClassPairs;
        }
    }

    public static class DataPair {
        public Class<?> baseClass;
        public Class<?> subClass;
        public DataPair(Class<?> baseClass, Class<?> subClass) {
            this.baseClass = baseClass;
            this.subClass  = subClass;
        }
    }
}
