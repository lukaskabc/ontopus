package cz.lukaskabc.ontology.ontopus.api.util;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

public class JsonResourceLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String JSON_SCHEMA_SUFFIX = ".json-schema.json";
    private static final String UI_SCHEMA_SUFFIX = ".ui-schema.json";

    public static JsonNode load(String resource) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream(resource)) {
            return objectMapper.readTree(Objects.requireNonNull(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode loadJsonSchema(String directory, String baseName) {
        return load(Path.of(directory, baseName + JSON_SCHEMA_SUFFIX).toString());
    }

    public static JsonNode loadUiSchema(String directory, String baseName) {
        return load(Path.of(directory, baseName + UI_SCHEMA_SUFFIX).toString());
    }

    private JsonResourceLoader() {
        throw new AssertionError();
    }
}
