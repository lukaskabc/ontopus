package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

public class JsonResourceLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String JSON_SCHEMA_SUFFIX = ".json-schema.json";
    private static final String UI_SCHEMA_SUFFIX = ".ui-schema.json";
    private static final Logger log = LogManager.getLogger(JsonResourceLoader.class);

    public static JsonNode load(String resource) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream(resource)) {
            return objectMapper.readTree(Objects.requireNonNull(is));
        } catch (Exception e) {
            throw log.throwing(new OntopusException("Failed to load resource " + resource, e));
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
