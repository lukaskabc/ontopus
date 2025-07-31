package cz.lukaskabc.ontology.ontopus.api.util;

import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class DataHelper {

    @Nullable public static String getStringValue(Map<String, String[]> data, String parameter) {
        String[] values = data.get(parameter);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    private DataHelper() {
        throw new AssertionError();
    }
}
