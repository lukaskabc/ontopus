package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

/** Helps with loading JSON files from the classpath resource directory. */
public class JsonResourceLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String JSON_SCHEMA_SUFFIX = ".json-schema.json";
    private static final String UI_SCHEMA_SUFFIX = ".ui-schema.json";
    private static final Logger log = LogManager.getLogger(JsonResourceLoader.class);

    /**
     * Loads a JSON file from the classpath.
     *
     * <p>Expected path: classpath-relative resource path using '/' separators (for example: {@code schemas/user.json}).
     *
     * @param resource classpath resource path including file name and extension
     * @return parsed JSON content
     * @throws OntopusException if the resource is missing or cannot be parsed
     */
    public static JsonNode load(String resource) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream(resource)) {
            return objectMapper.readTree(Objects.requireNonNull(is));
        } catch (Exception e) {
            throw log.throwing(new InternalException("Failed to load resource " + resource, e));
        }
    }

    /**
     * Loads a JSON schema file from the classpath.
     *
     * <p>Expected path pattern: {@code <directory>/<baseName>.json-schema.json}.
     *
     * @param directory classpath directory containing schema files
     * @param baseName schema file base name without suffix
     * @return parsed JSON schema
     * @throws OntopusException if the resource is missing or cannot be parsed
     */
    public static JsonNode loadJsonSchema(String directory, String baseName) {
        return load(Path.of(directory, baseName + JSON_SCHEMA_SUFFIX).toString());
    }

    /**
     * Loads a UI schema file from the classpath.
     *
     * <p>Expected path pattern: {@code <directory>/<baseName>.ui-schema.json}.
     *
     * @param directory classpath directory containing UI schema files
     * @param baseName UI schema file base name without suffix
     * @return parsed UI schema
     * @throws OntopusException if the resource is missing or cannot be parsed
     */
    public static JsonNode loadUiSchema(String directory, String baseName) {
        return load(Path.of(directory, baseName + UI_SCHEMA_SUFFIX).toString());
    }

    private JsonResourceLoader() {
        throw new AssertionError();
    }
}
