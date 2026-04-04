package cz.lukaskabc.ontology.ontopus.plugin.widoco.config;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public enum Argument {
    ANALYTICS("analytics", "string"),
    CONF_FILE("confFile", "string"),
    // not including Cross Ref, does not generate index file
    DISPLAY_DIRECT_IMPORTS_ONLY("displayDirectImportsOnly", "boolean"),
    DO_NOT_DISPLAY_SERIALIZATIONS("doNotDisplaySerializations", "boolean"),
    EXCLUDE_INTRODUCTION("excludeIntroduction", "boolean"),
    EXCLUDE_PROVENANCE("excludeProvenance", "boolean"),
    GET_ONTOLOGY_METADATA("getOntologyMetadata", "boolean", true),
    // not including help
    // not including htaccess (unused in ontopus)
    IGNORE_INDIVIDUALS("ignoreIndividuals", "boolean"),
    INCLUDE_ANNOTATION_PROPERTIES("includeAnnotationProperties", "boolean"),
    INCLUDE_IMPORTED_ONTOLOGIES("includeImportedOntologies", "boolean"),
    IMPORT("import", "string"),
    LANG("lang", "array_string"),
    LICENSIUS("licensius", "boolean"),
    NO_PLACEHOLDER_TEXT("noPlaceHolderText", "boolean"),
    ONT_FILE("ontFile", "string"),
    // not including outFolder, overridden by ontopus
    // not including ontURI, ontopus generates docs for local files
    OOPS("oops", "boolean"),
    // not including rewrite all, handled by ontopus
    // not including rewrite base, only for htaccess which is unused
    // not including saveConfig, the file would not be persisted for next call,
    // expected to be provided by user
    UNITE_SECTIONS("uniteSections", "boolean"),
    USE_CUSTOM_STYLE("useCustomStyle", "boolean"),
    WEB_VOWL("webVowl", "boolean");

    public static final Set<Argument> FILES = Set.of(CONF_FILE, ONT_FILE, IMPORT);

    private final String argument;
    /** Type for JSON schema */
    private final String schemaType;

    @Nullable private final String defaultValue;

    Argument(String argument, String schemaType) {
        this(argument, schemaType, null);
    }

    Argument(String argument, String schemaType, @Nullable Object defaultValue) {
        this.argument = argument;
        this.schemaType = schemaType;
        this.defaultValue = defaultValue == null ? null : Objects.toString(defaultValue);
    }

    public String argument() {
        return "-" + argument;
    }

    @Nullable public String defaultValue() {
        return defaultValue;
    }

    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    public String schemaType() {
        return schemaType;
    }

    /**
     * Variable expression using the name of the argument
     *
     * @return String: {@code ${name()}} e.g. {@code ${CONF_FILE}}
     */
    public String variable() {
        return "${" + name() + "}";
    }
}
