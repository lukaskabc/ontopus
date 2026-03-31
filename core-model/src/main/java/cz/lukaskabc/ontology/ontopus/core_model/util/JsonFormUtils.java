package cz.lukaskabc.ontology.ontopus.core_model.util;

import java.io.File;
import java.nio.file.Path;

public class JsonFormUtils {
    public static String schemaTypeForClass(Class<?> clazz) {
        if (clazz == String.class || clazz == File.class || clazz == Path.class) {
            return "string";
        } else if (clazz == Integer.class || clazz == int.class) {
            return "integer";
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return "boolean";
        } else if (clazz == Integer.class
                || clazz == int.class
                || clazz == Double.class
                || clazz == double.class
                || clazz == Float.class
                || clazz == float.class) {
            return "number";
        } else {
            throw new IllegalArgumentException("Unsupported class for JSON schema type: " + clazz.getName());
        }
    }

    private JsonFormUtils() {
        throw new AssertionError();
    }
}
